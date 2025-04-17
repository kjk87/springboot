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
@Alias("Approval")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Approval extends NoOnlyKey {
	private static final long serialVersionUID = -420662354924867952L;

	private String type;
	private String payTrId;
	private String payResultCode;
	private String payResultMsg;
	private String authTrId;
	private String authResultCode;
	private String authResultMsg;
	private String payInfo;
	private Map<String, Object> origianlResult;
	private Approval cancelApproval;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
}
