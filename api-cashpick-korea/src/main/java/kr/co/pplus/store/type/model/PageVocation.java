package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PageVocation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageVocation {


    Long seqNo ; // '페이지 순번',
    Long pageSeqNo  = null ; //페이지(상점) 순번',
    String startDate  = null ; // 영업 시작 시각
    String endDate  = null ; // 영업 종료 시각

}
