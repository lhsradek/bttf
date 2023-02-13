package local.intranet.bttf.test

import javax.crypto.spec.IvParameterSpec
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.controller.IndexController
import local.intranet.bttf.api.controller.InfoController
// import local.intranet.bttf.api.controller.ProviderController
import local.intranet.bttf.api.controller.StatusController
import local.intranet.bttf.api.security.AESUtil
import local.intranet.bttf.api.service.BttfService
import local.intranet.bttf.api.service.JobService
import local.intranet.bttf.api.service.LoginAttemptService
import local.intranet.bttf.api.service.MessageService
import local.intranet.bttf.api.service.RoleService
import local.intranet.bttf.api.service.UserService

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.context.SecurityContextHolder
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

    @Value("\${bttf.sec.name}")
    private lateinit var name: String
    
    @Autowired
    private lateinit var indexController: IndexController

    @Autowired
    private lateinit var infoController: InfoController
    
    @Autowired
    private lateinit var statusController: StatusController

    // @Autowired
    // private lateinit var providerController: ProviderController
    
    @Autowired
    private lateinit var loginAttemptService: LoginAttemptService
    
    @Autowired
    private lateinit var jobService: JobService

    @Autowired
    private lateinit var messageService: MessageService
    
    @Autowired
    private lateinit var roleService: RoleService
    
    @Autowired
    private lateinit var userService: UserService
    
    /**
     *
     * givenTest
     */
    @Test
    public fun givenTest() {
        assertThat(userService).isNotNull
        assertThat(userService.username()).isNotNull
        assertThat(userService.isAuthenticated()).isNotNull
        assertThat(userService.authoritiesRoles()).isNotNull
        assertThat(userService.userRoles().count() > 0)
        assertThrows<UsernameNotFoundException> { userService.loadUserByUsername("coco") }
        val user = userService.loadUserByUsername(name)
        val auth: Authentication = UsernamePasswordAuthenticationToken(
            user.getUsername(), user.getPassword(), user.getAuthorities())
        SecurityContextHolder.getContext().setAuthentication(auth)
        
        assertThat(roleService.roleInfo()).isNotNull
        assertThat(jobService.countValue()).isNotNull
        assertThat(jobService.lastInvocation()).isNotNull
        assertThat(jobService.getStatus().equals(StatusType.UP))
        assertThat(messageService.countValue()).isNotNull
        assertThat(loginAttemptService).isNotNull
        assertThat(loginAttemptService.loginAttempts(null)).isNotNull
        assertThat(loginAttemptService.loginAttempts(true)).isNotNull
        assertThat(loginAttemptService.loginAttempts(false)).isNotNull
        
        // assertThat(providerController).isNotNull
        // assertThat(providerController.getInstanceName()).isNotBlank
        assertThat(indexController).isNotNull
        assertThat(infoController).isNotNull
        assertThat(infoController.roleInfo()).isNotNull
        assertThat(infoController.countTotalLoggingEvents()).isNotNull
        assertThat(infoController.countTotalMessageEvents()).isNotNull
        // assertThat(infoController.beanInfo()).isNotNull
        // assertThat(statusController.bttfAPIBean()).isNotNull
        assertThat(statusController).isNotNull
        assertThat(statusController.plainStatus()).isNotBlank
        assertThat(statusController.implementationVersion()).isNotBlank
        assertThat(statusController.stage()).isNotBlank
        assertThat(statusController.activeProfiles()).isNotBlank
        assertThat(statusController.serverSoftware()).isNotBlank

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