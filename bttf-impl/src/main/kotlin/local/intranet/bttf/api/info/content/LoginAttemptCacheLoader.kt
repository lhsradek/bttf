package local.intranet.bttf.api.info.content

import com.google.common.cache.CacheLoader

/**
 *
 * {@link LoginAttemptCacheLoader} for
 * {@link local.intranet.bttf.api.service.LoginAttemptService}
 *
 * @author Radek Kádner
 */
class LoginAttemptCacheLoader<K, V> : CacheLoader<String, Int>() {

    /**
     *
     * Computes or retrieves the value corresponding to {@code key}.
     */
    @Throws(Exception::class)
    override fun load(key: String): Int {
        return 0
    }

}
