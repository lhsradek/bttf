package local.intranet.bttf.api.info.content

import local.intranet.bttf.api.domain.DefaultFieldLengths
import local.intranet.bttf.api.info.AttemptInfo
import local.intranet.bttf.api.info.TimedEntry
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.ZonedDateTime
import javax.validation.constraints.Size
import org.jetbrains.annotations.NotNull

/**
 *
 * https://hackmd.io/@pierodibello/ByEVNHg-v
 */ 
public interface LoginCache {

    /**
     * It doesn't have to exist, if exist return number of attempts
     * 
     * @param key {@link String}
     *
     * @return {@link Int?}
     */
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public fun getById(@NotNull key: String): Int?
    
    /**
     * Put new key or increment number of attempts
     *
     * @param key {@link String}
     */ 
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public fun keyToCache(@NotNull key: String)
    
    /**
     *
     * login succeeded, remove key from cache
     *
     * @param key {@link String}
     */           
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public fun invalidateKey(@NotNull key: String)
    
    /**
     *
     * Is blocked? Key doesn't have to exist
     *
     * @param key {@link String}
     *
     * @return {@link Boolean?}
     */ 
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public fun isBlocked(@NotNull key: String): Boolean
    
    /** 
     * key, number of attempts, creation timestamp of all
     * 
     * @param printBlocked {@link Boolean?} as filter if not null
     *
     * @return {@link List}&lt;{@link AttemptInfo}&gt;
     */
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    public fun getCache(printBlocked: Boolean?): List<AttemptInfo>
    
}
