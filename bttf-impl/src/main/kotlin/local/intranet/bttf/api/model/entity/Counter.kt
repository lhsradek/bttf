package local.intranet.bttf.api.model.entity

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
 * {@link Counter} is entity for CRUD with
 * {@link local.intranet.core.api.model.repository.CounterRepository}
 *
 * @author Radek Kádner
 *
 */
@Entity
@Audited
@EntityListeners(AuditingEntityListener::class)
@Table(name = "bttf_counter")
data class Counter(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotNull
    @Column(name = "counter_name", nullable = false)
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    val counterName: String,

    @Min(0)
    var cnt: Long, // var to change

    @Min(0)
    var timestmp: Long, // var to change

    @NotNull
    @Column(name = "status", nullable = false)
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_STATUS)
    val status: String
)
