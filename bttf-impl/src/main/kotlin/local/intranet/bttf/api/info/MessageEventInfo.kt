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
 * {@link MessageInfo} for
 * {@link local.intranet.core.api.controller.InfoController#messageInfo}
 *
 * @author Radek Kádner
 *
 * @constructor with parameters
 *
 * @param service        {@link String}
 * @param name           {@link String}
 * @param date           {@link ZonedDateTime}
 * @param count          {@link Long}
 * @param revisionNum    {@Int}
 * @param revisionType   {@RevisionType}
 */
@JsonPropertyOrder("id", "uuid", "name", "count", "date", "uuid", "revisionNum", "revisionType")
public data class MessageEventInfo (

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val id: Long,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val uuid: String,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val name: String,

    public val date: ZonedDateTime,

    public val count: Long,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(min = 0)
    public val revisionNum: Int,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(min = 0)
    public val revisionType: RevisionType

)  : Countable {
    
    // @JsonProperty("count")
    // @Size(min = 0)
    public override fun countValue(): Long {
    	return count
    }
    
}