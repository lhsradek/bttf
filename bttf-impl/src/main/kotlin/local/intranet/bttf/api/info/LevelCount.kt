package local.intranet.bttf.api.info

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import local.intranet.bttf.api.domain.DefaultFieldLengths
import javax.validation.constraints.Size


/**
 *
 * {@link LevelCount} for
 * {@link local.intranet.bttf.api.model.repository.LoggingEventRepository},
 * {@link local.intranet.bttf.api.service.LoggingEventService#countTotalLoggingEvents} and
 * {@link local.intranet.core.api.controller.InfoController#loggingEvent}
 *
 * Constructor with parameters
 *
 * @param level {@link String}
 * @param total {@link Long}
 */
@JsonPropertyOrder("level", "total")
public data class LevelCount (
    
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val level: String,
    
    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val total: Long

) 
