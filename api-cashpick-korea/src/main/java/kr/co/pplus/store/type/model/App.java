package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("App")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class App extends AbstractModel {

	private static final long serialVersionUID = 1L;

	private String appKey;
	private String status;
	private String type;
	private String platform;
	private Map<String, Object> appProp;
	@JsonIgnore
	private Map<String, Object> serverProp;
	protected Date modDate;
	protected User modUser;
	
	
}
