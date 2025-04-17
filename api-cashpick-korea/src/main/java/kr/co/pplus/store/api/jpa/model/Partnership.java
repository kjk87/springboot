package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "partnership")
@Table(name = "partnership")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Partnership implements Serializable{

	private static final long serialVersionUID = 2289325460717213066L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String code;
	
	@NotEmpty(message = "협력사명을 입력하세요.")
	private String name;
	private String status; // pending:승인대기, refuse:사용불가, normal:승인
	
	@Pattern(regexp="^[a-zA-Z0-9]{6,20}$", message="문자와 숫자로 6-20글자여야 합니다.")
	@NotEmpty(message = "아이디를 입력하세요.")
	@Column(name="login_id")
	private String loginId;
	
	@Transient
	private String password;
	
	private String phone;
	private String fax;
	
	@Column(name = "zip_code")
	@NotEmpty(message = "우편번호를 입력하세요.")
	private String zipCode;
	
	@Column(name = "base_addr")
	@NotEmpty(message = "기본주소를 입력하세요.")
	private String baseAddr;
	
	@Column(name = "detail_addr")
	@NotEmpty(message = "상세주소를 입력하세요.")
	private String detailAddr;
	
	private String homepage;

	@NotEmpty(message = "담당자명을 입력하세요.")
	@Column(name = "charge_name")
	private String chargeName;
	
	@Column(name = "charge_org")
	private String chargeOrg;
	
	@NotEmpty(message = "이메일을 입력하세요.")
	@Email
	@Column(name = "charge_email")
	private String chargeEmail;
	
	@NotEmpty(message = "담당자 전화번호를 입력하세요.")
	@Column(name = "charge_phone")
	private String chargePhone;
	
	@Column(name = "reg_datetime")
	private Date regDatetime;
	
	@Column(name = "update_datetime")
	private Date updateDatetime;
	
	private String register;
	private String updater;
	
	private boolean deleted;
	
	@NotNull(message = "수수료를 입력하세요.")
	private Float commission; // 파트너사 수수료 default 3.5%
	
	@NotNull(message = "수당요율을 입력하세요.")
	private Float benefit; // 파트너사 수당 default 1.0%
	
	@Transient
	private Integer memberCount;

}
