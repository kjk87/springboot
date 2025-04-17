package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import kr.co.pplus.store.type.model.code.MemberRegType;
import kr.co.pplus.store.type.model.code.TalkRecvBound;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("User")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends Loginable {

	private static final long serialVersionUID = -8328362722168043347L;

	private Country country;
	private String accountType;
	//private String name ;
	private String memberType;
	private String useStatus;
	private String restrictionStatus;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date restrictionClsDate;
	private String nickname;
	private String mobile;
	private String email;
	private String zipCode;
	private String baseAddr;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date joinDate;
	private String platform;
	private Verification verification;
	private String recommendationCode;
	private String gender;
	private Boolean married;
	private Boolean hasChild;
	private String birthday;
	private String job;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastLoginDate;
	private Integer loginFailCount;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastLoginFailDate;
	private Long contactVersion;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date reqLeaveDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date leaveDate;
	private Boolean calculated;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date calculateMonth;
	private MemberRegType regType;
	private Boolean sendbirdUser;
	private Double cash;
	private Double totalBol;
	private Double point;
	private String recommendKey;
	private Boolean friend;
	private Boolean haveSameFriends;
	private TalkRecvBound talkRecvBound;
	private String talkDenyDay;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date talkDenyStartTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date talkDenyEndTime;
	private Attachment profileImage;
	protected Map<String, Object> properties;
	private Short certificationLevel;
	private Integer normalNumberCount;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	private User modUser;
	private Long boardSeqNo; // BulletinBoard.no == board.seq_no
	private Integer lottoTicketCount;
	private Integer lottoDefaultTicketCount; // 매주 참여가능한 기본 로또 응모권 사용 유무

	private Double latitude ;
	private Double longitude ;
	
	
	private Page page;
	private List<PageCategory> interest;

	private String agentCode ;

	private Integer eventTicketCount;

	private String verificationMedia;

	private Boolean plusTerms;

	private Boolean woodongyi;

	private Boolean setPayPassword;

	private Boolean buyPlusTerms;

	private Integer pushCount;

	private String activeArea1Value;

	private String activeArea1Name;

	private String activeArea2Value;

	private String activeArea2Name;

	private String region1;
	private String region2;
	private String region3;
	private String regionCode;

	private String appType;

	private Boolean isVirtual;

	private Integer adRewardCount;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date adRewardDatetime;

	private Boolean plusPush;

	private String recommendType;
	private String adCompany;
	private String adCode;
	private String qrImage;
	private String joinType;
	private Boolean buffPostPublic;
	private Boolean receivedProfileReward;
	private Boolean supporter;
	private Integer recommendCount;

	public User() {
		
	}
	
	public User(Long no) {
		super(no);
	}

	public void setUserNo(Long no) {
		setNo(no);
	}
	
	public String getDisplayName() {
		if (StringUtils.isEmpty(nickname))
			return getName();
		return getNickname();
	}

//	@Override
//	public String getLoginId() {
//		if(StringUtils.isEmpty(appType) || appType.equals("pplus")){
//			return super.getLoginId();
//		}else{
//			return super.getLoginId().replace(appType+"##", "");
//		}
//	}
//
//	public String getMobile() {
//		if(StringUtils.isEmpty(appType) || appType.equals("pplus")){
//			return mobile;
//		}else{
//			return mobile.replace(appType+"##", "");
//		}
//	}
}
