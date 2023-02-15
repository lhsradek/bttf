package local.intranet.bttf.api.service

import local.intranet.bttf.api.controller.StatusController
import local.intranet.bttf.api.info.BeanInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 *
 * {@link BeanService} for
 * {@link local.intranet.bttf.api.controller.InfoController#beanInfo}
 *
 * @author Radek Kádner
 *
 */
@Service
public class BeanService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var statusController: StatusController

    /**
     *
     * For {@link local.intranet.bttf.api.controller.InfoController#userInfo}
     *
     * @return {@link UserInfo}
     */
    public fun beanInfo(): BeanInfo = statusController.bttfAPIBean()

}
