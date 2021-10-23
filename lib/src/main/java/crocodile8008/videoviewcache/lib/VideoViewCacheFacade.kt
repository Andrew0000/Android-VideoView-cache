@file: Suppress("Unused")

package crocodile8008.videoviewcache.lib

import android.content.Context
import crocodile8008.videoviewcache.lib.data.VideoViewCache
import java.io.File
import java.io.IOException

object VideoViewCacheFacade {

    var logEnabled: Boolean = false

    @Throws(IOException::class)
    fun cleanCacheFor(url: String, context: Context): Boolean =
        VideoViewCache.tryDeleteFile(
            VideoViewCache.getOutputFile(url, context)
        )

    @Throws(IOException::class)
    fun getOutputFile(url: String, context: Context): File = VideoViewCache.getOutputFile(url, context)

    fun getOutputDirPath(context: Context): String = VideoViewCache.getOutputDirPath(context)
}