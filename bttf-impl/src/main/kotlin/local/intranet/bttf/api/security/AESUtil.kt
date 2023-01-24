package local.intranet.bttf.api.security

import java.nio.charset.Charset
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.Base64

import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

import org.springframework.security.crypto.codec.Hex

/**
 *
 * {@link AESUtil} for
 * {@link local.intranet.bttf.api.controller.IndexController}
 * <p>
 * https://www.baeldung.com/java-aes-encryption-decryption <br>
 * https://github.com/eugenp/tutorials/blob/master/core-java-modules/core-java-security-algorithms/src/main/java/com/baeldung/aes/AESUtil.java
 * <p>
 * static fun:
 * <p>
 * https://www.baeldung.com/kotlin/static-methods
 * base64:
 * <p>
 * https://www.baeldung.com/kotlin/strings-base64-encode-decode-guide
 * hex:
 * <p>
 * https://www.baeldung.com/kotlin/int-to-hex-string
 * <p>
 *
 */
class AESUtil {

    companion object {

        private val AESUTIL_AES: String = "AES"
        private val AESUTIL_AES_PADDING: String = "AES/CBC/PKCS5Padding"
        private val AESUTIL_PBKDF2_WITH_HMAC_SHA256: String = "PBKDF2WithHmacSHA256"

        /**
         *
         * Encrypt
         *
         * <p>
         * byte[]
         * <p>
         * https://www.baeldung.com/kotlin/byte-array
         *
         * @param input {@link String}
         * @param key   {@link SecretKey}
         * @param iv    {@link IvParameterSpec}
         * @return {@link String}
         * @throws NoSuchPaddingException
         * @throws NoSuchAlgorithmException
         * @throws InvalidAlgorithmParameterException
         * @throws InvalidKeyException
         * @throws BadPaddingException
         * @throws IllegalBlockSizeException
         */
        @JvmStatic
        @Throws(
            NoSuchPaddingException::class, NoSuchAlgorithmException::class,
            InvalidAlgorithmParameterException::class, InvalidAlgorithmParameterException::class,
            InvalidKeyException::class, BadPaddingException::class, IllegalBlockSizeException::class
        )
        fun encrypt(input: String, key: SecretKey, iv: IvParameterSpec): String {
            val cipher: Cipher = Cipher.getInstance(AESUTIL_AES::class.simpleName)
            cipher.init(Cipher.ENCRYPT_MODE, key, iv)
            val cipherText: ByteArray = cipher.doFinal(input.toByteArray())
            val ret: String = Base64.getEncoder().encodeToString(cipherText)
            return ret;
        }

        /**
         *
         * Decrypt
         *
         * @param cipherText {@link String}
         * @param key        {@link SecretKey}
         * @param iv         {@link IvParameterSpec}
         * @return {@link String}
         * @throws NoSuchPaddingException
         * @throws NoSuchAlgorithmException
         * @throws InvalidAlgorithmParameterException
         * @throws InvalidKeyException
         * @throws BadPaddingException
         * @throws IllegalBlockSizeException
         */
        @JvmStatic
        @Throws(
            NoSuchPaddingException::class, NoSuchAlgorithmException::class,
            InvalidAlgorithmParameterException::class, InvalidAlgorithmParameterException::class,
            InvalidKeyException::class, BadPaddingException::class, IllegalBlockSizeException::class
        )
        fun decrypt(cipherText: String, key: SecretKey, iv: IvParameterSpec): String {
            val cipher: Cipher = Cipher.getInstance(AESUTIL_AES.toUpperCase()::class.simpleName)
            cipher.init(Cipher.DECRYPT_MODE, key, iv)
            val ret: ByteArray = cipher.doFinal(Base64.getDecoder().decode(cipherText))
            return String(ret)
        }

        /**
         *
         * Generate key
         *
         * @param n (128, 192 a 256)
         * @return {@link SecretKey}
         * @throws NoSuchAlgorithmException
         */
        @JvmStatic
        @Throws(NoSuchPaddingException::class)
        fun generateKey(n: Int): SecretKey {
            try {
                val keyGenerator: KeyGenerator = KeyGenerator.getInstance(AESUTIL_AES)
                keyGenerator.init(n)
                val ret: SecretKey = keyGenerator.generateKey()
                return ret
            } catch (e: NoSuchAlgorithmException) {
                throw e
            }
        }

        /**
         *
         * Generate salt
         *
         * @return {@link String}
         */
        @JvmStatic
        fun generateSalt(): String {
            val random: SecureRandom = SecureRandom()
            val ret: ByteArray = ByteArray(20)
            random.nextBytes(ret)
            return String(ret)
        }

        /**
         *
         * Get key from password
         *
         * @param password {@link String}
         * @param salt     {@link String}
         * @return {@link SecretKey}
         * @throws NoSuchAlgorithmException
         * @throws InvalidKeySpecException
         */
        @JvmStatic
        @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
        fun getKeyFromPassword(password: String, salt: String): SecretKey {
            val factory: SecretKeyFactory = SecretKeyFactory
                .getInstance(AESUTIL_PBKDF2_WITH_HMAC_SHA256)
            val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 65536, 256)
            val ret: SecretKey = SecretKeySpec(factory.generateSecret(spec).getEncoded(), AESUTIL_AES)
            return ret;
        }

        /**
         *
         * Generate Iv
         *
         * @return {@link IvParameterSpec}
         */
        @JvmStatic
        fun generateIv(): IvParameterSpec {
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            val ret: IvParameterSpec = IvParameterSpec(iv)
            return ret
        }

        /**
         *
         * Get plain text from Base64
         *
         * @param data base64
         * @return plain text
         */
        @JvmStatic
        fun getBase64(data: String): String {
            val ret: String = String(Base64.getDecoder().decode(data))
            return ret
        }

        /**
         *
         * Set to Base64
         *
         * @param data plain text
         * @return base64
         */
        @JvmStatic
        fun setBase64(data: String): String {
            val ret: String = String(Base64.getEncoder().encode(data.toByteArray(Charset.forName("UTF-8"))))
            return ret
        }

        /**
         *
         * Get plain text from hex
         *
         * @param data as hex
         * @return plain text
         */
        @JvmStatic
        fun getHex(data: String): String {
            val ret: String = String(Hex.decode(data))
            return ret
        }

        /**
         *
         * Set to Hex
         *
         * @param data plain text
         * @return hex
         */
        @JvmStatic
        fun setHex(data: String): String {
            val ret: String = String(Hex.encode(data.toByteArray(Charset.forName("UTF-8"))))
            return ret
        }

    }
}
