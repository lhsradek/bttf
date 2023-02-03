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
 * Constructor with parameter
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
     * Returns the rolename
     *
     * @return the rolename
     */
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("service")
    public fun serviceName(): String {
        return RoleService::class.java.getSimpleName()
    }

}
