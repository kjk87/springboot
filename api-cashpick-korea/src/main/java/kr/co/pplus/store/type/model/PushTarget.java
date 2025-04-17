package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PushTarget")
@ToString(callSuper=true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushTarget extends MsgTarget {

	private static final long serialVersionUID = 8661888477539634681L;

	private Boolean readed;
	private User user;
	private Integer pushPrice;
}
