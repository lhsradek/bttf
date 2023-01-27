package local.intranet.bttf.test

import local.intranet.bttf.api.controller.InfoController
import local.intranet.bttf.api.controller.StatusController
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.security.AESUtil
import local.intranet.bttf.api.service.UserService

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
import org.springframework.security.core.userdetails.UsernameNotFoundException
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

    @Autowired private lateinit var statusController: StatusController
    @Autowired private lateinit var infoController: InfoController
    @Autowired private lateinit var userService: UserService
    
    /**
     *
     * givenTest
     *
     */
    @Test
    fun givenTest() {
        assertThat(statusController).isNotNull
        assertThat(statusController.getPlainStatus()).isNotBlank
        assertThat(statusController.getImplementationVersion()).isNotBlank
        assertThat(statusController.getStage()).isNotBlank
        assertThat(statusController.getActiveProfiles()).isNotBlank
        assertThat(statusController.getServerSoftware()).isNotBlank

        assertThat(infoController).isNotNull

        assertThat(userService.getUsername()).isNotNull
        assertThat(userService.isAuthenticated()).isNotNull
        assertThat(userService.getAuthoritiesRoles()).isNotNull
        assertThat(userService.getUserRoles().count() > 0)
        assertThat(userService.loadUserByUsername("lhs")).isNotNull
        assertThrows<UsernameNotFoundException> { userService.loadUserByUsername("coco") }

        // a bit of Dadaism
        assertEquals("We have a stuffed grandfather in the closet.",
            AESUtil.getBase64("V2UgaGF2ZSBhIHN0dWZmZWQgZ3JhbmRmYXRoZXIgaW4gdGhlIGNsb3NldC4=")
        )
        assertEquals("V2UgaGF2ZSBhIHN0dWZmZWQgZ3JhbmRmYXRoZXIgaW4gdGhlIGNsb3NldC4=",
            AESUtil.setBase64("We have a stuffed grandfather in the closet.")
        )
        assertEquals("My cork badtub is like your giraffe rye!",
            AESUtil.getHex("4d7920636f726b20626164747562206973206c696b6520796f757220676972616666652072796521")
        )
        assertEquals("4d7920636f726b20626164747562206973206c696b6520796f757220676972616666652072796521",
            AESUtil.setHex("My cork badtub is like your giraffe rye!")
        )

    }

}