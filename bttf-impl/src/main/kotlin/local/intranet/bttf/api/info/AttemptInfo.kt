package local.intranet.bttf.api.info

import java.time.ZonedDateTime
import javax.validation.constraints.Size
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonProperty
import local.intranet.bttf.api.domain.DefaultFieldLengths

/**
 *
 * {@link AttemptInfo}
 *
 * @author Radek Kádner
 *
 * @constructor with parameters
 *
 * @param ip      {@link String}
 * @param attempt {@link Int}
 * @param date    {@link ZonedDateTime}
*/
@JsonPropertyOrder("ip", "attempt", "date")
public data class AttemptInfo (

    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val ip: String,

    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val attempt: Int,
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonFormat(timezone = JsonFormat.DEFAULT_TIMEZONE)
    public val date: ZonedDateTime
    
) 