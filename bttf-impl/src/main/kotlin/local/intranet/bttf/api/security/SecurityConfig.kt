package local.intranet.bttf.api.security

import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.exception.BttfException
import local.intranet.bttf.api.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

/**
 *
 * {@link SecurityConfig} for {@link local.intranet.bttf.BttfApplication}.
 * <p>
 * https://www.baeldung.com/spring-security-csp <br>
 * https://www.baeldung.com/spring-security-session <br>
 * https://www.baeldung.com/spring-security-cors-preflight <br>
 * https://www.baeldung.com/spring-security-basic-authentication <br>
 * https://www.baeldung.com/spring-security-login <br>
 * https://www.baeldung.com/spring-security-logout <br>
 * https://stackoverflow.com/questions/24057040/content-security-policy-spring-security
 * https://docs.spring.io/spring-security/reference/servlet/configuration/kotlin.html
 * https://www.baeldung.com/kotlin/spring-security-dsl
 *
 *
 * @author Radek Kádner
 *
 */
@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    // jsr250Enabled = true,
    prePostEnabled = true
)
public class SecurityConfig {

    @Value("#{'\${bttf.app.authenticated}'.split('\\s{1,}')}")
    private lateinit var authenticated: List<String>

    @Value("#{'\${bttf.app.permitAll}'.split('\\s{1,}')}")
    private lateinit var permitAll: List<String>

    @Autowired
    private lateinit var userService: UserService

    /**
     *
     * Set {@link CorsConfiguration}
     *
     * @return {@link CorsFilter}
     */
    @Bean
    public fun corsFilter(): CorsFilter {
        val source: UrlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
        val config: CorsConfiguration = CorsConfiguration()
        config.setAllowCredentials(true)
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        val ret = CorsFilter(source)
        return ret
    }
    
    /**
     *
     * Create AuthenticationManager
     * <p>
     * For {@link local.intranet.bttf.api.controller.IndexController#signin}
     *
     * @return {@link AuthenticationManager}
     * @throws {@link BttfException}
     */
    @Bean
    @Primary
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        try {
            val ret: AuthenticationManager = authenticationConfiguration.authenticationManager
            return ret
        } catch (e: Exception) {
            // A disguised exception
            e.message?.let {
                throw BttfException(e.message!!)
            } ?: throw BttfException(
                e::class.java.simpleName + " " + BttfConst.ERROR_ACCOUNT_EXPIRED
            )
        }
    }

    /**
     *
     * Set PasswordEncoder
     *
     * @return {@link PasswordEncoder}
     */
    @Bean
    public fun passwordEncoder(): PasswordEncoder {
        val ret: PasswordEncoder = BCryptPasswordEncoder()
        return ret
    }

    /**
     *
     * Configure AuthenticationManagerBuilder
     *
     * @param auth {@link AuthenticationManagerBuilder}
     * @throws {@link Exception}
     */
    @Throws(Exception::class)
    public fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder())
    }

    /**
     *
     * Configure {@link HttpSecurity}
     * <p>
     * {@link local.intranet.bttf.api.listener.LogoutSuccess} invalidates
     * {@link javax.servlet.http.HttpSession}.
     *
     * @param http {@link HttpSecurity}
     * @return {@link SecurityFilterChain}
     * @throws {@link Exception}
     */
    @Bean
    @Throws(Exception::class)
    public fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors().and().csrf().disable()
            .authorizeRequests { authorizeRequests ->
                permitAll.filter { it.length > 0 }
                    .forEach { authorizeRequests.antMatchers(it).permitAll() }
                authenticated.filter { it.length > 0 }
                    .forEach { authorizeRequests.antMatchers(it).authenticated() }
            }
            .headers()
            .xssProtection()
            .and()
            .contentSecurityPolicy("script-src 'self'; object-src 'self'; form-action 'self'; style-src 'self'")
            .and().cacheControl()
            .and()
            .httpStrictTransportSecurity()
            .and()
            .frameOptions().disable().frameOptions().sameOrigin()
            .and().httpBasic()
            .and().formLogin()
            .loginPage("/login").permitAll().failureUrl("/login?error=true")
            .and().exceptionHandling().accessDeniedPage("/login?error=403")
            .and()
            .logout().logoutSuccessHandler(userService.logoutSuccess())
            .logoutRequestMatcher(AntPathRequestMatcher("/logout")).logoutSuccessUrl("/")
            .invalidateHttpSession(true).deleteCookies("JSESSIONID").and().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS).sessionFixation().migrateSession()
            .maximumSessions(1)
        return http.build()
    }

}
