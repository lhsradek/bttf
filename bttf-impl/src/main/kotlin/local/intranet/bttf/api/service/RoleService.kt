package local.intranet.bttf.api.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import local.intranet.bttf.api.info.RoleInfo

/**
 *
 * {@link RoleService} for
 * {@link local.intranet.bttf.api.controller.InfoController#getRoleInfo}
 *
 * @author Radek Kádner
 *
 */
@Service
class RoleService {

    private val log = LoggerFactory.getLogger(RoleService::class.java)

    @Value("\${bttf.app.debug:false}")
    private lateinit var dbg: String // toBoolean

    /*
    @Transactional(readOnly = true)
    fun getRoleInfo(): RoleInfo {
        val roleInfo = RoleInfo()
        return roleInfo
    }
    */

}
