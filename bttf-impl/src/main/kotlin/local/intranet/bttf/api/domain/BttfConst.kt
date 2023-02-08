package local.intranet.bttf.api.domain

/**
 *
 * {@link Controller} for
 * {@link local.intranet.bttf.api.controller} and thymeleaf
 *
 * @author Radek Kádner
 *
 */
public object BttfConst {

    /**
     *
     * STATUS_TAG = "status-controller"
     */
    public const val STATUS_TAG = "status-controller"

    /**
     *
     * INFO_TAG = "status-controller"
     */
    public const val INFO_TAG = "info-controller"

    /**
     *
     * API = "/api"
     */
    public const val API = "/api"

    /**
     *
     * INFO_VERSION_PATH = "/v1"
     */
    public const val INFO_VERSION_PATH = "/v1"

    /**
     *
     * INFO_BASE_INFO = "/info"
     */
    public const val INFO_BASE_INFO = "/info"

    /**
     *
     * STATUS_BASE_INFO = "/status"
     */
    public const val STATUS_BASE_INFO = "/status"

    /**
     *
     * BLANK_SPACE = " "
     */
    public const val BLANK_SPACE = " "

    /**
     *
     * STATUS_PROTECTED = "[PROTECTED]"
     */
    public const val STATUS_PROTECTED: String = "[PROTECTED]"

    /**
     *
     * ROLE_PREFIX = "ROLE_"
     */
    public const val ROLE_PREFIX: String = "ROLE_"
    
    /**
     *
     * SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST"
     */
    public const val SAVED_REQUEST: String = "SPRING_SECURITY_SAVED_REQUEST"

    /**
     *
     * LAST_EXCEPTION = "BTTF_APPLICATION_LAST_EXCEPTION"
     */
    public const val LAST_EXCEPTION: String = "BTTF_APPLICATION_LAST_EXCEPTION"

    /**
     *
     * APPLICATION_YEAR = "BTTF_APPLICATION_YEAR"
     */
    public const val APPLICATION_YEAR: String = "BTTF_APPLICATION_YEAR"

    /**
     *
     * APPLICATION_SALT = "BTTF_APPLICATION_SALT"
     */
    public const val APPLICATION_SALT: String = "BTTF_APPLICATION_SALT"

    /**
     *
     * APPLICATION_SECRET_IV = "BTTF_APPLICATION_SECRET_IV"
     */
    public const val APPLICATION_SECRET_IV: String = "BTTF_APPLICATION_SECRET_IV"

    /**
     *
     * FORWARD_URI = "javax.servlet.forward.request_uri"
     */
    public const val FORWARD_URI: String = "javax.servlet.forward.request_uri"

    /**
     *
     * ERROR_INVALID_USERNAME_AND_PASSWORD = "Invalid username and password!"
     */
    public const val ERROR_INVALID_USERNAME_AND_PASSWORD: String = "Invalid username and password!"

    /**
     *
     * ERROR_INVALID_ROLE = "Invalid role for this page."
     */
    public const val ERROR_INVALID_ROLE: String = "Invalid role for this page!"

    /**
     *
     * ERROR_USERNAME_IS_LOCKED = "Username or user roles are locked!"
     */
    public const val ERROR_USERNAME_IS_LOCKED: String = "Username or user roles are locked!"

    /**
     *
     * ERROR_USERNAME_NOT_FOUND = "Username not found!"
     */
    public const val ERROR_USERNAME_NOT_FOUND: String = "Username not found!"

    /**
     *
     * ERROR_AUTHENTICATION_CREDETIALS_NOT_FOUND = "Authentication credentials not Found!"
     */
    public const val ERROR_AUTHENTICATION_CREDETIALS_NOT_FOUND = "Authentication credentials not Found!"

    /**
     *
     * ERROR_BAD_CREDENTIALS= "Bad credetials!"
     */
    public const val ERROR_BAD_CREDENTIALS: String = "Bad credetials!"

    /**
     *
     * ERROR_ACCOUNT_EXPIRED = "Account expired!"
     */
    public const val ERROR_ACCOUNT_EXPIRED: String = "Account expired!"

    /**
     *
     * ERROR_INTERNAL = "You clicked too fast or were you looking at the html source. ;-)"
     */
    public const val ERROR_INTERNAL: String =
        "You clicked too fast or were you looking at the html source."

}
