package local.intranet.bttf.api.service

import java.time.ZonedDateTime
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
 * <p>
 * I also write &#64;Autowired, although it is optional. Here it strikes me that something is different.
 * The variable loginAttempt is initialized in &#64;PostConstruct public fun init().
 * Similarly, I write an optional "public".
 * Without "public" or "protected" it, one wonders if something is missing for example in JavaDoc
 *
 * @author Radek Kádner
 *
 */
@Service
public class LoginAttemptService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${bttf.app.debug:false}")
    private lateinit var dbg: String // toBoolean

    @Value("\${bttf.app.login.maxAttemt}")
    private lateinit var maxAttempt: String // toInt()

    @Value("\${bttf.app.login.waitSec}")
    private lateinit var waitSec: String // toLong()

    @Value("\${bttf.app.login.printBlocked:false}")
    private lateinit var printBlocked: String // toBoolean()

    private lateinit var loginAttempt: AttemptCache

    /**
     *
     * Init be executed after injecting this service.
     */
    @PostConstruct
    public fun init() {
        loginAttempt = AttemptCache.init(waitSec.toLong(), SECONDS, printBlocked.toBoolean(), maxAttempt.toInt())
    }

    /**
     *
     * Get attempts for {@link local.intranet.bttf.api.controller.InfoController#loginAttempts}
     *
     * @param printBlocked {@link Boolean}
     * @return {@link List}&lt;{@link Triple}&lt;{@link String},{@link Int},{@link Long}&gt;&gt;
     */
    public fun loginAttempts(@NotNull printBlocked: Boolean):
        List<Triple<String, Int, ZonedDateTime>> = loginAttempt.getCache(printBlocked)

    /**
     *
     * Find by id
     *
     * @param key {@link String}
     * @return {@link Int?}
     */
    public fun findById(@NotNull key: String): Int? = loginAttempt.getById(key)

    /**
     *
     * Login succeeded
     *
     * @param key {@link String}
     */
    public fun loginSucceeded(@NotNull key: String) = loginAttempt.invalidateKey(key)

    /**
     *
     * Login failed
     *
     * @param key {@link String}
     */
    public fun loginFailed(@NotNull key: String) = loginAttempt.keyToCache(key)

    /**
     *
     * Is blocked?
     *
     * @param key {@link String}
     * @return {@link Boolean}
     */
    public fun isBlocked(@NotNull key: String): Boolean = loginAttempt.isBlocked(key)

    /**
     *
     * for scheduler {@link local.intranet.bttf.api.scheduler.BttfJob}
     */
    public fun flushCache() = loginAttempt.removeExpiredKeys()

}
