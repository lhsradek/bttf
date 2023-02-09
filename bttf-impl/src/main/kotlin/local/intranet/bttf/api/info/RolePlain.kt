package local.intranet.bttf.api.info

import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import local.intranet.bttf.api.domain.DefaultFieldLengths;

/**
 * 
 * {@link RolePlain} for {@link local.intranet.tombola.api.info.RoleInfo}
 * 
 * @author Radek Kádner
 *
 * @constructor with parameters
 *
 * @param id       {@link Long}
 * @param roleName {@link String}
 * @param enabled  {@link Boolean}
 * @param users    {@link List}&lt;{@link String}&gt;
 *
 */
@JsonPropertyOrder("id", "roleName", "enabled", "users")
public data class RolePlain (
    
    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val id: Long,
    
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_STATUS)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public val roleName: String,
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val enabled: Boolean,
    
    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val users: List<String>)
