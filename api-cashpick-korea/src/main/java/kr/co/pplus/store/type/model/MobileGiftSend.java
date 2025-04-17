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
@Alias("MobileGiftSend")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileGiftSend extends NoOnlyKey {

	private static final long serialVersionUID = -5684008001068468017L;
	
	private MobileGiftPurchase purchase;
	private String mobile;
	private String name;
	private String status;
	private String confirmKey;
	private String resultMsg;
	private Map<String, Object> properties;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;	
}
