package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("UserApp")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserApp extends InstalledApp {
	private Device device;
	private User user;
	
}
