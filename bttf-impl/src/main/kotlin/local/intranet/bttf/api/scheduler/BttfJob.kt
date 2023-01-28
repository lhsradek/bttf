package local.intranet.bttf.api.scheduler

import java.util.concurrent.atomic.AtomicLong
import local.intranet.bttf.api.service.LoginAttemptService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
class BttfJob : Job {

    private val log = LoggerFactory.getLogger(javaClass)

    private val moreCount = AtomicLong(0)

    @Autowired private lateinit var loginAttemptService: LoginAttemptService

    /**
     *
     * @param context {@link JobExecutionContext}
     * @throws JobExecutionException
     */
    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        loginAttemptService.flushCache()
        log.info("Fired '{}' cnt:{}", context.getJobDetail().key.name, moreCount.incrementAndGet())
    }

}