package local.intranet.bttf.api.config

import com.fasterxml.jackson.databind.ObjectMapper

import java.util.TimeZone

import javax.servlet.ServletContext
import javax.servlet.SessionTrackingMode
import javax.sql.DataSource

import org.slf4j.LoggerFactory

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.web.session.HttpSessionEventPublisher
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer
import org.springframework.web.WebApplicationInitializer
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler

/**
 *
 * {@link ApplicationConfig} for
 * {@link local.intranet.bttf.BttfApplication}.
 * https://www.baeldung.com/database-auditing-jpa
 *
 * @author Radek Kádner
 *
 */
@Configuration
@EnableAutoConfiguration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class ApplicationConfig : WebApplicationInitializer, AbstractHttpSessionApplicationInitializer() {

    private val log = LoggerFactory.getLogger(javaClass)

    // @Value("\${bttf.app.debug:false}") private lateinit var dbg: String  // toBoolean

    @Autowired(required = false)
    private lateinit var flyway: Flyway

    @Autowired
    private lateinit var servletContext: ServletContext

    /**
     *
     * Primary data source
     *
     * @return {@link DataSource}
     */
    @ConfigurationProperties(prefix = "spring.datasource")
    @ConditionalOnExpression("\${#strings.length(spring.datasource.url) > 0}")
    public fun dataSource(): DataSource = DataSourceBuilder.create().build()

    /**
     *
     * Secondary data source
     * <p>
     * https://stackoverflow.com/questions/30337582/spring-boot-configure-and-use-two-datasources
     *
     * @return {@link DataSource}
     */
    @ConfigurationProperties(prefix = "spring.secondaryDatasource")
    @ConditionalOnExpression("\${#strings.length(spring.secondaryDatasource.url) > 0}")
    public fun secondaryDataSource(): DataSource = DataSourceBuilder.create().build()

    /**
     *
     * auditorProvider
     *
     * @return {@link AuditorAware}&lt;{@link String}&gt;
     */
    @Bean
    @ConditionalOnExpression("\${bttf.envers.enabled}")
    public fun auditorProvider(): AuditorAware<String> = AuditorAwareImpl()

    /**
     *
     * Set HttpSessionEventPublisher
     * <p>
     * https://www.baeldung.com/spring-security-session
     *
     * @return {@link HttpSessionEventPublisher}
     */
    @Bean
    public fun sessionEventPublisher(): HttpSessionEventPublisher {
        val ret = HttpSessionEventPublisher()
        servletContext.setSessionTrackingModes(mutableSetOf(SessionTrackingMode.COOKIE))
        // if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     * 
     * Is Flyway ?
     * 
     * @return true if flyway exists ${spring.flyway.enabled} == true
     */
    @Bean
    public fun isFlyway(): Boolean = flyway.info() != null

    /**
     *
     * faviconHandlerMapping
     * <p>
     * https://www.baeldung.com/spring-boot-favicon
     * <p>
     * <code>
     * <b>setUrlMap</b>(Collections.singletonMap("<b>/favicon.*</b>", faviconRequestHandler()))
     * </code>
     *
     * @return {@link SimpleUrlHandlerMapping}
     */
    @Bean
    public fun faviconHandlerMapping(): SimpleUrlHandlerMapping {
        val ret = SimpleUrlHandlerMapping()
        ret.setOrder(Int.MIN_VALUE)
        ret.setUrlMap(mapOf("/favicon.*" to faviconRequestHandler()))
        // if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * faviconRequestHandler
     * <p>
     * https://www.baeldung.com/spring-boot-favicon
     *
     * @return {@link ResourceHttpRequestHandler}
     */
    @Bean
    protected fun faviconRequestHandler(): ResourceHttpRequestHandler {
        val ret = ResourceHttpRequestHandler()
        ret.setLocationValues(arrayOf("res/").asList())
        // if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * DEFAULT_TIMEZONE objectMapper.setTimeZone(TimeZone.getDefault())
     *
     * @param objectMapper {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Autowired
    public fun configureJackson(objectMapper: ObjectMapper) {
        // val tzd = TimeZone.getDefault()
        // objectMapper.setTimeZone(tzd)
        // if (dbg.toBoolean()) log.debug("{}", tzd)
        objectMapper.setTimeZone(TimeZone.getDefault())
    }

}
