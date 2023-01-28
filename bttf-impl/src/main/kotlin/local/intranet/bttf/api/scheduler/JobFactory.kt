package local.intranet.bttf.api.scheduler

import org.quartz.JobExecutionContext
import org.quartz.spi.TriggerFiredBundle
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.scheduling.quartz.SchedulerContextAware
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import org.springframework.stereotype.Component

/**
 *
 * {@link JobFactory} for
 * {@link local.intranet.bttf.api.scheduler.SchedulerConfig#springBeanJobFactory()}.
 * Adds auto-wiring support to quartz jobs. Quartz used
 * {@link local.intranet.bttf.api.scheduler.SchedulerConfig},
 * {@link local.intranet.bttf.api.scheduler.BttfJob#execute(JobExecutionContext)}
 * and {@link local.intranet.bttf.api.service.JobService#executeJob()}
 *
 * @author Radek Kádner
 *
 */
@Component
@ConditionalOnExpression("\${scheduler.enabled}")
class JobFactory : ApplicationContextAware, SchedulerContextAware, SpringBeanJobFactory() {

    @Transient lateinit var beanFactory: AutowireCapableBeanFactory

    /**
     *
     * Set the ApplicationContext that this object runs in.
     *
     * @param applicationContext {@link ApplicationContext}
     */
    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        super.setApplicationContext(applicationContext)
        beanFactory = applicationContext.getAutowireCapableBeanFactory()
    }

    /**
     *
     * Create the job instance, populating it with property values taken from the
     * scheduler context, job data map and trigger data map.
     *
     * @param bundle {@link TriggerFiredBundle}
     * @return {@link Object}
     */
    @Throws(Exception::class)
    protected override fun createJobInstance(bundle: TriggerFiredBundle): Any {
        val job = super.createJobInstance(bundle)
        beanFactory.autowireBean(job)
        return job
    }

}