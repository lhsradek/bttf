package local.intranet.bttf.api.service

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
 * {@link local.intranet.bttf.api.controller.InfoController#getRoleInfo}
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

    @Transactional(readOnly = true)
    public fun getRoleInfo(): RoleInfo {
        return RoleInfo(getUsersRoles())
    }

    /**
     *
     * Get userRole for {@link local.intranet.bttf.api.controller.InfoController#getUsersRoles}
     *
     * @return {@link List}&lt;{@link RolePlain}&gt;
     */
    @Transactional(readOnly = true)
    protected fun getUsersRoles(): List<RolePlain> {
        val ret = arrayListOf<RolePlain>()
        roleRepository.findAll().forEach {
            ret.add(RolePlain(it.id, it.roleName, it.enabled, it.user.map {
                it.userName
            }))
        }
        // if (dbg.toBoolean()) log.debug("GetUserRoles ret:{}", ret)
        return ret;
    }

}
