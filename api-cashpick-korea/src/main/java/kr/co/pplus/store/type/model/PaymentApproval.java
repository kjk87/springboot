package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PaymentApproval")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentApproval extends NoOnlyKey {
	private String status;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expireDate;
	private String payTransactionId;
	private String payResultCode;
	private String payResultMsg;
	private String authTransactionId;
	private String authResultCode;
	private String authResultMsg;
	private String payInfo;
	private String approvalUrl;
	private String payMethod;
	private String orderKey;
	private String type;
	private String date;
	private String storeId;
	private Long amt;
	private String userName;
	private String storeName;
	private PaymentApproval cancel;
	private Map<String, Object> cancelProperties;
	private User user;
	private Map<String, Object> properties;
	
}
