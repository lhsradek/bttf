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
 * @since 11.0.0
 */
public class BttfDbAppender : DBAppender() {

    /**
     *
     * This is where an appender accomplishes its work. Note that the argument is of
     * type Object.
     *
     * @param eventObject {@link ILoggingEvent}
     */
    override fun doAppend(eventObject: ILoggingEvent) {
        fixFormattedMessage(eventObject)
        while (eventObject.throwableProxy != null) {
            var throwableProxy = eventObject.throwableProxy
            fixMessage(throwableProxy)
            throwableProxy = throwableProxy.cause
            if (throwableProxy == null)
                break
        }
        super.doAppend(eventObject)
    }

    /**
     *
     * Fix message in DBAppender (max message length, forbidden characters in
     * Postgresql).
     *
     * @param throwableProxy {@link IThrowableProxy}
     */
    fun fixMessage(throwableProxy: IThrowableProxy) {
        try {
            val message = throwableProxy.message
            val fixedMessage = fixMessage(message)
            if (!message.equals(fixedMessage)) {
                FieldUtils.writeField(throwableProxy, "message", fixedMessage, true)
            }
        } catch (e: IllegalAccessException) {
            // System out is OK here.
            System.out.println(
                MessageFormat.format("BttfDbAppender error during fixing message: {0}", e.message)
            )
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
            val formattedMessage = eventObject.formattedMessage
            var fixedMessage = fixMessage(formattedMessage)
            if (!formattedMessage.equals(fixedMessage)) {
                FieldUtils.writeField(eventObject, "formattedMessage", fixedMessage, true)
            }
            val message = eventObject.message
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
        val maxLength = 240 // Only 200 because prefix is added lately. 240 By radek.kadner
        var fixedMessage = message
        if (message.contains("\u0000") || message.contains("\\x00") || message.length >= maxLength) {
            // Workaround -> We have replace null characters by empty space, for case when
            // exception will persisted in a Postgresql DB.
            fixedMessage = message.replace("\u0000", "").replace("\\x00", "")
            // Workaround for https://jira.qos.ch/browse/LOGBACK-493. -> DB tables has
            // limitation for max 254 characters.
            if (fixedMessage.length >= maxLength) {
                fixedMessage = fixedMessage.substring(0, maxLength - 1)
            }
        }
        return fixedMessage
    }

}
