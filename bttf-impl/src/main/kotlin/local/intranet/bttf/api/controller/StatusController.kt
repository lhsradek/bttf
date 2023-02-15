package local.intranet.bttf.api.controller

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import java.lang.management.ManagementFactory
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Optional
import java.util.StringJoiner
import java.util.AbstractMap.SimpleEntry
import java.util.Locale
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest
import javax.servlet.ServletContext
import local.intranet.bttf.BttfApplication
import local.intranet.bttf.api.config.AuditorAwareImpl
import local.intranet.bttf.api.config.ApplicationConfig
import local.intranet.bttf.api.config.OpenApiConfig
import local.intranet.bttf.api.controller.IndexController
import local.intranet.bttf.api.domain.Contented
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.domain.Statusable
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.info.BeanInfo
import local.intranet.bttf.api.info.LevelCount
import local.intranet.bttf.api.info.ServiceCount
import local.intranet.bttf.api.info.content.Provider
import local.intranet.bttf.api.listener.AuthenticationSuccessEventListener
import local.intranet.bttf.api.listener.AuthenticationFailureListener
import local.intranet.bttf.api.listener.LogoutSuccess
import local.intranet.bttf.api.redis.RedisConfig
import local.intranet.bttf.api.redis.RedisMessagePublisher
import local.intranet.bttf.api.redis.RedisMessageSubscriber
import local.intranet.bttf.api.security.SecurityConfig
import local.intranet.bttf.api.service.CounterService
import local.intranet.bttf.api.service.JobService
import local.intranet.bttf.api.service.LoggingEventService
import local.intranet.bttf.api.service.MessageService
import local.intranet.bttf.api.service.RoleService
import local.intranet.bttf.api.service.UserService
import local.intranet.bttf.api.scheduler.JobFactory
import local.intranet.bttf.api.scheduler.SchedulerConfig
import org.quartz.JobDetail
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootVersion
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.core.SpringVersion
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.PropertySource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.scheduling.quartz.JobDetailFactoryBean
import org.springframework.scheduling.quartz.CronTriggerFactoryBean
import org.springframework.scheduling.quartz.SpringBeanJobFactory

/**
 *
 * {@link StatusController} for
 * {@link local.intranet.bttf.BttfApplication}
 *
 * @author Radek Kádner
 *
 */
@RestController
@RequestMapping("/api/v1/status")
@Tag(name = BttfConst.STATUS_TAG)
public class StatusController : Statusable {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${bttf.app.stage}")
    private lateinit var stage: String

    @Value("\${bttf.app.debug:false}")
    private lateinit var dbg: String // toBoolean

    @Value("\${bttf.app.emptyParams:false}")
    private lateinit var emptyParams: String // toBoolean

    @Autowired
    private lateinit var servletContext: ServletContext

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var schedulerConfig: SchedulerConfig

    @Autowired
    private lateinit var environment: Environment

    @Autowired
    private lateinit var httpServletRequest: HttpServletRequest

    private val STATUS_BRACKET: String = "_"
    private val STATUS_BRACKETS: String = "__"
    private val STATUS_BTTF_SEC: String = "bttf.sec"
    private val STATUS_PASSWORD: String = "password"
    private val STATUS_SECRET: String = "secret"
    private val STATUS_USER_NAME: String = "username"
    private val STATUS_SECURITY_USER_NAME: String = "security.user.name"
    private val STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_FRAMEWORK_SERVLET_CONTEXT_DISPATCHER_SERVLET: String =
        "org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcherServlet"
    private val STATUS_ORG_APACHE_CATALINA_JSP_CLASSPATH: String = "org.apache.catalina.jsp_classpath"
    private val STATUS_ORG_APACHE_TOMCAT_UTIL_NET_SECURE_REQUESTED_PROTOCOL_VERSIONS: String =
        "org.apache.tomcat.util.net.secure_requested_protocol_versions"
    private val STATUS_ORG_APACHE_TOMCAT_UTIL_NET_SECURE_REQUESTED_CIPHERS: String =
        "org.apache.tomcat.util.net.secure_requested_ciphers"
    private val STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_DISPATCHERSERVLET_OUTPUT_FLASH_MAP: String =
        "org.springframework.web.servlet.DispatcherServlet.OUTPUT_FLASH_MAP"
    private val STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_BESTMATCHINGHANDLER: String =
        "org.springframework.web.servlet.HandlerMapping.bestMatchingHandler"
    private val STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_PATHWITHINHANDLERMAPPING: String =
        "org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping"
    private val STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_URITEMPLATEVARIABLES: String =
        "org.springframework.web.servlet.HandlerMapping.uriTemplateVariables"
    private val STATUS_JAVAX_SERVLET_REQUEST_SSL_SESSION_ID: String = "javax.servlet.request.ssl_session_id"
    private val STATUS_APPLICATION_CONFIG_PROPERTIES: String =
        "Config resource 'class path resource [application.properties]' via location 'optional:classpath:/'"
    private val STATUS_APPLICATION_CONFIG_PROFILE_PROPERTIES: String =
        "Config resource 'class path resource [application-%s.properties]' via location 'optional:classpath:/'"
    private val STATUS_SERVLET_CONTEXT: String = "@\\w+"
    private val STATUS_BEAN: String = "%s:%s"
    private val STATUS_FORMAT_BEAN: String = "%s:<strong class=\"data\">%s</strong>"

