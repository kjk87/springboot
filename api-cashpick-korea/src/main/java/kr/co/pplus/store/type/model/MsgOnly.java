package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MsgOnly")
@ToString(callSuper=true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MsgOnly extends NoOnlyKey {

	private static final long serialVersionUID = 7407700902509450552L;

	private String appType;
	private String input;
	private String type;
	private Boolean reserved;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date reserveDate;
	private String status;
	private String subject;
	private String contents;
	private Boolean includeMe;
	private String moveType1;
	private String moveType2;
	private NoOnlyKey moveTarget;
	private String moveTargetString;
	private User author;
	private Page page;
	private Integer targetCount;
	private Integer successCount;
	private Integer failCount;
	private Integer readCount;
	private String payType;
	private Long totalPrice;
	private Long refundPrice;
	private Map<String, Object> properties;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date completeDate;
	private Integer pushCase;
	private Boolean targetAll;
	

}
