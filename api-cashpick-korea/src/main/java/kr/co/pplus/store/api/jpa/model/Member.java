package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

@Entity(name="member") // This tells Hibernate to make a table out of this class
@Table(name="member")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Member {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo; //'회원 순번',

    @Column(name="country_seq_no")
    Long countrySeqNo = null ; //'국가 순번',

    @Column(name="member_name")
    String memberName = null ; //'이름',

    @Column(name="account_type")
    String accountType ; //'계정 타입',

    @Column(name="login_id")
    String loginId; //'로그인 ID',

    @JsonIgnore
    @Column(name="password")
    String password ; //'로그인 암호',

    @Column(name="member_type")
    String memberType ; //'회원 타입',

    @Column(name="use_status")
    String useStatus; //'사용 상태',

    @Column(name="restriction_status")
    String restrictionStatus ; //'제제 상태',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="restriction_clear_datetime")
    String restrictionClearDatetime; //

    @Column(name="nickname")
    String nickname = null ; // COMMENT '별명',

    @Convert(converter = JpaConverterMobileNumber.class)
    @Column(name="mobile_number", insertable = false, updatable = false)
    String mobileNumber = null ; //'휴대폰 번호',

    @Column(name="email")
    String email =  null ; //'이메일',

    @Column(name="zip_code")
    String zipCode = null ; //'우편번호',

    @Column(name="base_address")
    String baseAddress = null ; //'기본 주소',

    @Column(name="join_datetime")
    Date joinDatetime ; // '가입 일시',

    @Column(name="join_platform")
    String joinPlatform ; //'가입 플랫폼',

    @Column(name="verification_media")
    String verificationMedia ; // '인증 수단',

    @Column(name="recommendation_code")
    String recommendationCode; // '추천인 코드',

    @Column(name="gender")
    String gender = null ; //  '성별',

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="married")
    Boolean married = null ; //'기혼 여부',

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "child")
    Boolean child = null ; //'자녀 있음 여부',

    @Column(name="birthday")
    String birthday = null ; //'생년월일',

    @Column(name="job")
    String job = null ; //'직업',

    @JsonIgnore
    @Column(name="talk_receive_bounds")
    String talkReceiveBounds ; // '채팅 수신 범위',

    @JsonIgnore
    @Column(name="talk_deny_day")
    String talkDenyDay = null ; //'채팅 거부 요일',

    @JsonIgnore
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="talk_deny_start_time")
    String talkDenyStartTime = null ; // '채팅 거부 시작 시간',

    @JsonIgnore
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="talk_deny_end_time")
    String talkDenyEndTime = null ; //'채팅 거부 종료 시간',

    @JsonIgnore
    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="talk_receive")
    Boolean talkReceive ; //'채팅 수신 여부',

    @JsonIgnore
    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="talk_push_receive")
    Boolean talkPushReceive ; // '채팅 푸쉬 수신 여부',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="last_login_datetime")
    String lastLoginDatetime = null ; // '최종 로그인 일시',

    @Column(name="login_fail_count")
    Integer loginFailCount = null ; //'로그인 실패 횟수',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="last_login_fail_datetime")
    String lastLoginFailDatetime = null ; //'마지막 로그인 실패 일시',

    @JsonIgnore
    @Column(name="contact_list_version")
    Long contactListVersion = null ; //'연락처 버전',

    @JsonIgnore
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="leave_request_datetime")
    String leaveRequestDatetime = null ; // '탈퇴 신청 시간',

    @JsonIgnore
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="leave_finish_datetime")
    String leaveFinishDatetime = null ; // '탈퇴 완료 시간',

    @JsonIgnore
    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="calculated")
    Boolean calculated = null ; // '정산 마감 여부',

    @JsonIgnore
    @Convert(converter = JpaConverterDate.class)
    @Column(name="calculated_month")
    String calculatedMonth = null ; //'정산 월',

    @Column(name="reg_type")
    String regType ; //'등록 타입',

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="sendbird_user")
    Boolean sendbirdUser = null ; // '샌드버드 사용자 여부',

    @Column(name="cash")
    Double cash = null ; // '보유 캐쉬',

    @Column(name="bol")
    Double bol = null ;  // '보유 BOL',

    @Column(name="point")
    Double point;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime = null ; // '수정 일시',

    @JsonIgnore
    @Column(name="modifier_seq_no")
    Long modifierSeqNo = null ; //'수정자 순번',

    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="profile_seq_no")
    Attachment profileAttachment  = null ; // '프로필 이미지',

    @JsonIgnore
    @Column(name="member_prop", columnDefinition = "TEXT")
    String memberProp = null ; //'추가 속성',

    @Column(name="recommend_unique_key")
    String recommendUniqueKey = null ; //'추천 식별 코드',

    @Column(name="certification_level")
    Short certificationLevel = null ; // '인증 레벨',

    @Column(name="lotto_ticket_count")
    Integer lottoTicketCount ;

    @Column(name="lotto_default_ticket_count")
    Integer lottoDefaultTicketCount; // 매주 참여가능한 기본 로또 응모권 사용 유무

    @Column(name="agent_code")
    String agentCode ;

    @Column(name="event_ticket_count")
    Integer eventTicketCount;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="plus_terms")
    Boolean plusTerms ; // '채팅 푸쉬 수신 여부',

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="woodongyi")
    private Boolean woodongyi;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="set_pay_password")
    private Boolean setPayPassword;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="buy_plus_terms")
    private Boolean buyPlusTerms;

    @Column(name="push_count")
    private Integer pushCount;

    @Column(name="active_area1_value")
    private String activeArea1Value;

    @Column(name="active_area1_name")
    private String activeArea1Name;

    @Column(name="active_area2_value")
    private String activeArea2Value;

    @Column(name="active_area2_name")
    private String activeArea2Name;

    private String region1;
    private String region2;
    private String region3;

    @Column(name="region_code")
    private String regionCode;

    @Column(name="app_type")
    private String appType;

    @Column(name="is_virtual")
    private Boolean isVirtual;

    @Column(name="ad_reward_count")
    private Integer adRewardCount;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="ad_reward_datetime")
    private String adRewardDatetime;

    @Column(name="plus_push")
    private Boolean plusPush;

    @Column(name="qr_image")
    private String qrImage;

    @Column(name="join_type")
    private String joinType;

    @Column(name="buff_post_public")
    private Boolean buffPostPublic;

    @Column(name="received_profile_reward")
    private Boolean receivedProfileReward;

    private Boolean supporter;

    @Column(name="recommend_count")
    private Integer recommendCount;

    @Transient
    PageDetail page;


}
