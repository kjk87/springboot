package kr.co.pplus.store.type.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Session")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Session extends UserDevice {

	private static final long serialVersionUID = -3398387999469000634L;
	
	private String sessionKey;
	private String refreshKey;
	//extendde from User
//	private Map<String, Object> properties;
}
