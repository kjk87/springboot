package kr.co.pplus.store.type.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MsgForPush")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MsgForPush extends MsgOnly {

	private static final long serialVersionUID = -8121369746009940069L;

	private List<PushTarget> targetList;
}
