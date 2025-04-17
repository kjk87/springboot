package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;


@Entity(name = "agent")
@Table(name = "agent")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agent implements Serializable{
	
	private static final long serialVersionUID = 6811822014149524419L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	
	private String code;

	private String name;

	@Column(name = "contract_start")
	private String contractStart;

	@Column(name = "contract_end")
	private String contractEnd;

	private String status; // pending:승인대기, refuse:사용불가, normal:승인
	
	private String phone;

	private String fax;
	
	@Column(name = "zip_code")
	private String zipCode;
	
	@Column(name = "base_addr")
	private String baseAddr;
	
	@Column(name = "detail_addr")
	private String detailAddr;
	
	private String homepage;

	@Column(name = "charge_name")
	private String chargeName;
	
	@Column(name = "charge_org")
	private String chargeOrg;

	@Column(name = "charge_id")
	private String chargeId;

	@Column(name = "charge_pwd")
	private String chargePwd;
	
	@Column(name = "charge_email")
	private String chargeEmail;

	@Column(name = "charge_mobile")
	private String chargeMobile;

	@Column(name = "charge_phone")
	private String chargePhone;

	@Column(name = "charge_prop")
	private String chargeProp;

	@Column(name = "reg_datetime")
	private Date regDatetime;

	private Integer depth;

	@Column(name = "parent_seq_no")
	private Long parentSeqNo;

	@Column(name = "is_chain")
	private Boolean isChain;

	@Column(name = "partnership_code")
	private String partnershipCode;

	@Column(name = "update_datetime")
	private Date updateDatetime;

	private String register;

	private String updater;
	
	private Boolean deleted;

	@Column(name = "sole_distributor")
	private Boolean soleDistributor;
	
    private Boolean woodongyi; // 포인트적립 강제 적용(우동이 같은 경우 가맹점 정책 강제적용)

	private Boolean basic;

    private String parents; // 계층구조의 업라인

	private Integer type;

	@Column(name = "page_commission")
	private Float pageCommission;

	@Column(name = "user_commission")
	private Float userCommission;


	@Column(name = "advertise_fee")
	private Float advertiseFee;


	@Column(name = "target_profits")
	private Float targetProfits;


	@Column(name = "expiration_date")
	private Date expirationDate;

	private String bank;

	@Column(name = "bank_account")
	private String bankAccount;

	private String depositor;

	@Column(name = "rider_commission")
	private Float riderCommission; // 배달주문 수수료

	@Column(name = "pack_commission")
	private Float packCommission; // 포장주문 수수료

	@Column(name = "shop_commission")
	private Float shopCommission; // 매장주문 수수료

	@Column(name = "call_commission")
	private Float callCommission; // 전화주문 수수료

	@Column(name = "ticket_commission")
	private Float ticketCommission; // 티켓 수수료

	@Column(name = "prepayment_commission")
	private Float prepaymentCommission; // 전화주문 수수료


	@Key
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "partnership_code", referencedColumnName = "code", insertable = false, updatable = false)
    private Partnership partner;
	
}