    /**
     *
     * Get status
     *
     * @return {@link StatusType}
     */
    public override fun getStatus(): StatusType = StatusType.UP

    /**
     *
     * text/plain: "OK" if BTTF API is running
     *
     * @see <a href="/bttf/swagger-ui/#/status-controller/getPlainStatus" target=
     *      "_blank">bttf/swagger-ui/#/status-controller/getPlainStatus</a>
     *
     * @return {@link String}
     */
    @GetMapping(value = arrayOf("/status"), produces = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    @Operation(
        operationId = "plainStatus",
        summary = "Plain Status",
        description = "Get OK if BTTF API is running\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/StatusController.html#getPlainStatus()\" "
        // + "target=\"_blank\">StatusController.getPlainStatus</a>",
        tags = arrayOf(BttfConst.STATUS_TAG)
    )
    public fun plainStatus(): String = "OK"

    /**
     *
     * Info of Tomcat Environment.
     * <p>
     * Accessible to the
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     *
     * @see <a href="/bttf/swagger-ui/#/status-controller/bttfEnvironment"
     *      target=
     *      "_blank">bttf/swagger-ui/#/status-controller/bttfEnvironment</a>
     *
     * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link String}&gt;&gt;
     */
    @GetMapping(value = arrayOf("/environment"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "bttfEnvironment",
        summary = "Environment",
        description = "Get Environment\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/StatusController.html#"
        // + "bttfEnvironment()\" "
        // + "target=\"_blank\">StatusController.bttfEnvironment</a>",
        tags = arrayOf(BttfConst.STATUS_TAG)
    )
    @PreAuthorize("hasRole('ROLE_adminRole')")
    public fun bttfEnvironment(): List<Map.Entry<String, String>> {
        val ret = mutableListOf<Map.Entry<String, String>>()
        System.getenv().filter {
            (emptyParams.toBoolean() || it.value.length > 0) and !it.key.equals(STATUS_BRACKET) // nelíbí
        }.toSortedMap(java.lang.String.CASE_INSENSITIVE_ORDER).forEach {
            ret.add(SimpleEntry<String, String>(it.key, it.value))
        }
        return ret
    }

    /**
     *
     * Info of config params in .properties
     * <p>
     * Accessible to the
     * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
     *
     * @see <a href="/bttf/swagger-ui/#/status-controller/bttfProperties"
     *      target=
     *      "_blank">bttf/swagger-ui/#/status-controller/bttfProperties</a>
     *
     * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link String}&gt;&gt;
     */
    @GetMapping(value = arrayOf("/properties"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "bttfProperties",
        summary = "Properties",
        description = "Get Properties\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/StatusController.html#"
        // + "bttfProperties()\" "
        // + "target=\"_blank\">StatusController.bttfProperties</a>",
        tags = arrayOf(BttfConst.STATUS_TAG)
    )
    @PreAuthorize("hasRole('ROLE_adminRole')")
    public fun bttfProperties(): List<Map.Entry<String, String>> {
        val ret = mutableListOf<Map.Entry<String, String>>()
        val mps: MutablePropertySources = (environment as ConfigurableEnvironment).propertySources
        val map = mutableMapOf<String, String>()
        mutableListOf<PropertySource<*>?>(
            mps.get(STATUS_APPLICATION_CONFIG_PROPERTIES),
            mps.get(String.format(STATUS_APPLICATION_CONFIG_PROFILE_PROPERTIES, environment.activeProfiles.first()))
        ).forEach {
            it?.let {
                @Suppress("UNCHECKED_CAST")
                for ((key, any) in it.source as Map<String, *>) {
                    val value = any.toString()
                    if ((emptyParams.toBoolean() || value.length > 0) and !key.equals(STATUS_BRACKET)) {
                        if (key.startsWith(STATUS_BTTF_SEC) || key.contains(STATUS_PASSWORD) || key.contains(
                                STATUS_SECRET
                            ) ||
                            key.contains(STATUS_USER_NAME) || key.contains(STATUS_SECURITY_USER_NAME)
                        ) { // nelíbí
                            map[key] = BttfConst.PROTECTED
                        } else map[key] = value
                    }
                }
            }
        }
        map.toSortedMap(java.lang.String.CASE_INSENSITIVE_ORDER).forEach {
            ret.add(SimpleEntry<String, String>(it.key, it.value))
        }
        return ret
    }

    /**
     *
     * Info of Http servlet request
     * <p>
     * Accessible to the
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     *
     * @see <a href=
     *      "/bttf/swagger-ui/#/status-controller/bttfHttpServletRequest"
     *      target=
     *      "_blank">bttf/swagger-ui/#/status-controller/bttfHttpServletRequest</a>
     *
     * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link String}&gt;&gt;
     */
    @GetMapping(value = arrayOf("/httpServletRequest"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "bttfHttpServletRequest",
        summary = "HttpServletRequest",
        description = "Get HttpServletRequest\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/StatusController.html#"
        // + "bttfHttpServletRequest()\" "
        // + "target=\"_blank\">StatusController.bttfHttpServletRequest</a>",
        tags = arrayOf(BttfConst.STATUS_TAG)
    )
    @PreAuthorize("hasRole('ROLE_adminRole')")
    @Synchronized
    public fun bttfHttpServletRequest(): List<Map.Entry<String, String>> {
        val ret = mutableListOf<Map.Entry<String, String>>()
        val map = mutableMapOf<String, String>()
        for (key in httpServletRequest.attributeNames.toList()) {
            val value = httpServletRequest.getAttribute(key).toString()
            if ((value.length > 0 || emptyParams.toBoolean()) && !(key.startsWith(STATUS_BRACKET) ||
                        key.contains("@") || value.contains("@") ||
                        /*
                         * __spring_security_filterSecurityInterceptor_filterApplied:true
                         * __spring_security_scpf_applied:true
                         * __spring_security_session_mgmt_filter_applied:true
                             */
                        key.equals(STATUS_ORG_APACHE_TOMCAT_UTIL_NET_SECURE_REQUESTED_CIPHERS) ||
                        /*
                         * org.springframework.web.servlet.DispatcherServlet.OUTPUT_FLASH_MAP: FlashMap
                         * [attributes={}, targetRequestPath=null, targetRequestParams={}]
                         */
                        key.equals(STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_DISPATCHERSERVLET_OUTPUT_FLASH_MAP) ||
                        /*
                         * org.springframework.web.servlet.HandlerMapping.bestMatchingHandler:
                         */
                        key.equals(STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_BESTMATCHINGHANDLER) ||
                        /*
                         * duplicity /status in
                         * org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping
                         * org.springframework.web.servlet.HandlerMapping.bestMatchingPattern:/status
                         * org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping:/
                         * status:/status
                         */
                        key.equals(STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_PATHWITHINHANDLERMAPPING) ||
                        /*
                         * empty {}
                         * org.springframework.web.servlet.HandlerMapping.uriTemplateVariables:{}
                         */
                        key.equals(STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_URITEMPLATEVARIABLES) ||
                        /*
                         * duplicity /status in
                         * org.apache.tomcat.util.net.secure_protocol_version:TLSv1.3
                         * org.apache.tomcat.util.net.secure_requested_protocol_versions:Unknown(0x9a9a)
                         * ,TLSv1.3,TLSv1.2
                         */
                        key.equals(STATUS_ORG_APACHE_TOMCAT_UTIL_NET_SECURE_REQUESTED_PROTOCOL_VERSIONS) ||
                        key.equals(STATUS_JAVAX_SERVLET_REQUEST_SSL_SESSION_ID))
            ) { // nelíbí
                map[key] = value
            }
        }
        map.toSortedMap(java.lang.String.CASE_INSENSITIVE_ORDER).forEach {
            ret.add(SimpleEntry<String, String>(it.key, it.value))
        }
        return ret
    }

    /**
     *
     * Info of Http servlet request
     * <p>
     * Accessible to the
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     *
     * @see <a href=
     *      "/bttf/swagger-ui/#/status-controller/bttfHttpServletRequest"
     *      target=
     *      "_blank">bttf/swagger-ui/#/status-controller/bttfHttpServletRequest</a>
     *
     * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link String}&gt;&gt;
     */
    @GetMapping(value = arrayOf("/servletContext"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "bttfServletContext",
        summary = "ServletContext",
        description = "Get ServletContext\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/StatusController.html#"
        // + "bttfServletContext()\" "
        // + "target=\"_blank\">StatusController.bttfServletContext</a>",
        tags = arrayOf(BttfConst.STATUS_TAG)
    )
    @PreAuthorize("hasRole('ROLE_adminRole')")
    public fun bttfServletContext(): List<Map.Entry<String, String>> {
        val ret = mutableListOf<Map.Entry<String, String>>()
        val map = mutableMapOf<String, String>()
        for (key in servletContext.attributeNames.toList()) {
            val value = servletContext.getAttribute(key).toString()
            if ((value.length > 0 || emptyParams.toBoolean()) && !(key.startsWith(STATUS_BRACKET) ||
                        key.equals(STATUS_ORG_APACHE_CATALINA_JSP_CLASSPATH)) ||
                /*
                 * duplicity org.springframework.boot.web.servlet.context.
                 * AnnotationConfigServletWebServerApplicationContext in
                 * org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcherServlet
                 * org.springframework.web.context.WebApplicationContext.ROOT:
                 * org.springframework.boot.web.servlet.context.
                 * AnnotationConfigServletWebServerApplicationContext, started on Tue Jan 18
                 * 11:45:22 CET 2022
                 * org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcherServlet:
                 * org.springframework.boot.web.servlet.context.
                 * AnnotationConfigServletWebServerApplicationContext, started on Tue Jan 18
                 * 11:45:22 CET 2022
                 */
                key.equals(STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_FRAMEWORK_SERVLET_CONTEXT_DISPATCHER_SERVLET)
            // || value.length() > 0
            ) { // nelíbí
                map[key] = value.replaceFirst(STATUS_SERVLET_CONTEXT, "")
            }
        }
        map.toSortedMap(java.lang.String.CASE_INSENSITIVE_ORDER).forEach {
            ret.add(SimpleEntry<String, String>(it.key, it.value))
        }
        return ret
    }

    /**
     *
     * Info of Http servlet request
     * <p>
     * Accessible to the
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     *
     * @see <a href=
     *      "/bttf/swagger-ui/#/status-controller/propertiesAPIBean"
     *      target=
     *      "_blank">bttf/swagger-ui/#/status-controller/propertiesAPIBean</a>
     *
     * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link String}&gt;&gt;
     */
    @PreAuthorize("hasRole('ROLE_adminRole')")
    @Synchronized
    public fun propertiesAPIBean(): List<Map.Entry<String, String>> {
        val ret = mutableListOf<Map.Entry<String, String>>()
        val map = mutableMapOf<String, String>()
        for (beanName in applicationContext.beanDefinitionNames) {
            val bean = applicationContext.getBean(beanName)
            var cl = bean.javaClass
            var name = cl.superclass.simpleName
            if (name.equals("Object")) {
                name = cl.simpleName
                cl = cl.superclass
            }
            if (isBeanSuitable(cl)) {
                var set = mutableSetOf<String>()
                cl.declaredMethods.filter {
                    Modifier.isPublic(it.modifiers) && !Modifier.isStatic(it.modifiers)
                }.forEach {
                    set.addAll(makeAPIBeans(it, bean, true))
                }
                val href = "[${name}]"
                val setStr = set.toSortedSet(java.lang.String.CASE_INSENSITIVE_ORDER)
                    .joinToString(separator = BttfConst.BLANK_SPACE)
                if (setStr.length > 0) {
                    map[name] = "${href}|${setStr}"
                } else map[name] = "${href}|"
            }
        }
        map.toSortedMap(java.lang.String.CASE_INSENSITIVE_ORDER).forEach {
            val s = it.value.split(BttfConst.PIPE_LINE)
            if (s.size == 2) {
                ret.add(SimpleEntry<String, String>("${s[0]} ", s[1]))
            } else if (s.size == 1) {
                if (emptyParams.toBoolean()) {
                    ret.add(SimpleEntry<String, String>(s[0], ""))
                }
            }
        }
        // log.debug("propertiesAPIBean {}", ret)
        return ret
    }

    /**
     *
     * Info of BTTF API beans
     * <p>
     * Accessible to the {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
     *
     * @return {@link Map}&lt;{@link String},{@link Any}&gt;
     */
    @PreAuthorize("hasRole('ROLE_adminRole')")
    @Synchronized
    public fun bttfAPIBean(): BeanInfo {
        val ret = mutableMapOf<String, Any>()
        for (beanName in applicationContext.beanDefinitionNames) {
            val bean = applicationContext.getBean(beanName)
            var cl = bean.javaClass
            var name = cl.superclass.simpleName
            if (name.equals("Object")) {
                name = cl.simpleName
                cl = cl.superclass
            }
            if (isBeanSuitable(cl)) {
                var set = mutableSetOf<String>()
                cl.declaredMethods.filter {
                    Modifier.isPublic(it.modifiers) && !Modifier.isStatic(it.modifiers)
                }.forEach {
                    set.addAll(makeAPIBeans(it, bean, false))
                }
                val str = StringJoiner(BttfConst.PIPE_LINE)
                set.toSortedSet(java.lang.String.CASE_INSENSITIVE_ORDER).filter {
                    it.length > 0
                }.forEach {
                    str.add(it)
                }
                val map = mutableMapOf<String, Any>()
                if (str.toString().length > 0) {
                    for (s in str.toString().split(BttfConst.PIPE_LINE)) {
                        val l = s.split(":")
                        if (l.size > 1) {
                            val exp = l.subList(1, l.size).joinToString(separator = ":")
                            if (l.first().equals("authoritiesRoles")) {
                                val m = mutableListOf<String>()
                                if (exp.length > 2) {
                                    for (p in exp.substring(1, exp.length - 1).split(", ")) {
                                        m.add(p);
                                    }
                                }
                                map[l.first()] = m
                            } else if (arrayOf<String>(
                                    "countTotalLoggingEvents", "countTotalMessageEvents", "operatingSystem"
                                ).contains(l.first())
                            ) {
                                val m = mutableMapOf<String, Any>()
                                if (exp.length > 2) {
                                    for (p in exp.substring(1, exp.length - 1).split(", ")) {
                                        val b = p.split("=")
                                        m[b[0]] = b[1]
                                    }
                                }
                                map[l.first()] = m
                            } else map[l.first()] = exp
                        } else map[s] = ""
                    }
                    ret[name] = map.toSortedMap(java.lang.String.CASE_INSENSITIVE_ORDER)
                } else ret[name] = ""
            }
        }
        return BeanInfo(ret.toSortedMap(java.lang.String.CASE_INSENSITIVE_ORDER))
    }

    /**
     *
     * Implementation version
     *
     * @return {@link String}
     */
    public fun implementationVersion(): String = Optional
        .ofNullable(BttfApplication::class.java.`package`.implementationVersion).orElse(BttfConst.UNKNOWN)

    /**
     *
     * SpringVersion version
     *
     * @return {@link String}
     */
    public fun springVersion(): String {
        val ret = SpringVersion.getVersion()
        ret?.let {
            return ret
        } ?: return BttfConst.UNKNOWN
    }

    /**
     *
     * SpringBootVersion version
     *
     * @return {@link String}
     */
    public fun springBootVersion(): String {
        val ret = SpringBootVersion.getVersion()
        ret?.let {
            return ret
        } ?: return BttfConst.UNKNOWN
    }

    /**
     *
     * Get Operating System
     *
     * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link String}&gt;&gt;
     */
    @PreAuthorize("hasRole('ROLE_adminRole')")
    @Synchronized
    public fun operatingSystem(): List<Map.Entry<String, String>> {
        val ret = mutableListOf<Map.Entry<String, String>>()
        val system = ManagementFactory.getOperatingSystemMXBean()
        ret.add(SimpleEntry<String, String>("name", "${system.name}"))
        ret.add(
            SimpleEntry<String, String>(
                "loadAverage",
                DecimalFormat("#.##", DecimalFormatSymbols(Locale.US)).format(system.systemLoadAverage)
            )
        )
        // ret.add(SimpleEntry<String, String>("arch", "${system.arch}"))
        // ret.add(SimpleEntry<String, String>("processors", "${system.availableProcessors}"))
        // ret.add(SimpleEntry<String, String>("version", "${system.version}"))
        return ret
    }

    /**
     *
     * Session id
     *
     * @return {@link String}
     */
    @Synchronized
    public fun sessionId(): String {
        val session = httpServletRequest.getSession(false)
        val ret = session?.let {
            session.id?.let {
                session.id
            } ?: ""
        } ?: ""
        return ret;
    }

    /**
     *
     * Stage from ${bttf.app.stage}
     *
     * @return {@link String}
     */
    public fun stage(): String = stage

    /**
     *
     * Active profiles
     *
     * @return {@link String}
     */
    public fun activeProfiles(): String = environment.activeProfiles.joinToString(separator = BttfConst.BLANK_SPACE)

    /**
     *
     * Server name
     *
     * @return {@link String}
     */
    public fun serverName(): String = virtualServerName().split(BttfConst.SLASH).last()

    /**
     *
     * Server software
     *
     * @return {@link String}
     */
    public fun serverSoftware(): String = serverInfo().split(BttfConst.SLASH).first()

    /**
     *
     * Get virtualServerName from ServletContext.getVirtualServerName()
     *
     * @return {@link String}
     */
    protected fun virtualServerName(): String = servletContext.virtualServerName

    /**
     *
     * Server info from ServletContext.getServerInfo()
     *
     * @return {@link String}
     */
    protected fun serverInfo(): String = servletContext.serverInfo

    /**
     *
     * Get host name
     *
     * @return {@link String}
     */
    public fun hostName(): String = serverName().split(".").first()

    /**
     *
     * Get startup date
     *
     * @return {@link ZonedDateTime} from RequestContextUtils
     *         ApplicationContext.getStartupDate()
     */
    public fun startupDate(): ZonedDateTime {
        if (applicationContext.getStartupDate() == 0L) {
            implementationVersion()
        }
        return ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(
                applicationContext.getStartupDate()
            ),
            ZoneId.systemDefault()
        )
    }

    /**
     *
     * Get time zone
     *
     * @return {@link TimeZone#getDefault()}.getID() with {@link TimeZone#getID()}
     */
    public fun timeZone(): String = ZoneId.systemDefault().id

    /**
     *
     * client IP
     *
     * @return {@link String}
     */
    @Synchronized
    public fun clientIP(): String {
        try {
            val xfHeader = httpServletRequest.getHeader("X-Forwarded-For")
            val ret = xfHeader?.let {
                xfHeader.split(",").first()
            } ?: httpServletRequest.remoteAddr
            return ret
        } catch (e: Exception) {
            return getIpv4HostAddress()
        }
    }

    /**
     *
     * https://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device-from-code
     */
    @Synchronized
    public fun getIpv4HostAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let {
                return it.hostAddress
            }
        }
        return BttfConst.UNKNOWN
    }

    /**
     *
     * For {@link #bttfAPIBean} and {@link #propertiesAPIBean}
     * <p>
     * Used {@link StatusController}, {@link ApplicationConfig}
     * {@link UserService}
     * ... all in
     * {@link local.intranet.bttf.api.service}
     * <p>
     *
     * @param method   {@link Method}
     * @param bean     {@link Object}
     * @param isFormat {@link Boolean}
     * @return {@link Set}&lt;{@link String}&gt;
     */
    @Synchronized
    public fun makeAPIBeans(method: Method, bean: Any, isFormat: Boolean): Set<String> {
        val ret = mutableSetOf<String>()
        var cl = bean.javaClass.superclass
        if (cl.simpleName.equals("Object")) {
            cl = bean.javaClass
        }
        val name = method.name
        val strFormat = if (isFormat) STATUS_FORMAT_BEAN else STATUS_BEAN
        // methods without parameters returning non empty String
        val met = arrayOf(
            // {@link StatusController}
            "timeZone",
            "activeProfiles",
            "implementationVersion",
            "springVersion",
            "springBootVersion",
            "hostName",
            "serverName",
            "stage",
            "serverSoftware",
            "plainStatus",
            "clientIP",
            "getIpv4HostAddress",
            // {@link UserService}
            "isAuthenticated"
        )
        if (isNiceBeanName(name)) {
            if (met.contains(name)) {
                ret.add(String.format(strFormat, name, cl.getMethod(name).invoke(bean)))
            } else when (name) {

                "springBeanJobFactory" -> {
                    // val any = cl.getMethod(name).invoke(bean) as Any
                    // ret.add(String.format(strFormat, name, any.javaClass.simpleName))
                    val jobFactory = cl.getMethod(name).invoke(bean) as SpringBeanJobFactory
                    ret.add(String.format(strFormat, name, jobFactory.javaClass.simpleName))
                }

                "jobDetail" -> {
                    // val any = cl.getMethod(name).invoke(bean) as Any
                    // ret.add(String.format(strFormat, name, any.javaClass.superclass.simpleName))
                    val jobDetail = cl.getMethod(name).invoke(bean) as JobDetailFactoryBean
                    jobDetail.`object`?.let {
                        ret.add(String.format(strFormat, name, jobDetail.`object`!!.jobClass.simpleName))
                    } ?: ret.add(String.format(strFormat, name))
                }

                "trigger" -> {
                    val jobDetail = cl.getMethod("jobDetail").invoke(bean) as JobDetailFactoryBean
                    val trigger = cl.getMethod(name, JobDetail::class.java)
                        .invoke(bean, jobDetail.`object`) as CronTriggerFactoryBean
                    trigger.`object`?.let {
                        ret.add(
                            String.format(
                                strFormat, name,
                                trigger.`object`!!.jobKey.name.replaceFirst("DEFAULT\\.", "")
                            )
                        )
                    } ?: ret.add(String.format(strFormat, name))
                }

                "username",
                "sessionId" -> { // no epmty String
                    val str = cl.getMethod(name).invoke(bean) as String
                    if (str.length > 0) {
                        ret.add(String.format(strFormat, name, str))
                    }
                }

                "countValue" -> { // no zerro Long
                    val long = cl.getMethod(name).invoke(bean) as Long
                    if (long > 0) {
                        ret.add(String.format(strFormat, name, "${long}"))
                    }
                }

                "lastInvocation",  // as ZonedDateTime
                "startupDate" -> {
                    val zoneDateTime = cl.getMethod(name).invoke(bean) as ZonedDateTime
                    ret.add(
                        String.format(
                            strFormat, name,
                            zoneDateTime.format(
                                DateTimeFormatter.ofPattern(
                                    if (isFormat) {
                                        Contented.CONTENT_DATE_FORMAT
                                    } else Contented.CONTENT_DATE_REST_FORMAT
                                )
                            )
                        )
                    )
                }

                "bttfEnvironment", // size of list
                "bttfHttpServletRequest",
                "bttfServletContext",
                "bttfProperties",
                "countTotalCounterName" -> {
                    @Suppress("UNCHECKED_CAST")
                    val list = cl.getMethod(name).invoke(bean) as List<Map.Entry<String, String>>
                    ret.add(String.format(strFormat, name, list.size))
                }

                // {@link UserService}
                "authoritiesRoles" -> { // as List<String>
                    @Suppress("UNCHECKED_CAST")
                    val list = cl.getMethod(name).invoke(bean) as List<String>
                    ret.add(String.format(strFormat, name, "${list}"))
                }

                // {@link UserService}
                "operatingSystem" -> {  // as Map.Entry
                    val events = mutableListOf<Map.Entry<String, String>>()

                    @Suppress("UNCHECKED_CAST")
                    val list = cl.getMethod(name).invoke(bean) as List<Map.Entry<String, String>>
                    list.forEach {
                        events.add(SimpleEntry<String, String>(it.key, it.value))
                    }
                    if (events.size > 0) {
                        ret.add(String.format(strFormat, name, "${events}"))
                    } else ret.add(name)
                }

                // {@link IndexController}
                // {@link StatusController}
                // {@link JobService}
                // {@link MessageService}
                // {@link UserService}
                "getStatus" -> {  // as StatusType
                    when (bean) {
                        is IndexController,
                        is StatusController,
                        is JobService,
                        is MessageService,
                        is UserService -> {
                            val status = cl.getMethod(name).invoke(bean) as StatusType
                            ret.add(String.format(strFormat, name, "${status}"))
                        }
                        else -> ret.add(name)
                    }
                }

                // {@link LoggingEventService}
                "countTotalLoggingEvents" -> {  // as Map.Entry
                    when (bean) {
                        is LoggingEventService -> {
                            val event = mutableListOf<Map.Entry<String, Long>>()

                            @Suppress("UNCHECKED_CAST")
                            val list = cl.getMethod(name).invoke(bean) as List<LevelCount>
                            list.forEach {
                                event.add(SimpleEntry<String, Long>(it.level, it.total))
                            }
                            if (event.size > 0) {
                                // log.debug("MakeApiBeans::countTotalLoggingEvents {}", event)
                                ret.add(String.format(strFormat, name, "${event}"))
                            } else ret.add(name)
                        }
                        else -> ret.add(name)
                    }
                }

                // {@link MessageService}
                "countTotalMessageEvents" -> {  // as Map.Entry - is disabled by isNiceBeanName for duplicity info
                    when (bean) {
                        is MessageService -> {
                            val event = mutableListOf<Map.Entry<String, Long>>()

                            @Suppress("UNCHECKED_CAST")
                            val list = cl.getMethod(name).invoke(bean) as List<ServiceCount>
                            list.forEach {
                                event.add(SimpleEntry<String, Long>(it.serviceName, it.total))
                            }
                            if (event.size > 0) {
                                log.debug("MakeApiBeans::countTotalMessageEvents {}", event)
                                ret.add(String.format(strFormat, name, "${event}"))
                            } else ret.add(name)
                        }
                        else -> ret.add(name)
                    }
                }
                else -> ret.add(name)
            }
        }
        // log.debug("MakeApiBeans {}", ret)
        return ret
    }

    /**
     *
     * Get true if nice
     *
     * @param name          {@String}
     * @return boolean if nice
     */
    public fun isNiceBeanName(name: String): Boolean {
        val ret: Boolean
        when (name) {
            "init",
            "jedisConnectionFactory",
            "faviconHandlerMapping",
            "setBeanFactory",
            "sessionEventPublisher",
            "localeResolver",
            "localeChangeInterceptor",
            "auditorProvider",
            "userRoles",
            "toString",
            "toProxyConfigString",
            "addAdvice",
            "addAdvisor",
            "equals",
            "getAdvisors",
            "getAdvisorCount",
            "getCallback",
            "getCallbacks",
            "getProxiedInterfaces",
            "getTargetClass",
            "getTargetSource",
            "hashCode",
            "indexOf",
            "onLogoutSuccess",
            "isExposeProxy",
            "isFrozen",
            "isInterfaceProxied",
            "isPreFiltered",
            "isProxyTargetClass",
            "newInstance",
            "removeAdvice",
            "removeAdvisor",
            "replaceAdvisor",
            "setCallback",
            "setCallbacks",
            "setExposeProxy",
            "setPreFiltered",
            "setTargetSource",
            "getPassword",
            "isBlocked",
            "isNiceBeanName",
            "isBeanSuitable",
            "logoutSuccess",
            "loadUserByUsername",
            "lastInvocationFromAudit",
            "countTotalMessageEvents",  // for duplicity info
            "makeAPIBeans",
            "propertiesAPIBean",
            "bttfAPIBean" -> ret = false
            else -> ret = true
        }
        // println("IsNiceBeanName ${ret}")
        return ret
    }

    /**
     *
     * Is Bean suitable?
     *
     * @param cl {@link Class}&lt;? extends {@link Any}&gt;
     * @return boolean
     */
    public fun isBeanSuitable(cl: Class<Any>): Boolean {
        val ret: Boolean
        val arr = arrayOf<String>(
            AuditorAwareImpl::class.java.simpleName,
            AuthenticationFailureListener::class.java.simpleName,
            AuthenticationSuccessEventListener::class.java.simpleName,
            JobFactory::class.java.simpleName,
            LogoutSuccess::class.java.simpleName,
            RedisMessagePublisher::class.java.simpleName,
            RedisMessageSubscriber::class.java.simpleName
        )
        with(cl.simpleName) {
            if (cl.name.startsWith(BttfApplication::class.java.`package`.name)
                && !(arr.contains(cl.simpleName))
                && !(startsWith(BttfApplication::class.java.simpleName) ||
                        startsWith(ApplicationConfig::class.java.simpleName) ||
                        startsWith(OpenApiConfig::class.java.simpleName) ||
                        startsWith(RedisConfig::class.java.simpleName) ||
                        startsWith(SecurityConfig::class.java.simpleName) ||
                        startsWith(CounterService::class.java.simpleName) ||
                        startsWith(RoleService::class.java.simpleName))
            ) {
                // log.debug("{} {}", BttfApplication::class.java.`package`.name, cl.superclass.simpleName)
                ret = true
            } else ret = false
        }
        return ret
    }

}
