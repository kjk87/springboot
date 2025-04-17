package kr.co.pplus.store.type.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("UserRequest")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest extends NoOnlyKey {

	private static final long serialVersionUID = 1411022644441407281L;

	private String type;
	private Map<String, Object> properties;
	private String status;
	private User user;
	
}
