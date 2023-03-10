package local.intranet.bttf.api.service

import local.intranet.bttf.api.controller.StatusController
import local.intranet.bttf.api.exception.BttfException
import local.intranet.bttf.api.security.AESUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 *
 * {@link BttfService} for {@link local.intranet.bttf.BttfApplication}
 *
 * @author Radek Kádner
 *
 */
@Service
public class BttfService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${bttf.sec.key}")
    private lateinit var key: String

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var statusController: StatusController

    public companion object {

        /**
         *
         * SecForPlayer for thymeleaf play.html
         * <p>
         * used {@link local.intranet.bttf.api.controller.IndexController}
         *
         * @param id        {@link Long}
         * @param secretKey {@link SecretKey}
         * @param iv        {@link IvParameterSpec}
         * @return {@link String}
         * @throws BttfException
         */
        @JvmStatic
        @Synchronized
        @Throws(BttfException::class)
        public fun secForPlayer(id: Long, secretKey: SecretKey, iv: IvParameterSpec): String {
            try {
                return AESUtil.setHex(AESUtil.encrypt("${id}", secretKey, iv))
            } catch (e: Exception) {
                when (e) {
                    is InvalidKeyException,
                    is NoSuchPaddingException,
                    is NoSuchAlgorithmException,
                    is InvalidAlgorithmParameterException,
                    is BadPaddingException,
                    is IllegalBlockSizeException -> {
                        // A disguised exception
                        e.message?.let {
                            throw BttfException(e.message!!, e)
                        } ?: throw BttfException(e::class.java.simpleName, e)
                    }
                    else -> throw e
                }
            }
        }
    }

}
