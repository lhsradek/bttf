package local.intranet.bttf.api.info

import java.time.ZonedDateTime
import javax.validation.constraints.Size
import local.intranet.bttf.api.domain.DefaultFieldLengths
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude

/**
 *
 * {@link LoggingEventInfo} for
 * {@link local.intranet.bttf.api.service.LoggingEventService}
 *
 * @author Radek Kádner
 *
 * @constructor with parameters
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
public data class LoggingEventInfo(

    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val id: Long,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val formattedMessage: String,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val levelString: String,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val callerClass: String,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val callerMethod: String,

    public val arg0: String,
    public val arg1: String,
    public val arg2: String,
    public val arg3: String,

    @JsonFormat(timezone = JsonFormat.DEFAULT_TIMEZONE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val date: ZonedDateTime
)
