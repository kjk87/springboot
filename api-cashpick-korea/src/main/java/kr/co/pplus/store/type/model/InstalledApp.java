package kr.co.pplus.store.type.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("InstalledApp")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstalledApp extends AppVersion {

	private static final long serialVersionUID = -4883109041004986268L;

	private Device device;
	private String pushKey;
	private Boolean pushActivate;
	private String pushMask;
	private Boolean installed;
	private Map<String, Object> installedProperties;
}
