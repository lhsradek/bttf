package local.intranet.bttf.api.model.entity

import org.hibernate.annotations.Immutable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.Size

import local.intranet.bttf.api.domain.DefaultFieldLengths

/**
 * 
 * {@link LoggingEvent} is an &#64;Immutable entity for JPA with
 * {@link local.intranet.bttf.api.model.repository.LoggingEventRepository}
 * <p>
 * The entity without setters is only used to read data written by
 * logback-spring DbAppender
 * <p>
 * https://www.baeldung.com/hibernate-immutable <br/>
 * https://stackoverflow.com/questions/67679636/spring-data-jpa-immutable-entity
 * 
 * @author Radek Kádner
 *
 */
@Entity
@Immutable
@Table(name = "logging_event")
class LoggingEvent {

	@Id
	@Column(name = "event_id")
	val id = 0L

	@Column(name = "formatted_message")
	val formattedMessage = ""

	@Column(name = "level_string")
	@Size(max = DefaultFieldLengths.DEFAULT_STATUS)
	val levelString = ""

	@Column(name = "caller_class")
	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	val callerClass = ""

	@Column(name = "caller_method")
	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	val callerMethod = ""

	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	val arg0 = ""

	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	val arg1 = ""

	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	val arg2 = ""

	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	val arg3 = ""

	val timestmp = 0L

	/**
	 * 
	 * Returns a string representation of the object.
	 */
	override fun toString(): String {
		return "LoggingEvent [id=" + id + ", formattedMessage=" + formattedMessage + ", levelString=" + levelString +
                ", callerClass=" + callerClass + ", callerMethod=" + callerMethod + ", arg0=" + arg0 +
                ", arg1=" + arg1 + ", arg2=" + arg2 +", arg3=" + arg3 + ", timestmp=" + timestmp + "]"
	}

}
