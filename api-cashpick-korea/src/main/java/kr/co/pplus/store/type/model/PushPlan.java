package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PushPlan")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushPlan extends NoOnlyKey {
	private static final long serialVersionUID = 343053036172852436L;

	private String code;
	private String status;
	private String type;
	private String title;
	private String contents;
	private Boolean aos;
	private Boolean ios;
	private String moveType1;
	private String moveType2;
	private String moveTargetString;
	private Long moveTargetNo;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date reserveDate;
	private String etc;
	private String targetType;
	private Integer priority;
	private Boolean man;
	private Boolean woman;
	private Boolean age10;
	private Boolean age20;
	private Boolean age30;
	private Boolean age40;
	private Boolean age50;
	private Boolean age60;
	private Boolean married;
	private Boolean notMarried;
	private Boolean hasChild;
	private Boolean noChild;
	private Boolean allAddress;
	private Boolean allCategory;
	private Boolean allJob;
	private Boolean store;
	private Boolean person;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	
	private Attachment image;
	private MsgOnly msg;
	private Event event;
	
}
