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
@Alias("MobileGiftSendHistory")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileGiftSendHistory extends AbstractModel {

	private static final long serialVersionUID = -5627296476737649957L;

	private MobileGiftSend send;
	private Integer no;
	private String prevStatus;
	private String procStatus;
	private String resultMsg;
	private Map<String, Object> properties;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
}
