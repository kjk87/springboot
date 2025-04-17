package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Device")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device extends NoOnlyKey {
	
	private static final long serialVersionUID = 4357098649071892749L;
	
	private String deviceId;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastAccessDate;
	private String platform;
	private User owner;
	private InstalledApp installedApp;
	private Map<String, Object> properties;
	
	public void setDeviceNo(Long no) {
		super.setNo(no);
	}
}
