package local.intranet.bttf.api.domain.type

/**
 *
 * {@link RoleType} for
 * {@link local.intranet.bttf.api.config.ApplicationConfig} and
 * {@link local.intranet.bttf.api.service.UserService}
 * <p>
 * ANONYMOUS_ROLE, USER_ROLE, MANAGER_ROLE, ADMIN_ROLE
 *
 * @author Radek Kádner
 */
enum class RoleType(val role: String) {

    /**
     *
     * ANONYMOUS_ROLE = "anonymousRole"
     */
    ANONYMOUS_ROLE("anonymousRole"),

    /**
     *
     * USER_ROLE = "userRole"
     */
    USER_ROLE("userRole"),

    /**
     *
     * MANAGER_ROLE = "managerRole"
     */
    MANAGER_ROLE("managerRole"),

    /**
     *
     * ADMIN_ROLE = "adminRole"
     */
    ADMIN_ROLE("adminRole")

}
