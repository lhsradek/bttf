package local.intranet.bttf.api.security

import java.io.IOException;
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import org.springframework.util.StringUtils

/**
 *
 * {@link LogoutSuccess} for
 * {@link local.intranet.bttf.api.security.SecurityConfig#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)}
 * <br>
 * https://www.baeldung.com/spring-security-logout
 *
 * @author Radek Kádner
 *
 */
class LogoutSuccess : LogoutSuccessHandler, SimpleUrlLogoutSuccessHandler() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     *
     * Logout info of user. Clean session, call super.onLogoutSuccess(request,
     * response, authentication) and write to logger event Logout.
     * <p>
     * Login is in
     * {@link local.intranet.bttf.api.controller.IndexController#signin}
     */
    override fun onLogoutSuccess(
        request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        try {
            if (request.cookies != null) {
                for (cookie in request.cookies) {
                    if (cookie != null) {
                        val cookieToDelete = Cookie(cookie.name, null)
                        cookieToDelete.setMaxAge(0)
                        response.addCookie(cookieToDelete)
                    }
                }
            }
            if (authentication.principal != null && authentication.name != null) {
                log.info("Logout username:'{}' refererUrl:'{}' sessionId:{}",
                    authentication.name, request.getHeader("Referer"), request.getSession().getId())
            }
            super.onLogoutSuccess(request, response, authentication)
            request.session.invalidate()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    /**
     *
     * Builds the target URL according to the logic defined in the main class
     * Javadoc.
     *
     * @param request  {@link javax.servlet.http.HttpServletRequest}
     * @param response {@link javax.servlet.http.HttpServletResponse}
     * @return {@link String}
     */
    override protected fun determineTargetUrl(request: HttpServletRequest, response: HttpServletResponse): String {
        if (isAlwaysUseDefaultTargetUrl()) {
            return getDefaultTargetUrl()
        }
        var targetUrl: String = ""
        if (targetUrlParameter != null) {
            targetUrl = request.getParameter(targetUrlParameter)
            if (StringUtils.hasText(targetUrl)) {
                return targetUrl
            }
        }
        if (!StringUtils.hasText(targetUrl)) {
            targetUrl = defaultTargetUrl
        }
        return targetUrl
    }

}