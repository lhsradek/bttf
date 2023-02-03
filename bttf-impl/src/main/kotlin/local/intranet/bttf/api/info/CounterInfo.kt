package local.intranet.core.api.info

import java.time.ZonedDateTime
import javax.validation.constraints.Size
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.model.repository.UserRepository
import local.intranet.bttf.api.domain.DefaultFieldLengths
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.GrantedAuthority
import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 * 
 * {@link CounterInfo} for
 * {@link local.intranet.core.api.service.IndexService#getCounterInfo} and
 * {@link local.intranet.core.api.controller.InfoController#getCounterInfo}
 * 
 * @author Radek Kádner
 *
 * Constructor with parameter
     * @param id             {@link Long}
     * @param date           {@link ZonedDateTime}
     * @param userRepository {@link UserRepository}
     * @param authorities    {@link MutableList}&lt;?{@link GrantedAuthority}&gt;
     * @param status         {@link StatusType}
     * @param username       {@link String}
 *
 */
@JsonPropertyOrder("_id", "name", "status", "count", "date", "username", "authoritiesRoles", "counterInfo")
data class CounterInfo (
    val id: Long,
    val date: ZonedDateTime,
    val userRepository: UserRepository,
    val authorities: MutableList<GrantedAuthority>,
    val status: StatusType,
    val username: String,
    @Size(min = 0, max = DefaultFieldLengths.DEFAULT_NAME)
    val counterName: String)
