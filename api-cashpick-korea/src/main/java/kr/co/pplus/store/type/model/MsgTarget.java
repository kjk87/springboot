package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MsgTarget")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MsgTarget extends AbstractModel {

	private static final long serialVersionUID = 5409137204358009727L;

	private MsgOnly msg;
	private String msgType;
	private Map<String, Object> confirmProperties;
	private String status;
	protected String name;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date sendDate;
}
