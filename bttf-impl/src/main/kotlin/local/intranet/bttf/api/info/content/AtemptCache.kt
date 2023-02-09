package local.intranet.bttf.api.info.content

import local.intranet.bttf.api.info.TimedEntry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import org.jetbrains.annotations.NotNull
import org.slf4j.LoggerFactory

/**
 * inspired by
 * https://hackmd.io/@pierodibello/ByEVNHg-v
 */
public class AttemptCache() : LoginCache {

    private val log = LoggerFactory.getLogger(javaClass)

    private val hashMap = ConcurrentHashMap<String, TimedEntry>()
    private var cacheTimeValidityInMillis: Long = 0
    private var printBlocked: Boolean = false
    private var maxAtempt: Int = 0

    public companion object {

        /**
         *
         * Init for {@link local.intranet.bttf.api.service.LoginAttemptService#init}
         *
         * @param duration      {@link Long}
         * @param timeUnit      {@link TimeUnit}
         * @param printBlocked  {@link Boolean}
         * @param maxAtempt     {@link Int}
         */
        @JvmStatic
        public fun init(duration: Long, timeUnit: TimeUnit, printBlocked: Boolean, maxAtempt: Int) =
            AttemptCache().apply {
                cacheTimeValidityInMillis = MILLISECONDS.convert(duration, timeUnit)
                this.printBlocked = printBlocked
                this.maxAtempt = maxAtempt
            }
    }

    /**
     * It doesn't have to exist, if exist return number of attempts
     *
     * @param key {@link String}
     *
     * @return {@link Int?}
     */
    public override fun getById(@NotNull key: String): Int? {
        val timedEntry = hashMap[key]
        val ret: Int? = timedEntry?.let {
            with(timedEntry) {
                if (isExpired()) {
                    null
                } else {
                    value
                }
            }
        }
        return ret
    }

    public override fun keyToCache(@NotNull key: String) {
        if (hashMap.containsKey(key)) {
            val timedEntry = hashMap[key]
            timedEntry?.let {
                with(timedEntry) {
                    value++
                    creationTime = System.currentTimeMillis()
                }
            }
        } else {
            hashMap.put(key, TimedEntry(key, 1, cacheTimeValidityInMillis))
        }
    }

    public override fun invalidateKey(@NotNull key: String) {
        if (hashMap.containsKey(key)) {
            hashMap.remove(key)
        }
    }

    public override fun isBlocked(@NotNull key: String): Boolean {
        val ret: Boolean
        if (hashMap.containsKey(key)) {
            val timedEntry = hashMap[key]
            ret = timedEntry?.let {
                with(timedEntry) {
                    if (isExpired()) {
                        invalidateKey(key)
                        false
                    } else if (value >= maxAtempt) {
                        true  // is blocked !!!
                    } else {
                        false
                    }
                }
            } ?: false
        } else {
            ret = false
        }
        return ret
    }

    public override fun getCache(@NotNull printBlocked: Boolean): List<Triple<String, Int, ZonedDateTime>> {
        val ret = mutableListOf<Triple<String, Int, ZonedDateTime>>()
        if (printBlocked) {
            hashMap.filter { b -> isBlocked(b.key) == true }.forEach {
                val timedEntry = it.value
                ret.add(
                    Triple(
                        it.key, timedEntry.value,
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(timedEntry.creationTime), ZoneId.systemDefault()
                        )
                    )
                )
            }
        } else {
            hashMap.filter { b -> isBlocked(b.key) == false }.forEach {
                val timedEntry = it.value
                ret.add(
                    Triple(
                        it.key, timedEntry.value,
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(timedEntry.creationTime), ZoneId.systemDefault()
                        )
                    )
                )
            }
        }
        return ret
    }

    public fun removeExpiredKeys() {
        var i = 0;
        for (item in hashMap) {
            val timedEntry = item.value
            if (timedEntry.isExpired()) {
                i++
                hashMap.remove(item.key)
            }
        }
        if (i > 0) {
            log.info("RemoveExpiredKeys count:{}", i)
        }
    }

}

