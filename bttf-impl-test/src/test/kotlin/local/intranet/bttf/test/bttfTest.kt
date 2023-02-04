package local.intranet.bttf.test

import javax.crypto.spec.IvParameterSpec
import local.intranet.bttf.api.controller.InfoController
import local.intranet.bttf.api.controller.StatusController
import local.intranet.bttf.api.security.AESUtil
import local.intranet.bttf.api.service.LoginAttemptService
import local.intranet.bttf.api.service.BttfService
import local.intranet.bttf.api.service.UserService

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
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
public class bttfTest {

    @Value("\${bttf.sec.key}")
    private lateinit var key: String

    @Autowired
    private lateinit var statusController: StatusController

    @Autowired
    private lateinit var infoController: InfoController

    @Autowired
    private lateinit var loginAttemptService: LoginAttemptService
    
    @Autowired
    private lateinit var userService: UserService

    /**
     *
     * givenTest
     *
     */
    @Test
    public fun givenTest() {
        assertThat(statusController).isNotNull
        assertThat(statusController.getPlainStatus()).isNotBlank
        assertThat(statusController.getImplementationVersion()).isNotBlank
        assertThat(statusController.getStage()).isNotBlank
        assertThat(statusController.getActiveProfiles()).isNotBlank
        assertThat(statusController.getServerSoftware()).isNotBlank

        assertThat(infoController).isNotNull

        assertThat(loginAttemptService).isNotNull
        assertThat(loginAttemptService.getLoginAttempts(true)).isNotNull
        assertThat(loginAttemptService.getLoginAttempts(false)).isNotNull

        assertThat(userService).isNotNull
        assertThat(userService.getUsername()).isNotNull
        assertThat(userService.isAuthenticated()).isNotNull
        assertThat(userService.getAuthoritiesRoles()).isNotNull
        assertThat(userService.getUserRoles().count() > 0)
        assertThat(userService.loadUserByUsername("lhs")).isNotNull
        assertThrows<UsernameNotFoundException> { userService.loadUserByUsername("coco") }

        val year1 = 2023L
        val salt = AESUtil.generateSalt()
        val iv = AESUtil.generateIv()
        val secretKey1 = AESUtil.getKeyFromPassword(key, salt)
        val en = BttfService.secForPlayer(year1, secretKey1, iv)
        val secretKey2 = AESUtil.getKeyFromPassword(key, salt)
        val year2 = AESUtil.decrypt(AESUtil.getHex(en), secretKey2, iv).toLong()
        assertThat(year1 == year2)

        // a bit of Dadaism
        assertThat(
            "We have a stuffed grandfather in the closet." ==
            AESUtil.getBase64("V2UgaGF2ZSBhIHN0dWZmZWQgZ3JhbmRmYXRoZXIgaW4gdGhlIGNsb3NldC4=")
        )
        assertThat(
            "V2UgaGF2ZSBhIHN0dWZmZWQgZ3JhbmRmYXRoZXIgaW4gdGhlIGNsb3NldC4=" ==
            AESUtil.setBase64("We have a stuffed grandfather in the closet.")
        )
        assertThat(
            "My cork badtub is like your giraffe rye!" ==
            AESUtil.getHex("4d7920636f726b20626164747562206973206c696b6520796f757220676972616666652072796521")
        )
        assertThat(
            "4d7920636f726b20626164747562206973206c696b6520796f757220676972616666652072796521" ==
            AESUtil.setHex("My cork badtub is like your giraffe rye!")
        )

    }
    
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
// import org.springframework.transaction.annotation.Transactional
// import org.springframework.transaction.annotation.Propagation
// @DataJpaTest
// @Autowired lateinit var entityManager: TestEntityManager
// @Transactional(propagation = Propagation.NOT_SUPPORTED)

}