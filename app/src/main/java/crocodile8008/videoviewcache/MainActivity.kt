package crocodile8008.videoviewcache

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.VideoView
import crocodile8008.videoviewcache.lib.VideoViewCacheFacade
import crocodile8008.videoviewcache.lib.VideoViewCached
import crocodile8008.videoviewcache.lib.playUrl

class MainActivity : AppCompatActivity() {

    private lateinit var cachedVideoView: VideoViewCached
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cachedVideoView = findViewById(R.id.cachedVideoView)
        videoView = findViewById(R.id.videoView)

        VideoViewCacheFacade.logEnabled = true

        cachedVideoView.mpPreparedListener = MediaPlayer.OnPreparedListener {
            it.isLooping = true
        }
        cachedVideoView.playUrl("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_1920_18MG.mp4")

        videoView.setOnPreparedListener {
            it.isLooping = true
        }
        videoView.playUrl("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_1280_10MG.mp4")
    }
}