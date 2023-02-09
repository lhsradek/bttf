package local.intranet.bttf.api.info

import java.time.ZonedDateTime
import javax.validation.constraints.Size
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.domain.Countable
import local.intranet.bttf.api.domain.DefaultFieldLengths
import local.intranet.bttf.api.domain.Invocationable
import local.intranet.bttf.api.domain.Statusable
import local.intranet.bttf.api.model.repository.CounterRepository
import local.intranet.bttf.api.service.JobService
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.envers.RevisionType

/**
 *
 * {@link CounterInfo} for
 * {@link local.intranet.core.api.controller.InfoController#getCounterInfo}
 * implements {@link local.intranet.bttf.api.domain.Countable},
 * implements {@link local.intranet.bttf.api.domain.Invocationable} and
 * implements {@link local.intranet.bttf.api.domain.Statusable},
 *
 * @author Radek Kádner
 *
 * @constructor with parameters
 *
 * @param cnt            {@link Long}
 * @param date           {@link ZonedDateTime}
 * @param status         {@link StatusType}
 * @param name           {@link String}
 * @param name           {@link String}
 * @param revisionNum    {@Int}
 * @param revisionType   {@RevisionType}
 */
@JsonPropertyOrder("name", "count", "date", "status", "revisionNum", "revisionType")
public data class CounterInfo (

    public val count: Long,

    public val date: ZonedDateTime,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val statusType: StatusType,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val name: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(min = 0)
    public val revisionNum: Int,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(min = 0)
    public val revisionType: RevisionType

)  : Countable, Invocationable, Statusable {
    
    // @JsonProperty("count")
    // @Size(min = 0)
    public override fun countValue(): Long = count
    
    // @JsonFormat(timezone = JsonFormat.DEFAULT_TIMEZONE)
    // @JsonInclude(JsonInclude.Include.NON_NULL)
    public override fun lastInvocation(): ZonedDateTime = date

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public override fun getStatus(): StatusType = statusType

}
