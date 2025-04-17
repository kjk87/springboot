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
@Alias("Partner")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Partner extends Loginable {

	private static final long serialVersionUID = -578309018605555532L;
	
	private Country country;
	private String code;
	private Boolean autoApproval;
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
