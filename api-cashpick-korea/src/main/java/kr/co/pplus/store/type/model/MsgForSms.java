package kr.co.pplus.store.type.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MsgForSms")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MsgForSms extends MsgOnly {
	private static final long serialVersionUID = -890611802569150689L;
	private List<SmsTarget> targetList;
}
