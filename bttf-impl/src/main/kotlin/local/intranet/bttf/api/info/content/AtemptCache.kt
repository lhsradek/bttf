package local.intranet.bttf.api.info.content

import local.intranet.bttf.api.info.TimedEntry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS
import local.intranet.bttf.api.info.content.LoginCache
import org.jetbrains.annotations.NotNull

/**
 *
 * https://hackmd.io/@pierodibello/ByEVNHg-v
 */ 
class AttemptCache() : LoginCache {

    private val hashMap = ConcurrentHashMap<String, TimedEntry>()
    private var cacheTimeValidityInMillis: Long = 0
    private var printBlocked: Boolean = false

    override fun getById(@NotNull key: String): Int? {
        val timedEntry = hashMap[key]
        if (timedEntry == null || timedEntry.isExpired()) {
            return null
        }
        return timedEntry.value
    }

    override fun keyToCache(@NotNull key: String) {
        hashMap[key]?.let {
            hashMap[key]!!.value++
        }?: TimedEntry(key, 1, cacheTimeValidityInMillis)
    }
    
    //TODO    
    override fun invalidateKey(@NotNull key: String) {
    }

    //TODO   
    override fun isBlocked(@NotNull key: String): Boolean? {
        return false
    }
    
    override fun getCache(@NotNull printBlocked: Boolean): List<Triple<String, Int, Long>> {
        val ret = mutableListOf<Triple<String, Int, Long>>()
        hashMap.forEach {
            hashMap[it.key]?.let{
                ret.add(Triple(it.key, it.value, it.creationTime))
            }
        }
        return ret
    }
    
    companion object {
        
        @JvmStatic
        fun init(duration: Long, timeUnit: TimeUnit, printBlocked: Boolean) = AttemptCache().apply {
            cacheTimeValidityInMillis = MILLISECONDS.convert(duration, timeUnit)
            this.printBlocked = printBlocked
        }
    }

}

