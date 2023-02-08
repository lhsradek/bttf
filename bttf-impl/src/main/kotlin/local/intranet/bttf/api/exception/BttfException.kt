package local.intranet.bttf.api.exception

import java.net.ConnectException
import org.slf4j.LoggerFactory

/**
 * It has a harmless exception that I sometimes dress up as some threatening runtime exception.
 * Which shouldn't be done ;-)
 *
 * {@link BttfException} for
 * {@link local.intranet.bttf.BttfApplication}
 *
 * @author Radek Kádner
 *
 */
public class BttfException : ConnectException {

    private val log = LoggerFactory.getLogger(javaClass)
    
    /**
     *
     * Constructor
     */
    public constructor() : super() {
        log.error("${javaClass.simpleName}")
    }

    /**
     *
     * Constructor with param
     *
     * @param msg {@link String}
     */
    public constructor(msg: String) : super(msg) {
        log.error("${msg}")
    }
    
    /**
     *
     * Constructor with param
     *
     * @param t   {@link Throwable}
     */
    public constructor(t: Throwable) : super() {
        log.error("${t::class.java.simpleName}")
    }
    
    /**
     *
     * Constructor with params
     *
     * @param msg {@link String}
     * @param t   {@link Throwable}
     */
    public constructor(msg: String, t: Throwable) : super(msg) {
        log.error("${msg} ${t::class.java.simpleName}")
    }

}
