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
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.envers.RevisionType

/**
 *
 * {@link MessageInfo} for
 * {@link local.intranet.core.api.controller.InfoController#messageInfo}
 * implements {@link local.intranet.bttf.api.domain.Countable}
 *
 * @author Radek Kádner
 *
 * @constructor with parameters
 *
 * @param id             {@link Long}
 * @param uuid           {@link String}
 * @param name           {@link String}
 * @param date           {@link ZonedDateTime}
 * @param count          {@link Long}
 * @param message        {@link String}
 * @param revisionNum    {@Int}
 * @param revisionType   {@RevisionType}
 */
@JsonPropertyOrder("id", "uuid", "name", "count", "date", "message", "revisionNum", "revisionType")
public data class MessageEventInfo (

    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val id: Long,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_STATUS)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val uuid: String,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_STATUS)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val name: String,

    @JsonFormat(timezone = JsonFormat.DEFAULT_TIMEZONE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val date: ZonedDateTime,

    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val count: Long,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val message: String,

    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val revisionNum: Int,

    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val revisionType: RevisionType

)  : Countable {
        
    @JsonIgnore
    public override fun countValue(): Long = count
    
}
