package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MobileGiftPurchase")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileGiftPurchase extends NoOnlyKey {

	private static final long serialVersionUID = -9171914558531387860L;

	private User user;
	private MobileGift mobileGift;
	private String status;
	private Short countPerTarget;
	private Long totalCost;
	private Long pgCost;
	private PaymentApproval approval;
	private Integer targetCount;
	private Integer successCount;
	private Integer failCount;
	private Boolean includeMe;
	private String msg;
	private String mainName;
	private String mainMobile;
	private Map<String, Object> properties;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private List<MobileGiftSend> targetList;
}
