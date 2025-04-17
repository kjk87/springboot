package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("EventGroup")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventGroup extends NoOnlyKey {
	private static final long serialVersionUID = -3968497499828183241L;

	private String title;
}
