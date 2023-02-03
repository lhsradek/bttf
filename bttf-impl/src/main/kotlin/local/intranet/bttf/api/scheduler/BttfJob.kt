package local.intranet.bttf.api.scheduler

import local.intranet.bttf.api.service.JobService
import local.intranet.bttf.api.service.LoginAttemptService
import local.intranet.bttf.api.redis.RedisMessagePublisher
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
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
@AutoConfigureAfter(LoginAttemptService::class, RedisMessagePublisher::class)
public class BttfJob : Job {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var jobService: JobService

    @Autowired
    private lateinit var loginAttemptService: LoginAttemptService

    @Autowired
    private lateinit var redisMessagePublisher: RedisMessagePublisher
    
    /**
     *
     * @param context {@link JobExecutionContext}
     * @throws JobExecutionException
     */
    @Throws(JobExecutionException::class)
    public override fun execute(context: JobExecutionContext) {
        jobService.incrementCounter()
        loginAttemptService.flushCache()
        // Redis as a message broker
        redisMessagePublisher.publish("Fired:${context.jobDetail.key.name} count:${jobService.countValue()}")
    }

}