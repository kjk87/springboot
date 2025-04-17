package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PageOpentime")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageOpentime {


    Long seqNo ; // '페이지 순번',
    Long pageSeqNo  = null ; //페이지(상점) 순번',
    Integer type  = null ; //형식',
    String weekDay  = null ; // 'mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'
    String startTime  = null ; // 영업 시작 시각
    String endTime  = null ; // 영업 종료 시각
    Boolean nextDay = false ; // 다음날 새벽까지 영업 여부

}
