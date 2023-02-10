package local.intranet.bttf.api.domain.type

/**
 *
 * {@link ResponseCodeType} 
 * <p>
 * OK, ERROR, REFUSED
 *
 * @author Radek Kádner
 */
public enum class ResponseCodeType(val responseCode: String) {

    /**
     *
     * OK = "OK"
     */
    OK("OK"),

    /**
     *
     * ERROR = "ERROR"
     */
    ERROR("ERROR"),

    /**
     *
     * REFUSED = "REFUSED"
     */
    REFUSED("REFUSED"),

}
