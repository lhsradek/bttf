package local.intranet.bttf.api.info.content

import local.intranet.bttf.api.info.AttemptInfo
import local.intranet.bttf.api.info.TimedEntry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicInteger
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
    private var maxAtempt: Int = 0
    private var isInvalidateKey: Boolean = true

    public companion object {

        /**
         *
         * Init for {@link local.intranet.bttf.api.service.LoginAttemptService#init}
         *
         * @param duration        {@link Long}
         * @param timeUnit        {@link TimeUnit}
         * @param maxAtempt       {@link Int}
         * @param isInvalidateKey {@link Boolean}
         */
        @JvmStatic
        public fun init(duration: Long, timeUnit: TimeUnit, maxAtempt: Int, isInvalidateKey: Boolean) =
            AttemptCache().apply {
                cacheTimeValidityInMillis = MILLISECONDS.convert(duration, timeUnit)
                this.maxAtempt = maxAtempt
                this.isInvalidateKey = isInvalidateKey
            }
    }

    /**
     * It doesn't have to exist, if exist return number of attempts
     *
     * implements {@link LoginCache#getById}
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

    /**
     *
     * implements {@link LoginCache#keyToCache}
     *
     */
    public override fun keyToCache(@NotNull key: String) {
        if (hashMap.containsKey(key)) {
            val timedEntry = hashMap[key]
            timedEntry?.let {
                with(timedEntry) {
                    value++
                    creationTime = System.currentTimeMillis()
                    log.debug("KeyToCache key:'{}' value:{}", key, value)
                }
            }
        } else {
            hashMap.put(key, TimedEntry(key, 1, cacheTimeValidityInMillis))
            log.debug("KeyToCache key:'{}'", key)
        }
    }

    /**
     *
     * implements {@link LoginCache#invalidateKey}
     *
     * Scheduler invalidates all keys by {@link #removeExpiredKeys} if isInvalidateKey == false
     */
    public override fun invalidateKey(@NotNull key: String) {
        if (isInvalidateKey) {
        	if (hashMap.containsKey(key)) {
        		hashMap.remove(key)
                log.debug("InvalidateKey key:'{}'", key)
            }
        }
    }

    /**
     *
     * implements {@link LoginCache#isBlocked}
     *
     */
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
                        
                    	log.debug("IsBlocked key:'{}'", key)
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

    /**
     *
     * Get Cache implements {@link LoginCache#getCache}
     *
     * @param printBlocked {@link Boolean?} as filter if not null
     * @return {@link List}&lt;{@link AttemptInfo}&gt;
     */
    public override fun getCache(printBlocked: Boolean?): List<AttemptInfo> {
        val ret = mutableListOf<AttemptInfo>()
        with (printBlocked?.let {
            hashMap.filter { isBlocked(it.key) == printBlocked }
        } ?: hashMap
        ) {
            forEach {
                val timedEntry = it.value
                ret.add(
                    AttemptInfo(
                        timedEntry.key,
                        timedEntry.value,
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
        val i = AtomicInteger()
        for (item in hashMap) {
            val timedEntry = item.value
            if (timedEntry.isExpired()) {
                i.incrementAndGet()
                hashMap.remove(item.key)
            }
        }
        if (i.get() > 0) {
            log.debug("RemoveExpiredKeys count:{}", i.get())
        }
    }

}

