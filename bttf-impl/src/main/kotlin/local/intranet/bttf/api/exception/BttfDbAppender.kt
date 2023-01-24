package local.intranet.bttf.api.exception

import java.text.MessageFormat

import ch.qos.logback.classic.db.DBAppender
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.IThrowableProxy
import org.apache.commons.lang3.reflect.FieldUtils

/**
 *
 * {@link BttfDbAppender}. Implementation of log DBAppender. Only purpose is
 * fix bugs in DBAppender (max message length, forbidden characters).
 *
 * @author Radek Kadner
 *
 * Inspired by Vít Švanda
 *
 * @since 11.0.0
 */
public class BttfDbAppender : DBAppender() {

    /**
     *
     * This is where an appender accomplishes its work. Note that the argument is of type Object.
     *
     * @param eventObject {@link ILoggingEvent}
     */
    override fun doAppend(eventObject: ILoggingEvent) {
        fixFormattedMessage(eventObject)
        while (eventObject.throwableProxy != null) {
            val throwableProxy: IThrowableProxy? = eventObject.throwableProxy
            throwableProxy?.let {
            	fixMessage(throwableProxy)
            	throwableProxy.cause
            }?: break
        }
        super.doAppend(eventObject)
    }

    /**
     *
     * Fix message in DBAppender (max message length, forbidden characters)
     *
     * @param throwableProxy {@link IThrowableProxy}
     */
    fun fixMessage(throwableProxy: IThrowableProxy) {
        try {
            val message: String = throwableProxy.message
            val fixedMessage: String = fixMessage(message)
            if (!message.equals(fixedMessage)) {
                FieldUtils.writeField(throwableProxy, "message", fixedMessage, true)
            }
        } catch (e: IllegalAccessException) {
            println(MessageFormat.format("BttfDbAppender error during fixing message: {0}", e.message))
        }
    }

    /**
     *
     * Fix formatted message in DBAppender (max message length, forbidden characters
     * in Postgresql).
     *
     * @param eventObject {@link ILoggingEvent}
     */
    fun fixFormattedMessage(eventObject: ILoggingEvent) {
        try {
            val formattedMessage: String = eventObject.formattedMessage
            var fixedMessage: String = fixMessage(formattedMessage)
            if (!formattedMessage.equals(fixedMessage)) {
                FieldUtils.writeField(eventObject, "formattedMessage", fixedMessage, true)
            }
            val message: String = eventObject.message
            fixedMessage = fixMessage(message)
            if (!message.equals(fixedMessage)) {
                FieldUtils.writeField(eventObject, "message", fixedMessage, true)
            }
        } catch (e: IllegalAccessException) {
            // System out is OK here.
            println(MessageFormat.format("BttfDbAppender error during fixing message: {0}", e.message))
        }
    }

    fun fixMessage(message: String): String {
        val maxLength: Int = 240 // Only 240 because prefix is added lately
        var ret: String = message
        if (message.contains("\u0000") || message.contains("\\x00") || message.length >= maxLength) {
            ret = message.replace("\u0000", "").replace("\\x00", "")
            if (ret.length >= maxLength) {
                ret = ret.substring(0, maxLength - 1)
            }
        }
        return ret
    }

}
