package local.intranet.bttf.api.info

/**
 *
 * https://hackmd.io/@pierodibello/ByEVNHg-v
 */ 
data class TimedEntry(val key: String, var value: Int, val maxDurationInMillis: Long) {
    
    var creationTime: Long = now()

    fun isExpired() = (now() - creationTime) > maxDurationInMillis
        
    private fun now() = System.currentTimeMillis()
    
}