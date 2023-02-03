package local.intranet.bttf.api.info

import java.time.ZonedDateTime

/**
 *
 * {@link LoggingEventInfo} for
 * {@link local.intranet.bttf.api.service.LoggingEventService}
 *
 * @author Radek Kádner
 *
 * Constructor with parameters
 *
 * @param id               {@link Long}
 * @param formattedMessage {@link String}
 * @param levelString      {@link String}
 * @param callerClass      {@link String}
 * @param callerMethod     {@link String}
 * @param arg0             {@link String}
 * @param arg1             {@link String}
 * @param arg2             {@link String}
 * @param arg3             {@link String}
 * @param date             {@link ZonedDateTime}
 */
data class LoggingEventInfo (
    val id: Long,
    val formattedMessage: String,
    val levelString: String,
    val callerClass: String,
    val callerMethod: String,
    val arg0: String,
    val arg1: String,
    val arg2: String,
    val arg3: String,
    val date: ZonedDateTime )
