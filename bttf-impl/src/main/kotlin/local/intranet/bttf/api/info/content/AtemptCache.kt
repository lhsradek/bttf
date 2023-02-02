package local.intranet.bttf.api.info.content

import local.intranet.bttf.api.info.TimedEntry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import org.jetbrains.annotations.NotNull

/**
 * inspired by
 * https://hackmd.io/@pierodibello/ByEVNHg-v
 */
class AttemptCache() : LoginCache {

    private val hashMap = ConcurrentHashMap<String, TimedEntry>()
    private var cacheTimeValidityInMillis: Long = 0
    private var printBlocked: Boolean = false
    private var maxAtempt: Int = 0

    /**
     * It doesn't have to exist, if exist return number of attempts
     *
     * @param key {@link String}
     *
     * @return {@link Int?}
     */
    override fun getById(@NotNull key: String): Int? {
        val timedEntry = hashMap[key]
        if (timedEntry == null || timedEntry.isExpired()) {
            return null
        }
        return timedEntry.value
    }

    override fun keyToCache(@NotNull key: String) {
        if (hashMap.containsKey(key)) {
            val timedEntry = hashMap[key]
            timedEntry?.let {
                timedEntry.value++
                timedEntry.creationTime = System.currentTimeMillis()
            }
        } else {
            hashMap.put(key, TimedEntry(key, 1, cacheTimeValidityInMillis))
        }
    }

    override fun invalidateKey(@NotNull key: String) {
        if (hashMap.containsKey(key)) {
            hashMap.remove(key)
        }
    }

    override fun isBlocked(@NotNull key: String): Boolean {
        val ret: Boolean
        if (hashMap.containsKey(key)) {
            val timedEntry = hashMap[key]
            ret = timedEntry?.let {
                if (timedEntry.isExpired()) {
                    invalidateKey(key)
                    false
                } else if (timedEntry.value >= maxAtempt) {
                    true  // is blocked !!!
                } else {
                    false
                }
            } ?: false
        } else {
            ret = false
        }
        return ret
    }

    override fun getCache(@NotNull printBlocked: Boolean): List<Triple<String, Int, ZonedDateTime>> {
        val ret = mutableListOf<Triple<String, Int, ZonedDateTime>>()
        if (printBlocked) {
            hashMap.filter { b -> isBlocked(b.key) == true }.forEach {
                val timedEntry = it.value
                ret.add(Triple(it.key, timedEntry.value, ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(timedEntry.creationTime), ZoneId.systemDefault())))
            }
        } else {
            hashMap.filter { b -> isBlocked(b.key) == false }.forEach {
                val timedEntry = it.value
                ret.add(Triple(it.key, timedEntry.value, ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(timedEntry.creationTime), ZoneId.systemDefault())))
            }
        }
        return ret
    }

    fun removeExpiredKeys() {
        for (item in hashMap) {
            val timedEntry = item.value
            if (timedEntry.isExpired()) {
                hashMap.remove(item.key)
            }
        }
    }

    companion object {

        @JvmStatic
        fun init(duration: Long, timeUnit: TimeUnit, printBlocked: Boolean, maxAtempt: Int) =
            AttemptCache().apply {
                cacheTimeValidityInMillis = MILLISECONDS.convert(duration, timeUnit)
                this.printBlocked = printBlocked
                this.maxAtempt = maxAtempt
            }

    }
}

