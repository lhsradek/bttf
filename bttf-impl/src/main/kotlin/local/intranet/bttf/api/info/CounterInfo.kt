package local.intranet.bttf.api.info

import java.time.ZonedDateTime
import javax.validation.constraints.Size
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.domain.DefaultFieldLengths
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
public data class CounterInfo(

    @JsonProperty("count")
    @Size(min = 0)
    public val count: Long,

    @JsonFormat(timezone = JsonFormat.DEFAULT_TIMEZONE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val date: ZonedDateTime,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val status: StatusType,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val name: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(min = 0)
    public val revisionNum: Int,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(min = 0)
    public val revisionType: RevisionType
)
