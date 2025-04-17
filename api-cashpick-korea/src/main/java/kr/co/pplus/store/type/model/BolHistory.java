package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("BolHistory")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BolHistory extends NoOnlyKey {
	private User user;
	private Page page;
	private String primaryType;
	private String secondaryType;
	private String refundStatus;
	private Float amount;
	private String subject;
	private Map<String, Object> properties;
	private Approval approval;
	private NoOnlyKey target;
	private String targetType;
	private Date regDate;
	private Boolean isLottoTicket = false ;
	private Boolean isEventTicket = false ;
	private List<BolHistoryTarget> targetList;

	//버프 확인용 db데이터와 관련없음
	private Boolean saveBuff;

	public void setHistoryNo(Long no) {
		setNo(no);
	}

}
