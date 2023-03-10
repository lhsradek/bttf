package local.intranet.bttf.api.service

import local.intranet.bttf.api.domain.type.RoleType
import local.intranet.bttf.api.info.RoleInfo
import local.intranet.bttf.api.info.RolePlain
import local.intranet.bttf.api.model.repository.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * {@link RoleService} for
 * {@link local.intranet.bttf.api.controller.InfoController#roleInfo}
 *
 * @author Radek Kádner
 *
 */
@Service
public class RoleService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${bttf.app.debug:false}")
    private lateinit var dbg: String // toBoolean

    @Autowired
    private lateinit var roleRepository: RoleRepository

    /**
     *
     * For {@link local.intranet.bttf.api.controller.InfoController#roleInfo}
     *
     * @return {@link RoleInfo}
     */
    @Synchronized
    @Transactional(readOnly = true)
    public fun roleInfo(): RoleInfo = RoleInfo(usersRoles())

    /**
     *
     * Get users roles
     *
     * @return {@link List}&lt;{@link RolePlain}&gt;
     */
    @Synchronized
    @Transactional(readOnly = true)
    protected fun usersRoles(): List<RolePlain> {
        val ret = arrayListOf<RolePlain>()
        roleRepository.findAll().forEach {
            with(it) {
                ret.add(RolePlain(id!!, roleName, enabled, user.map { it.userName } ))
            }
        }
        // if (dbg.toBoolean()) log.debug("UserRoles ret:{}", ret)
        return ret
    }

}
