package local.intranet.bttf.api.info

/**
 *
 * https://hackmd.io/@pierodibello/ByEVNHg-v
 *
 * @constructor with parameters
 */ 
public data class TimedEntry(val key: String, var value: Int, val maxDurationInMillis: Long) {
    
    public var creationTime: Long = now()

    public fun isExpired() = (now() - creationTime) > maxDurationInMillis
        
    private fun now() = System.currentTimeMillis()
    
}