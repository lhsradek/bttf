package local.intranet.bttf.api.scheduler

import java.util.StringJoiner
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.redis.RedisMessagePublisher
import local.intranet.bttf.api.service.JobService
import local.intranet.bttf.api.service.LoginAttemptService
import local.intranet.bttf.api.service.MessageService
import local.intranet.bttf.api.service.UserService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 *
 * {@link BttfJob} for quartz scheduler uses in
 * {@link local.intranet.bttf.api.scheduler.SchedulerConfig}
 *
 * @author Radek Kádner
 *
 */
@Component
@ConditionalOnExpression("\${scheduler.enabled}")
@AutoConfigureAfter(LoginAttemptService::class, RedisMessagePublisher::class, UserService::class)
public class BttfJob : Job {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${bttf.app.redis.message}")
    private lateinit var isRedis: String // toBoolean

    @Value("\${bttf.sec.name}")
    private lateinit var name: String
    
    @Autowired
    private lateinit var jobService: JobService

    @Autowired
    private lateinit var loginAttemptService: LoginAttemptService

    @Autowired
    private lateinit var messageService: MessageService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var redisMessagePublisher: RedisMessagePublisher

    /**
     *
     * @param context {@link JobExecutionContext}
     * @throws JobExecutionException
     */
    @Throws(JobExecutionException::class)
    public override fun execute(context: JobExecutionContext) {
        
        val user = userService.loadUserByUsername(name)
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken(
            user.getUsername(), user.getPassword(), user.getAuthorities()))
        
        loginAttemptService.flushCache()

        val message = StringJoiner(BttfConst.BLANK_SPACE)
        with(message) {
            add("Fired:'${context.jobDetail.key.name}'")
            add("message.count:${messageService.countValue()}")
            val messageEvent = messageService.sendMessage("${message}")
            jobService.incrementCounter()
            add("counter.count:${messageService.countValue()}")
            add("message.event.uuid:'${messageEvent.uuid}'")
        }

        // Redis as a message broker
        if (isRedis.toBoolean()) {
            redisMessagePublisher.publish("${message}")
        } else {
            log.info("${message}")
        }
    }

    /*
     *
     * import javax.annotation.PostConstruct
     *  
     * Init be executed after injecting this service.
     *
    @PostConstruct
    public fun init() {
        log.info("start {}", javaClass.simpleName)
    }
     */

}