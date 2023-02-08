package local.intranet.bttf.api.model.entity

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.EntityListeners
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import org.hibernate.envers.Audited
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import local.intranet.bttf.api.domain.DefaultFieldLengths

/**
 *
 * {@link MessageEvent} is entity for CRUD with
 * {@link local.intranet.core.api.model.repository.MessageEventRepository}
 *
 * @author Radek Kádner
 *
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "bttf_message")
public data class MessageEvent(

    @Audited
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public val id: Long? = null,

    @Audited
    @NotNull
    @Column(name = "uuid", nullable = false)
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_STATUS)
    public val uuid: String,

    @NotNull
    @Column(name = "service_name", nullable = false)
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    public val serviceName: String,

    @Audited
    @Min(0)
    public var cnt: Long, // var to change

    @Audited
    @Min(0)
    public var timestmp: Long, // var to change

    @NotNull
    @Column(name = "message", nullable = false)
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_STATUS)
    public val message: String
)
