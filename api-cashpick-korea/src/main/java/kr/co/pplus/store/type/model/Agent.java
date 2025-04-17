package kr.co.pplus.store.type.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Agent")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agent extends NoOnlyKey {
	private static final long serialVersionUID = -4228537218021756031L;

	private String name;
	private String contractStart;
	private String contractEnd;
	private String status;
	private String phone;
	private String fax;
	private String zipCode;
	private String baseAddr;
	private String detailAddr;
	private String homepage;
	private String chargeName;
	private String chargeOrg;
	private String chargeId;
	private String chargePwd;
	private String chargeEmail;
	private String chargeMobile;
	private String chargePhone;
	private String code;
	private String partnershipCode;

	private Map<String, Object> properties;
}
