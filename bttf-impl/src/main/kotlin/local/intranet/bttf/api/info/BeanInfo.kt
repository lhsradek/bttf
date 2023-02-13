package local.intranet.bttf.api.info

import javax.validation.constraints.Size
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import local.intranet.bttf.api.domain.DefaultFieldLengths
import local.intranet.bttf.api.service.BeanService
/**
 *
 * {@link BeanInfo}
 *
 * @author Radek Kádner
 *
 * @constructor with parameters
 *
 * @param beans {@link Map}&lt;{@link String},{@link Any}&gt;
*/
@JsonPropertyOrder("name", "beans")
public data class BeanInfo (

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public val beans: Map<String, Any>) {
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    public fun beanName(): String = BeanService::class.java.simpleName

}