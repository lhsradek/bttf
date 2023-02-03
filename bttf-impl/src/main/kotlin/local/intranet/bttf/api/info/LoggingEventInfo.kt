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
public data class LoggingEventInfo (
    public val id: Long,
    public val formattedMessage: String,
    public val levelString: String,
    public val callerClass: String,
    public val callerMethod: String,
    public val arg0: String,
    public val arg1: String,
    public val arg2: String,
    public val arg3: String,
    public val date: ZonedDateTime )
