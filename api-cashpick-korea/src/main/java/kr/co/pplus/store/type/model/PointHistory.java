package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PointHistory")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PointHistory extends AbstractModel {
	private Long seqNo;
	private Long memberSeqNo;
	private Long pointBuySeqNo;
	private String type;
	private Float point;
	private String subject;
	private Map<String, Object> historyProp;
	private Date regDatetime;

	//버프 확인용 db데이터와 관련없음
	private Boolean saveBuff;
}
