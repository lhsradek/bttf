package local.intranet.bttf.api.info

import java.time.ZonedDateTime
import javax.validation.constraints.Size
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.model.repository.CounterRepository
import local.intranet.bttf.api.domain.DefaultFieldLengths
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonProperty
import local.intranet.bttf.api.service.JobService

/**
 *
 * {@link CounterInfo} for
 * {@link local.intranet.core.api.controller.InfoController#getCounterInfo}
 *
 * @author Radek Kádner
 *
 * Constructor with parameter
 * @param cnt            {@link Long}
 * @param date           {@link ZonedDateTime}
 * @param status         {@link StatusType}
 * @param name           {@link String}
 *
 */
@JsonPropertyOrder("name", "count", "date", "status")
data class CounterInfo(

    @JsonProperty("count")
    val cnt: Long,

    @JsonFormat(timezone = JsonFormat.DEFAULT_TIMEZONE)
    val date: ZonedDateTime,

    val status: StatusType,

    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val name: String
)
