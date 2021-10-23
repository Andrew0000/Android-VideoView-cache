package crocodile8008.videoviewcache

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate
import crocodile8008.videoviewcache.lib.VideoViewCached
import crocodile8008.videoviewcache.lib.playUrl
import crocodile8008.videoviewcache.lib.stop

class RecyclerActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView

    private val adapter by lazy {
        ListDelegationAdapter(
            createVideoViewAdapterDelegate(),
            createVideoViewCachedAdapterDelegate(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)
        recycler = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recycler.adapter = adapter

        adapter.items = listOf(
            UrlForVideoView("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_480_1_5MG.mp4"),
            UrlForVideoViewCached("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_640_3MG.mp4"),
            UrlForVideoView("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_1280_10MG.mp4"),
            UrlForVideoViewCached("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_1920_18MG.mp4"),
            UrlForVideoView("https://download.samplelib.com/mp4/sample-5s.mp4"),
            UrlForVideoViewCached("https://download.samplelib.com/mp4/sample-10s.mp4"),
            UrlForVideoView("https://download.samplelib.com/mp4/sample-15s.mp4"),
            UrlForVideoViewCached("https://download.samplelib.com/mp4/sample-30s.mp4"),
        )
    }

    private fun createVideoViewAdapterDelegate() = adapterDelegate<UrlForVideoView, Any>(R.layout.item_video_view) {
        val videoView : VideoView = findViewById(R.id.itemVideoView)
        val numTextView : TextView = findViewById(R.id.numTextView)
        bind {
            videoView.stop()
            videoView.playUrl(item.url)
            numTextView.text = (adapterPosition + 1).toString()
        }
    }

    private fun createVideoViewCachedAdapterDelegate() = adapterDelegate<UrlForVideoViewCached, Any>(R.layout.item_video_view_cached) {
        val itemVideoViewCached : VideoViewCached = findViewById(R.id.itemVideoViewCached)
        val numTextView : TextView = findViewById(R.id.numTextView)
        bind {
            itemVideoViewCached.stop()
            itemVideoViewCached.playUrl(item.url)
            numTextView.text = (adapterPosition + 1).toString()
        }
    }
}

private data class UrlForVideoView(
    val url: String,
)

private data class UrlForVideoViewCached(
    val url: String,
)