package kr.co.pplus.store.type.dto;

import kr.co.pplus.store.type.model.Device;
import kr.co.pplus.store.type.model.InstalledApp;
import kr.co.pplus.store.type.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("DeviceReq")
public class DeviceReq extends User {
	private Device device;
	private InstalledApp installedApp;
}
