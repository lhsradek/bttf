package local.intranet.bttf.api.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License

import local.intranet.bttf.api.controller.StatusController

import org.slf4j.LoggerFactory

import org.springdoc.core.GroupedOpenApi

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *
 * {@link OpenApiConfig} for {@link local.intranet.bttf.BttfApplication}.
 *
 * @author Radek Kádner
 *
 */
@Configuration
@ConditionalOnExpression("\${bttf.springdoc.enabled}")
public class OpenApiConfig {

    private val log = LoggerFactory.getLogger(javaClass)

    private val API = "BTTF API"

    @Value("\${bttf.app.debug:false}")
    private lateinit var dbg: String // toBoolean

    @Autowired
    private lateinit var statusController: StatusController

    /**
     *
     * Grouped OpenApi info
     *
     * @return {@link GroupedOpenApi}
     */
    @Bean
    public fun groupedOpenApi(): GroupedOpenApi = GroupedOpenApi
        .builder()
            .pathsToMatch("/api/v1/**")
            .group("bttf")
            .displayName(API)
            .build()

    /**
     *
     * BTTF OpenApi
     *
     * @return {@link OpenAPI}
     */
    @Bean
    protected fun bttfOpenApi(): OpenAPI = OpenAPI()
        .info(
            Info().title(API)
                .description("Back to the Future API")
                .version(statusController.implementationVersion())
                .termsOfService("/bttf")
                .contact(
                    Contact()
                        .name("Radek Kádner")
                        .url("https://www.linkedin.com/in/radekkadner/")
                        .email("radek.kadner@gmail.com")
                )
                .license(
                    License()
                        .name("The MIT License")
                        .url("https://opensource.org/licenses/MIT")
                )
        ) /*
            .externalDocs(
                ExternalDocumentation()
                    .description("Java Documentation")
                    .url("/bttf-javadoc/")
            ) */

}
