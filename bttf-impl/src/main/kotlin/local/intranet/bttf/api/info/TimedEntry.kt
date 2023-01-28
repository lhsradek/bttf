package local.intranet.bttf.api.info

/**
 *
 * https://hackmd.io/@pierodibello/ByEVNHg-v
 */ 
data class TimedEntry(val key: String, var value: Int, val maxDurationInMillis: Long) {
    
    fun isExpired() = (now() - creationTime) > maxDurationInMillis
    
    val creationTime: Long = now()
    
    private fun now() = System.currentTimeMillis()
    
}