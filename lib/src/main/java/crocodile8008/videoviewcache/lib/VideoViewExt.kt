package crocodile8008.videoviewcache.lib

import android.view.View
import android.view.ViewTreeObserver
import android.widget.VideoView
import crocodile8008.videoviewcache.lib.data.VideoViewCache
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*

private val attachedLoaders by lazy {
    WeakHashMap<VideoView, LoaderData>()
}

/**
 * Start loading with caching and playing.
 *
 * Note: loading will be stopped in [View.OnAttachStateChangeListener.onViewDetachedFromWindow]
 * and resumed in [View.OnAttachStateChangeListener.onViewAttachedToWindow].
 */
@Suppress("Unused")
fun VideoView.playUrl(url: String, headers: Map<String, String>? = null) {
    var loader = attachedLoaders[this]
    log("[ext] playUrl: $loader, $url")
    if (loader == null) {
        loader = LoaderData(this)
        attachedLoaders[this] = loader

        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

            override fun onViewAttachedToWindow(v: View?) {
                loader.onWindowFocusChangeListener?.let {
                    viewTreeObserver.addOnWindowFocusChangeListener(it)
                }
                loader.loadVideoIfHasToLoad()
                if (loader.playCalled) {
                    loader.videoView.start()
                }
            }

            override fun onViewDetachedFromWindow(v: View?) {
                loader.onWindowFocusChangeListener?.let {
                    viewTreeObserver.removeOnWindowFocusChangeListener(it)
                }
                loader.disposables.clear()
                if (loader.videoView.isPlaying) {
                    loader.videoView.stopPlayback()
                }
            }
        })

        loader.onWindowFocusChangeListener =
            ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
                if (hasFocus && loader.playCalled && !loader.videoView.isPlaying) {
                    loader.videoView.start()
                }
            }
        if (isAttachedToWindow) {
            loader.onWindowFocusChangeListener?.let {
                viewTreeObserver.addOnWindowFocusChangeListener(it)
            }
        }
    }

    loader.apply {
        disposables.clear()
        videoToLoad = VideoRequestParam(url, headers)
        loadVideoIfHasToLoad()
        playCalled = true
    }
}

/**
 * Stop loading and playing.
 */
@Suppress("Unused")
fun VideoView.stop() {
    val loader = attachedLoaders[this]
    log("[ext] stop for: $loader")
    if (loader == null) {
        return
    }
    loader.disposables.clear()
    loader.videoView.stopPlayback()
    loader.playCalled = false
}

private class LoaderData(
    val videoView: VideoView,
) {
    var playCalled = false
    var videoToLoad: VideoRequestParam? = null
    var isLoading = false
    val disposables = CompositeDisposable()
    var onWindowFocusChangeListener: ViewTreeObserver.OnWindowFocusChangeListener? = null

    fun loadVideoIfHasToLoad() {
        if (isLoading) {
            return
        }
        videoToLoad?.let {
            isLoading = true
            val disposable = VideoViewCache
                .loadInFileCached(it.url, it.headers, videoView.context)
                .doFinally {
                    isLoading = false
                }
                .subscribe(
                    { filePath ->
                        if (filePath.isEmpty()) {
                            return@subscribe
                        }
                        videoView.setVideoPath(filePath)
                        videoView.start()
                        videoToLoad = null
                    },
                    { t ->
                        log("$t")
                    }
                )
            disposables.add(disposable)
        }
    }
}
