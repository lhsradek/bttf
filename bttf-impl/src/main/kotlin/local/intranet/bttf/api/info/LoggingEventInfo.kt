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
data class LoggingEventInfo constructor(
    val id: Long,
    val formattedMessage: String,
    val levelString: String,
    val callerClass: String,
    val callerMethod: String,
    val arg0: String,
    val arg1: String,
    val arg2: String,
    val arg3: String,
    val date: ZonedDateTime ) {

    /**
     *
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        return "LoggingEventInfo [id=" + id + ", formattedMessage=" + formattedMessage + ", levelString=" + levelString +
                ", callerClass=" + callerClass + ", callerMethod=" + callerMethod + ", arg0=" + arg0 + ", arg1=" +
                arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", date=" + date + "]"
    }

}
