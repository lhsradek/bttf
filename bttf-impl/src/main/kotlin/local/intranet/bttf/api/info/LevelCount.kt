package local.intranet.bttf.api.info

/**
 *
 * {@link LevelCount} for
 * {@link local.intranet.bttf.api.model.repository.LoggingEventRepository}
 * and {@link local.intranet.bttf.api.service.LoggingEventService}
 *
 * Constructor with parameters
 *
 * @param level {@link String}
 * @param total {@link Long}
 */
data class LevelCount constructor(val level: String, val total: Long) {

    /**
     *
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        return "LevelCount [level=" + level + ", total=" + total + "]"
    }

}
