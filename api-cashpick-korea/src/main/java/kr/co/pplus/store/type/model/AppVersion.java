package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("AppVersion")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppVersion extends App {
	private static final long serialVersionUID = 3966164736709074305L;

	private String version;
	private Map<String, Object> versionProp;
//  extended field
//	private Date modDate;
//	private User modUser;
}
