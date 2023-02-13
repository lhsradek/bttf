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
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.envers.RevisionType

/**
 *
 * {@link CounterInfo} for
 * {@link local.intranet.core.api.controller.InfoController#getCounterInfo}
 * implements {@link local.intranet.bttf.api.domain.Countable},
 * {@link local.intranet.bttf.api.domain.Invocationable} and
 * {@link local.intranet.bttf.api.domain.Statusable},
 *
 * @author Radek Kádner
 *
 * @constructor with parameters
 *
 * @param count          {@link Long}
 * @param date           {@link ZonedDateTime}
 * @param name           {@link String}
 * @param status         {@link StatusType}
 * @param revisionNum    {@Int}
 * @param revisionType   {@RevisionType}
 */
@JsonPropertyOrder("name", "count", "date", "status", "revisionNum", "revisionType")
public data class CounterInfo (

    @Size(min = 0)
    public val count: Long,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonFormat(timezone = JsonFormat.DEFAULT_TIMEZONE)
    public val date: ZonedDateTime,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_STATUS)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val name: String,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_STATUS)
    @JsonProperty("status")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val statusType: StatusType,

    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val revisionNum: Int,

    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val revisionType: RevisionType

)  : Countable, Invocationable, Statusable {
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("count")
    public override fun countValue(): Long = count
    
    @JsonIgnore
    public override fun lastInvocation(): ZonedDateTime = date

    @JsonIgnore
    public override fun getStatus(): StatusType = statusType

}
