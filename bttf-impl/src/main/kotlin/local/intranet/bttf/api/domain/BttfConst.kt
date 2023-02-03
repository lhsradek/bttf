package local.intranet.bttf.api.domain

/**
 *
 * {@link Controller} for
 * {@link local.intranet.bttf.api.controller} and thymeleaf
 *
 * @author Radek Kádner
 *
 */
object BttfConst {

    /**
     *
     * STATUS_TAG = "status-controller"
     */
    const val STATUS_TAG = "status-controller"

    /**
     *
     * INFO_TAG = "status-controller"
     */
    const val INFO_TAG = "info-controller"

    /**
     *
     * API = "/api"
     */
    const val API = "/api"

    /**
     *
     * INFO_VERSION_PATH = "/v1"
     */
    const val INFO_VERSION_PATH = "/v1"

    /**
     *
     * INFO_BASE_INFO = "/info"
     */
    const val INFO_BASE_INFO = "/info"

    /**
     *
     * STATUS_BASE_INFO = "/status"
     */
    const val STATUS_BASE_INFO = "/status"

    /**
     *
     * STATUS_PROTECTED = "[PROTECTED]"
     */
    const val STATUS_PROTECTED: String = "[PROTECTED]"

    /**
     *
     * ROLE_PREFIX = "ROLE_"
     */
    const val ROLE_PREFIX: String = "ROLE_"


    /**
     *
     * SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST"
     */
    const val SAVED_REQUEST: String = "SPRING_SECURITY_SAVED_REQUEST"

    /**
     *
     * LAST_EXCEPTION = "BTTF_APPLICATION_LAST_EXCEPTION"
     */
    const val LAST_EXCEPTION: String = "BTTF_APPLICATION_LAST_EXCEPTION"

    /**
     *
     * APPLICATION_YEAR = "BTTF_APPLICATION_YEAR"
     */
    const val APPLICATION_YEAR: String = "BTTF_APPLICATION_YEAR"

    /**
     *
     * APPLICATION_SALT = "BTTF_APPLICATION_SALT"
     */
    const val APPLICATION_SALT: String = "BTTF_APPLICATION_SALT"

    /**
     *
     * APPLICATION_SECRET_IV = "BTTF_APPLICATION_SECRET_IV"
     */
    const val APPLICATION_SECRET_IV: String = "BTTF_APPLICATION_SECRET_IV"

    /**
     *
     * FORWARD_URI = "javax.servlet.forward.request_uri"
     */
    const val FORWARD_URI: String = "javax.servlet.forward.request_uri"

    /**
     *
     * ERROR_INVALID_USERNAME_AND_PASSWORD = "Invalid username and password!"
     */
    const val ERROR_INVALID_USERNAME_AND_PASSWORD: String = "Invalid username and password!"

    /**
     *
     * ERROR_INVALID_ROLE = "Invalid role for this page."
     */
    const val ERROR_INVALID_ROLE: String = "Invalid role for this page!"

    /**
     *
     * ERROR_USERNAME_IS_LOCKED = "Username or user roles are locked!"
     */
    const val ERROR_USERNAME_IS_LOCKED: String = "Username or user roles are locked!"

    /**
     *
     * ERROR_USERNAME_NOT_FOUND = "Username not found!"
     */
    const val ERROR_USERNAME_NOT_FOUND: String = "Username not found!"

    /**
     *
     * ERROR_AUTHENTICATION_CREDETIALS_NOT_FOUND = "Authentication credentials not Found!"
     */
    const val ERROR_AUTHENTICATION_CREDETIALS_NOT_FOUND = "Authentication credentials not Found!"

    /**
     *
     * ERROR_BAD_CREDENTIALS= "Bad credetials!"
     */
    const val ERROR_BAD_CREDENTIALS: String = "Bad credetials!"

    /**
     *
     * ERROR_ACCOUNT_EXPIRED = "Account expired!"
     */
    const val ERROR_ACCOUNT_EXPIRED: String = "Account expired!"

    /**
     *
     * ERROR_INTERNAL = "You clicked too fast or were you looking at the html source. You're brought back to the now! ;-)"
     */
    const val ERROR_INTERNAL: String =
        "You clicked too fast or were you looking at the html source."

}
