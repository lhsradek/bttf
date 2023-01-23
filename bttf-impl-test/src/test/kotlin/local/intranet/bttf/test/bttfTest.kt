package local.intranet.bttf.test

import local.intranet.bttf.api.controller.StatusController
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class bttfTest {

    @Autowired
    private lateinit var statusController: StatusController

    @Test
    fun givenTest() {
        assertThat(statusController).isNotNull
        assertThat(statusController.getPlainStatus()).isNotBlank
        assertThat(statusController.getImplementationVersion()).isNotBlank
        assertThat(statusController.getStage()).isNotBlank
        assertThat(statusController.getActiveProfiles()).isNotBlank
        assertThat(statusController.getServerSoftware()).isNotBlank
    }

}