package local.intranet.bttf.api.info

/**
 *
 * https://hackmd.io/@pierodibello/ByEVNHg-v
 *
 * @constructor with parameters
 *
 * @param key                 {@link String}
 * @param value               {@link Int}
 * @param maxDurationInMillis {@link Long}
 */ 
public data class TimedEntry(val key: String, var value: Int, val maxDurationInMillis: Long) {
    
    public var creationTime: Long = now()

    /**
     * Is Expired
     *
     * @return {@link Boolean}
     */
    public fun isExpired() = (now() - creationTime) > maxDurationInMillis
        
    private fun now() = System.currentTimeMillis()
    
}