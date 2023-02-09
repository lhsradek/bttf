package local.intranet.bttf.api.info

import javax.validation.constraints.Size
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonProperty
import local.intranet.bttf.api.domain.DefaultFieldLengths
import local.intranet.bttf.api.service.RoleService

/**
 *
 * {@link RoleInfo}
 *
 * @author Radek Kádner
 *
 * @constructor with parameters
 *
 * @param role {@link List}&lt;{@link RolePlain}&gt;
*/
@JsonPropertyOrder("service", "roles")
public data class RoleInfo (

    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val role: List<RolePlain>) {

    /**
     *
     * Returns the role service name
     *
     * @return {@link String} 
     */
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_STATUS)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("service")
    public fun serviceName(): String = RoleService::class.java.getSimpleName()

}
