package local.intranet.bttf.api.service

import java.util.concurrent.TimeUnit.SECONDS
import javax.annotation.PostConstruct
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.info.content.AttemptCache
import org.jetbrains.annotations.NotNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 *
 * {@link LoggingAttemptService} for
 * {@link local.intranet.bttf.BttfApplication} and
 * {@link local.intranet.bttf.api.controller.InfoController#getLoginAttempts}
 *
 * @author Radek Kádner
 *
 */
@Service
public class LoginAttemptService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${bttf.app.login.maxAttemt}") private lateinit var maxAttempt: String  // toInt()
    @Value("\${bttf.app.login.waitSec}") private lateinit var waitSec: String       // toLong()
    @Value("\${bttf.app.login.printBlocked:false}") private lateinit var printBlocked: String // toBoolean()

    private lateinit var loginAttempt: AttemptCache

    /**
     *
     * Init be executed after injecting this service.
     */
    @PostConstruct
    fun init() {
        loginAttempt = AttemptCache.init(waitSec.toLong(), SECONDS, printBlocked.toBoolean())
    }

    /**
     *
     * Get attempts for {@link local.intranet.bttf.api.controller.InfoController#getLoginAttempts}
     *
     * @param printBlocked {@link Boolean}
     * @return {@link List}&lt;{@link Triple}&lt;{@link String},{@link Int},{@link Long}&gt;&gt;
     */
    fun getLoginAttempts(@NotNull printBlocked: Boolean): List<Triple<String, Int, Long>> {
        val ret = loginAttempt.getCache(printBlocked)
        return ret
    }

    /**
     *
     * Find by id
     *
     * @param key {@link String}
     * @return {@link Int}
     */
    fun findById(@NotNull key: String): Int {
    	val ret: Int = loginAttempt.getById(key)?.let {
            loginAttempt.getById(key)
        }?: 0
    	return ret
    }

    /**
     *
     * Login succeeded
     *
     * @param key {@link String}
     */
    fun loginSucceeded(@NotNull key: String) {
    	loginAttempt.invalidateKey(key)
    }

    /**
     *
     * Login failed
     *
     * @param key {@link String}
     */
    fun loginFailed(@NotNull key: String) {
        loginAttempt.keyToCache(key)
    }

    /**
     *
     * Is blocked?
     *
     * @param key {@link String}
     * @return {@link Boolean}
     */
    fun isBlocked(@NotNull key: String): Boolean {
    	val ret = loginAttempt.isBlocked(key)?.let {
            loginAttempt.isBlocked(key)
        }?: false
        return ret
    }

}
