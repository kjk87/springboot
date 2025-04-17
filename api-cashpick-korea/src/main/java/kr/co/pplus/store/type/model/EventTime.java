package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("EventTime")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventTime extends AbstractModel {

	private static final long serialVersionUID = 8753145528592742829L;

	private Event event;
	private String start;
	private String end;
}
