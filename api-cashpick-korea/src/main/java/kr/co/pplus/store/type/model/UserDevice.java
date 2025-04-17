package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("UserDevice")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDevice extends User {
	
	private static final long serialVersionUID = 3114829198623301185L;
	
	private Device device;
}
