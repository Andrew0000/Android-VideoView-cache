package crocodile8008.videoviewcache.lib.utils

import android.util.Log
import crocodile8008.videoviewcache.lib.VideoViewCacheFacade
import java.io.Closeable
import java.io.File

internal const val LOG_TAG = "[VideoViewCache]"

internal fun Closeable?.closeSilent() {
    try {
        this?.close()
    } catch (e: Exception) {
        // Ignore
    }
}

internal fun log(msg: String) {
    if (VideoViewCacheFacade.logEnabled) {
        Log.v(LOG_TAG, msg)
    }
}

internal fun File.tryDelete(): Boolean =
    tryDeleteFile(this)

private fun tryDeleteFile(file: File): Boolean {
    try {
        if (file.exists()) {
            val deleted = file.delete()
            log("delete file: $deleted, ${file.name}")
            return deleted
        }
    } catch (e: Exception) {
        log("delete file error: $e")
    }
    return false
}
