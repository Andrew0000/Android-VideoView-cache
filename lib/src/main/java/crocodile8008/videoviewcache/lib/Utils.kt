package crocodile8008.videoviewcache.lib

import android.util.Log
import java.io.Closeable

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