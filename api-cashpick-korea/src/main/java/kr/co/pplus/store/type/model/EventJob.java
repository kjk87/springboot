package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("EventJob")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventJob extends AbstractModel {

	private static final long serialVersionUID = -3253622224652566658L;

	private Event event;
	private String job;
}
