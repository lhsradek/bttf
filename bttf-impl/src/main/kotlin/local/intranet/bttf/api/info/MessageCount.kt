package local.intranet.bttf.api.info

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import local.intranet.bttf.api.domain.DefaultFieldLengths
import javax.validation.constraints.Size


/**
 *
 * {@link LevelCount} for
 * {@link local.intranet.bttf.api.model.repository.MessageEventRepository},
 * {@link local.intranet.bttf.api.service.MessageEventService#countTotalMessageEvents} and
 * {@link local.intranet.core.api.controller.InfoController#loggingEvent}
 *
 * @constructor with parameters
 *
 * @param level {@link String}
 * @param total {@link Long}
 */
@JsonPropertyOrder("serviceName", "total")
public data class MessageCount (
    
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val serviceName: String,
    
    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val total: Long

) 
