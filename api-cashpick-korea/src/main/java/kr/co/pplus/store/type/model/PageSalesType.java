package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PageSalesType")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageSalesType extends NoOnlyKey {
	private Long seqNo;
	private Long pageSeqNo;
	private Long salesTypeSeqNo; //1:매장판매, 2:배달, 3:배송, 4:예약 5: 픽업
}
