package local.intranet.bttf.api.exception

import java.net.ConnectException
import org.slf4j.LoggerFactory

/**
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
     * Constructor with param
     *
     * @param msg {@link String}
     */
    public constructor(msg: String) : super(msg)

    /**
     *
     * Constructor with params
     *
     * @param msg {@link String}
     * @param t   {@link Throwable}
     */
    public constructor(msg: String, t: Throwable) : super(msg) {
        log.error(msg, t)
    }

}
