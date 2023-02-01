package local.intranet.bttf.api.scheduler

import local.intranet.bttf.api.config.ApplicationConfig
import local.intranet.bttf.api.redis.RedisMessagePublisher

import org.quartz.CronTrigger
import org.quartz.JobDetail
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.quartz.CronTriggerFactoryBean
import org.springframework.scheduling.quartz.JobDetailFactoryBean
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import org.springframework.util.StringUtils

/**
 *
 * {@link SchedulerConfig} for {@link local.intranet.bttf.IndexApplication} and {@link BttfJob}
 *
 * @author Radek Kádner
 *
 */
@Configuration
@EnableAutoConfiguration
@AutoConfigureAfter(ApplicationConfig::class, RedisMessagePublisher::class)
@ConditionalOnExpression("\${scheduler.enabled}")
public class SchedulerConfig {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${scheduler.properties.location}")
    private lateinit var location: String

    @Value("\${bttf.app.cron.frequency.trigger}")
    private lateinit var frequency: String

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    /**
     *
     * springBeanJobFactory used
     * {@link local.intranet.bttf.api.scheduler.JobFactory}
     *
     * @return {@link SpringBeanJobFactory}
     */
    @Bean
    fun springBeanJobFactory(): SpringBeanJobFactory {
        val ret = JobFactory()
        ret.setApplicationContext(applicationContext)
        log.info("Configuring Job factory:'{}'", ret::class.java.simpleName)
        return ret;
    }

    /**
     *
     * Job detail
     *
     * @return {@link JobDetailFactoryBean}
     */
    @Bean
    fun jobDetail(): JobDetailFactoryBean {
        val ret = JobDetailFactoryBean()
        ret.setJobClass(BttfJob::class.java)
        val name = StringUtils.uncapitalize(BttfJob::class.java.simpleName)
        ret.setName(name)
        ret.setDescription("Invoke Job Service...")
        ret.setDurability(true)
        ret.afterPropertiesSet()
        log.info("Configuring Job detail:'{}'", name)
        return ret;
    }

    /**
     *
     * Cron scheduler to perform {@link BttfJob#execute} if is time to job.
     * <p>
     * <code>
     * Quartz Scheduler 'bttf-scheduler-instance'
     * </code>
     * <p>
     *
     * @param trigger {@link org.quartz.CronTrigger}
     * @param job     {@link org.quartz.JobDetail
     * @return {@link SchedulerFactoryBean}
     */
    @Bean
    fun scheduler(trigger: CronTrigger, job: JobDetail): SchedulerFactoryBean {
        val ret = SchedulerFactoryBean()
        ret.setJobFactory(springBeanJobFactory())
        ret.setTriggers(trigger)
        ret.setConfigLocation(ClassPathResource(location))
        ret.setJobDetails(job)
        ret.setWaitForJobsToCompleteOnShutdown(true)
        log.info("Starting Scheduler threads. '{}'", trigger.key.name)
        return ret
    }

    /**
     *
     * Cron trigger for {@link #scheduler}
     *
     * @param job {@link org.quartz.JobDetail}
     * @return {@link CronTriggerFactoryBean}
     */
    @Bean
    fun trigger(job: JobDetail): CronTriggerFactoryBean {
        val ret = CronTriggerFactoryBean()
        ret.setJobDetail(job)
        ret.setName("trigger")
        ret.setDescription("jobTrigger")
        ret.setStartDelay(5000)
        ret.setCronExpression(frequency)
        log.info("Configuring trigger to fire '{}' '{}'", frequency, job.key.name)
        return ret
    }

}