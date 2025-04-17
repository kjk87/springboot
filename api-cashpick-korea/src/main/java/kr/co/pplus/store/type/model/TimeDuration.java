package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("TimeDuration")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeDuration extends AbstractModel {

	private static final long serialVersionUID = -3252871296772187842L;

	private String start;
	private String end;
}	
