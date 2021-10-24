package crocodile8008.videoviewcache.lib

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.VideoView
import crocodile8008.videoviewcache.lib.data.VideoViewCache
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * [VideoView] that wrapped into frame and supports loading with caching.
 * Also it has a progress bar and auto scaling of video.
 */
class VideoViewCached : FrameLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    lateinit var videoView: VideoView
        private set

    lateinit var progressBar: ProgressBar
        private set

    var mediaPlayer: MediaPlayer? = null
        private set

    var autoScale = true

    var mpPreparedListener: MediaPlayer.OnPreparedListener? = null
    var mpErrorListener: MediaPlayer.OnErrorListener? = null
    var commonErrorListener: (t: Throwable) -> Unit = {}

    private var playCalled = false
    private var videoToLoad: VideoRequestParam? = null
    private var isLoading = false
    private val disposables = CompositeDisposable()

    private fun init() {
        val tmpFrame = LayoutInflater.from(context)
            .inflate(R.layout.cached_video_view, this, false) as ViewGroup
        videoView = tmpFrame.findViewById(R.id.cached_video_view_video)
        progressBar = tmpFrame.findViewById(R.id.cached_video_view_pb)
        tmpFrame.removeAllViews()
        addView(videoView)
        addView(progressBar)

        videoView.setOnPreparedListener { mp: MediaPlayer ->
            if (videoToLoad == null) {
                progressBar.visibility = GONE
            }
            mediaPlayer = mp
            if (autoScale) {
                post {
                    // At this moment it's not working:
                    // mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                    // So scale video view manually
                    try {
                        val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
                        val frameRatio = width / height.toFloat()
                        var scaleXNew = videoRatio / frameRatio

                        // Sometimes there is 1px missing so scale little more
                        if (scaleXNew > 1f) {
                            scaleXNew += 0.001f
                        } else if (scaleXNew < 1f) {
                            scaleXNew -= 0.001f
                        }
                        log(
                            "scale x: $scaleXNew" +
                                    ". video: ${mp.videoWidth} / ${mp.videoHeight} ($videoRatio)" +
                                    ". frame: $width / $height ($frameRatio)"
                        )
                        if (scaleXNew >= 1f) {
                            videoView.scaleY = scaleXNew
                        } else {
                            videoView.scaleX = 1f / scaleXNew
                        }
                    } catch (t: Throwable) {
                        commonErrorListener(t)
                    }
                }
            }
            mpPreparedListener?.onPrepared(mp)
        }

        videoView.setOnErrorListener { mp: MediaPlayer, what: Int, extra: Int ->
            if (!isLoading) {
                progressBar.visibility = GONE
            }
            mpErrorListener?.onError(mp, what, extra)
            true
        }
    }

    /**
     * Start loading with caching and playing.
     *
     * Note: loading will be stopped in [View.OnAttachStateChangeListener.onViewDetachedFromWindow]
     * and resumed in [View.OnAttachStateChangeListener.onViewAttachedToWindow].
     */
    @Suppress("Unused")
    fun playUrl(url: String, headers: Map<String, String>? = null) {
        log("playUrl: $url")
        disposables.clear()
        progressBar.visibility = VISIBLE
        videoToLoad = VideoRequestParam(url, headers)
        loadVideoIfHasToLoad()
        playCalled = true
    }

    /**
     * Stop loading and playing.
     */
    @Suppress("Unused")
    fun stop() {
        log("stop: ${videoToLoad?.url}")
        disposables.clear()
        videoView.stopPlayback()
        // VideoView may play previous video so it helps to reset.
        videoView.setBackgroundColor(Color.BLACK)
        playCalled = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        loadVideoIfHasToLoad()
        if (playCalled) {
            videoView.start()
        }
    }

    override fun onDetachedFromWindow() {
        disposables.clear()
        if (videoView.isPlaying) {
            videoView.stopPlayback()
        }
        super.onDetachedFromWindow()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus && playCalled && !videoView.isPlaying) {
            videoView.start()
        }
    }

    private fun loadVideoIfHasToLoad() {
        if (isLoading) {
            return
        }
        videoToLoad?.let {
            isLoading = true
            progressBar.visibility = VISIBLE
            val disposable = VideoViewCache
                .loadInFileCached(it.url, it.headers, context)
                .doFinally {
                    isLoading = false
                }
                .subscribe(
                    { filePath ->
                        if (filePath.isEmpty()) {
                            return@subscribe
                        }
                        videoToLoad = null
                        videoView.setVideoPath(filePath)
                        videoView.start()
                        videoView.background = null
                    },
                    { t ->
                        progressBar.visibility = GONE
                        log("$t")
                        commonErrorListener(t)
                    }
                )
            disposables.add(disposable)
        }
    }
}

internal data class VideoRequestParam(
    val url: String,
    val headers: Map<String, String>?,
)