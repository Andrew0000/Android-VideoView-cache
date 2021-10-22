package crocodile8008.videoviewcache

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import crocodile8008.videoviewcache.lib.VideoViewCacheFacade
import crocodile8008.videoviewcache.lib.VideoViewCached

class MainActivity : AppCompatActivity() {

    private lateinit var cachedVideoView: VideoViewCached
    private lateinit var cachedVideoView2: VideoViewCached

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cachedVideoView = findViewById(R.id.cachedVideoView)
        cachedVideoView2 = findViewById(R.id.cachedVideoView2)

        VideoViewCacheFacade.logEnabled = true

        cachedVideoView.mpPreparedListener = MediaPlayer.OnPreparedListener {
            it.isLooping = true
        }
        cachedVideoView.playUrl("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_640_3MG.mp4")

        cachedVideoView2.mpPreparedListener = MediaPlayer.OnPreparedListener {
            it.isLooping = true
        }
        cachedVideoView2.playUrl("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_480_1_5MG.mp4")
    }
}