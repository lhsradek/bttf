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
data class LevelCount (val level: String, val total: Long)
