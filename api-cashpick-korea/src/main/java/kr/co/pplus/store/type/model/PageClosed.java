package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PageClosed")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageClosed {

    Long seqNo ; // '페이지 순번',
    Long pageSeqNo  = null ; //페이지(상점) 순번',
    Integer everyWeek  = 0 ; // 0: 매주, 1: 첫번째 주, 2: 두번째 주, 3: 세번째 주, 4: 네번째 주, 5: 다섯번째 주
    String weekDay  = null ; // 'mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'


}
