package crocodile8008.videoviewcache

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.VideoView
import crocodile8008.videoviewcache.lib.VideoViewCacheFacade
import crocodile8008.videoviewcache.lib.VideoViewCached
import crocodile8008.videoviewcache.lib.playUrl
import crocodile8008.videoviewcache.lib.stop
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var cachedVideoView: VideoViewCached
    private lateinit var videoView: VideoView
    private lateinit var btnClean: View
    private lateinit var btnRecycler: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cachedVideoView = findViewById(R.id.cachedVideoView)
        videoView = findViewById(R.id.videoView)
        btnClean = findViewById(R.id.btnClean)
        btnRecycler = findViewById(R.id.btnRecycler)

        VideoViewCacheFacade.logEnabled = BuildConfig.DEBUG

        cachedVideoView.apply {
            mpPreparedListener = MediaPlayer.OnPreparedListener {
                it.isLooping = true
            }
            cachedVideoView.playUrl("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_1920_18MG.mp4")
        }

        videoView.apply {
            setOnPreparedListener {
                it.isLooping = true
            }
            videoView.playUrl("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_1280_10MG.mp4")
        }

        btnClean.setOnClickListener {
            cachedVideoView.stop()
            videoView.stop()

            val deleted = File(VideoViewCacheFacade.getOutputDirPath(this)).deleteRecursively()
            Toast.makeText(this, "deleted: $deleted", Toast.LENGTH_SHORT).show()

            cachedVideoView.playUrl("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_1920_18MG.mp4")
            videoView.playUrl("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_1280_10MG.mp4")
        }

        btnRecycler.setOnClickListener {
            startActivity(Intent(this, RecyclerActivity::class.java))
        }
    }
}