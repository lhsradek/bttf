package local.intranet.bttf.test

import local.intranet.bttf.api.controller.InfoController
import local.intranet.bttf.api.controller.StatusController
import local.intranet.bttf.api.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

/**
 *
 * {@link BttfTest}
 *
 * @author Radek Kadner
 *
 */
@RunWith(SpringRunner::class)
@SpringBootTest
class bttfTest {

    @Autowired
    private lateinit var statusController: StatusController

    @Autowired
    private lateinit var infoController: InfoController

    @Autowired
    private lateinit var userService: UserService

    /**
     *
     * givenStatusControllerTest
     *
     */
    @Test
    fun givenStatusControllerTest() {
        assertThat(statusController).isNotNull
        assertThat(statusController.getPlainStatus()).isNotBlank
        assertThat(statusController.getImplementationVersion()).isNotBlank
        assertThat(statusController.getStage()).isNotBlank
        assertThat(statusController.getActiveProfiles()).isNotBlank
        assertThat(statusController.getServerSoftware()).isNotBlank

        assertThat(infoController).isNotNull
        assertThrows<UsernameNotFoundException> { infoController.getUserInfo() }

        assertThat(userService).isNotNull
        assertThat(userService.getUsername()).isNotNull
        assertThat(userService.isAuthenticated()).isNotNull
        assertThat(userService.getAuthoritiesRoles()).isNotEmpty
        assertThat(userService.getUserRoles().count() > 0)
        assertThat(userService.loadUserByUsername("lhs")).isNotNull
        assertThrows<UsernameNotFoundException> { userService.loadUserByUsername("MyCorkBathtubIsLikeGiraffe'sEye") }
    }

}