package local.intranet.bttf.api.info

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import local.intranet.bttf.api.domain.DefaultFieldLengths
import javax.validation.constraints.Size


/**
 *
 * {@link ServiceCount} for
 * {@link local.intranet.bttf.api.model.repository.MessageEventRepository},
 * {@link local.intranet.bttf.api.service.MessageEventService#countTotalMessageEvents}
 *
 * @constructor with parameters
 *
 * @param level {@link String}
 * @param total {@link Long}
 */
@JsonPropertyOrder("serviceName", "total")
public data class ServiceCount (
    
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_STATUS)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val serviceName: String,
    
    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val total: Long

) 
