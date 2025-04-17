package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Agency")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agency extends Loginable {

	private static final long serialVersionUID = -653808474759464229L;
	
	private Partner partner;
	private Country country;
	private String code;
	private Contract contract;
	private String phone;
	private String fax;
	private Address address;
	private String managerName;
	private String managerDepartment;
	private String managerPhone;
	private String managerMobile;
	private String managerEmail;
	private String homepage;
	private String note;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User regUser;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	private User modUser;
	
}
