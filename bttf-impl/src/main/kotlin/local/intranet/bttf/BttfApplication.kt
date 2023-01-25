package local.intranet.bttf

import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession
import org.springframework.web.WebApplicationInitializer

/**
 *
 * {@link BttfApplication} extends SpringBootServletInitializer for BackToTheFuture
 * API that it uses controllers in {@link local.intranet.bttf.api.controller}
 * and early services in {@link local.intranet.bttf.api.service}
 * <p>
 * Security and configuration is defined in
 * {@link local.intranet.bttf.api.security.SecurityConfig} and
 * {@link local.intranet.bttf.api.config.ApplicationConfig}
 * <p>
 * Redis is configured in {@link local.intranet.bttf.api.redis.RedisConfig}
 * For RedisHttpSession:
 * <p>
 * <code>
 * &#64;EnableRedisHttpSession(maxInactiveIntervalInSeconds = 120, cleanupCron = ..)
 * </code>
 * <p>
 * For JdbcHttpSession:
 * <p>
 * <code>
 * &#64;EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 120, cleanupCron = ..)
 * </code>
 * <p>
 *
 * @author Radek Kádner
 *
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableAutoConfiguration
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 120, cleanupCron = "0 */5 * * * *")
class BttfApplication : WebApplicationInitializer, SpringBootServletInitializer() {

    private val log = LoggerFactory.getLogger(BttfApplication::class.java)

    private val ENTERING_APPLICATION = "Entering application."

    /**
     *
     * Configure
     * <p>
     * Comment is from overrides
     * {@link org.springframework.boot.web.servlet.support.SpringBootServletInitializer#configure}
     * <p>
     * Configure the application. Normally all you would need to do is to add
     * sources (e.g. config classes) because other settings have sensible defaults.
     * You might choose (for instance) to add default command line arguments, or set
     * an active Spring profile.
     * @see SpringApplicationBuilder
     *
     * @param builder a builder for the application context
     * @return the application builder
     */
    override fun configure(builder: SpringApplicationBuilder): SpringApplicationBuilder {
        log.info(ENTERING_APPLICATION)
        val ret = builder.sources(BttfApplication::class.java)
        builder.bannerMode(Banner.Mode.OFF)
        return ret
    }

}

// import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession
// @EnableRedisHttpSession(maxInactiveIntervalInSeconds = 120, cleanupCron = "0 */5 * * * *", redisNamespace = "spring:session:bttf")