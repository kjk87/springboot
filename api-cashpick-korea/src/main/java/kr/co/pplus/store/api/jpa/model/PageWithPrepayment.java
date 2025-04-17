package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity(name="pageWithPrepayment") // This tells Hibernate to make a table out of this class
@Table(name="page")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageWithPrepayment {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '페이지 순번',

    @Column(name="member_seq_no")
    Long memberSeqNo  = null ; //`회원 순번',

    @Column(name="coop_seq_no")
    Long coopSeqNo  = null ; // '제휴사 순번',

    String status  = null ; //`상태',

    @Column(name="page_name")
    String name  = null ; //`이름',

    @Column(name="phone_number")
    String phone  = null ; // '전화 번호',

    @Column(name="open_bounds")
    String openBound  = null ; //`공개 범위',

    @Column(name="zip_code")
    String zipCode  = null ; // '우편 번호',

    @Column(name="road_address")
    String roadAddress  = null ; // '도로명 기본 주소',

    @Column(name="road_detail_address")
    String roadDetailAddress  = null ; // '도로명 상세 주소',

    @Column(name="parcel_address")
    String parcelAddress  = null ; // '지번 기본 주소',

    @Column(name="parcel_detail_address")
    String parcelDetailAddress  = null ; // '지번 상세 주소',

    @Column(name="latitude")
    Double latitude  = null ; // '위도',

    @Column(name="longitude")
    Double longitude = null ; // '경도',

    @Column(name="catchphrase")
    String catchphrase  = null ; // '홍보 문구',

    @Column(name="category_text")
    String categoryText  = null ; // '신청 카테고리',

    @Column(name="today_view_count")
    Long todayViewCount  = null ; //`금일 조회 수',

    @Column(name="total_view_count")
    Long totalViewCount  = null ; //`전체 조회 수',

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="blind")
    Boolean blind = false ; //`블라인드 여부',

    @JsonIgnore
    @Column(name="talk_receive_bounds")
    String talkRecvBound  = null ; // '채팅 수신 범위',

    @JsonIgnore
    @Column(name="talk_deny_day")
    String talkDenyDay  = null ; // '채팅 거부 요일',

    @JsonIgnore
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="talk_deny_start_time")
    String talkDenyStartTime  = null ; // '채팅 거부 시작 시간',

    @JsonIgnore
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="talk_deny_end_time")
    String talkDenyEndTime  = null ; // '채팅 거부 종료 시간',

    @Column(name="customer_count")
    Integer customerCount  = null ; //`고객 수',

    @Column(name="page_prop", columnDefinition="TEXT")
    String pageProp = null ; //'추가 속성',

    @JsonIgnore
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; // '등록 일시',

    @JsonIgnore
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; // '수정 일시',

    @Column(name="page_type")
    String type  = null ; //`페이지 타입',

    @Column(name="modifier_seq_no")
    Long modifierSeqNo  = null ; // '수정자 순번',

    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="profile_seq_no", referencedColumnName = "seq_no")
    Attachment profileAttachment  = null ; // '프로필 이미지',

    @Column(name="bg_seq_no")
    Long bgSeqNo  = null ; // '배경 이미지',

    @Column(name="valuation_count")
    Long valuationCount  = null ; //`평점 부여자 수',

    @Column(name="valuation_point")
    Long valuationPoint  = null ; //`총 평점',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="offer_res_datetime")
    String offerLimitDate  = null ; // '의뢰 가능 일시',


    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="virtual_page")
    Boolean virtualPage  = null ; //`가상 여부',


    @Column(name="search_keyword")
    String searchKeyword  = null ; // '검색 키워드',

    @Column(name="code")
    String code  = null ; // '코드',

    @Column(name="introduction")
    String introduction  = null ; // '소개 글',

    @Column(name="coop_status")
    String coopStatus  = null ; //`가맹점 상태',

    @Column(name="main_movie_url")
    String mainMovieUrl  = null ; // '메인 소개 동영상',

    @Column(name="page_level")
    Integer pageLevel  = null ; //`페이지 레빌',

    @JsonIgnore
    @Column(name="auth_code")
    String authCode  = null ; // '사용 인증번호',

    @JsonIgnore
    @Column(name="incorrect_auth_code_count")
    Integer incorrectAuthCodeCount  = null ; //`인증번호 틀린 횟수',

    @Column(name="agent_seq_no")
    Long agentSeqNo  = null ; // '대행사 순번',

    @Column(name="recommendation_code")
    String recommendationCode  = null ; // '추천인 코드',

    @Column(name="settlement_url")
    String settlementUrl  = null ; // '정상 링크 URL',


    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "page_seq_no")
    List<PageVirtualNumber> numberList =  new ArrayList<PageVirtualNumber>() ;

    @Column(name="distance")
    Double distance = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_link")
    Boolean isLink ;

    @Column(name="homepage_link")
    String homepageLink ;

    @Column(name="hashtag")
    String hashtag ;

    @Column(name="thema_seq_no")
    Long themaSeqNo ;

    @Column(name="plus", insertable = false, updatable = false)
    Boolean plus ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_holiday_closed")
    Boolean isHolidayClosed = false ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_shop_orderable")
    Boolean isShopOrderable = true ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_packing_orderable")
    Boolean isPackingOrderable = false ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_delivery_orderable")
    Boolean isDeliveryOrderable = false ;

    @Column(name="delivery_radius")
    Double deliveryRadius = 5.0 ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_parking_available")
    Boolean isParkingAvailable ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_valet_parking_available")
    Boolean isValetParkingAvailable ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_chain")
    Boolean isChain ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_delivery")
    Boolean isDelivery ;

    @Column(name="parent_seq_no")
    Long parentSeqNo ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="use_prnumber")
    Boolean usePrnumber ;

    @Column(name="distributor_agent_code")
    String distributorAgentCode ;


    @Column(name="sales_agent_code")
    String salesAgentCode ;


    @Column(name="management_agent_code")
    String managementAgentCode ;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="is_brand")
    Boolean isBrand;

    @Column(name="bank")
    String bank;

    @Column(name="bank_account")
    String bankAccount;

    @Column(name="depositor")
    String depositor;

    @Column(name="reason")
    String reason;

    @Column(name="email")
    String email;

    @Column(name="license_image")
    String licenseImage;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="able_nfc")
    Boolean ableNfc;

    @Column(name="shop_code")
    String shopCode;

    @Column(name="open_hours")
    private String openHours;

    @Column(name="holiday")
    private String holiday;

    @Column(name="thumbnail")
    private String thumbnail;

    @Column(name="goods_noti_type")
    private String goodsNotiType;

    @Column(name="goods_notification")
    private String goodsNotification;

    @Column(name="point")
    private Float point;

    @Column(name="woodongyi")
    private Boolean woodongyi;

    @Column(name="qr_image")
    private String qrImage;

    @Column(name="store_Type")
    private String storeType;

    @Column(name="category_minor_seq_no")
    private Long categoryMinorSeqNo;

    @Column(name="category_major_seq_no")
    private Long categoryMajorSeqNo;

    @Column(name="page_operator_type")
    private String pageOperatorType; // 회사형태 corperate, indivisual, none(비사업자)

    private String area; // 활동지역

    @Column(name="background_image")
    private String backgroundImage;

    @Column(name="plus_image")
    private String plusImage;

    @Column(name="plus_info")
    private String plusInfo;

    @Column(name="shopping_mall")
    private String shoppingMall;

    @Column(name="none_status")
    private String noneStatus;

    @Column(name="none_result_status")
    private String noneResultStatus;

    @Column(name="none_reason")
    private String noneReason;

    @Column(name="sns_point")
    private String snsPoint;

    @Column(name="visit_point")
    private Integer visitPoint;

    @Column(name="visit_count")
    private Integer visitCount;

    @Column(name="use_sns")
    private Boolean useSns;

    private String benefit;

    @Column(name="pre_discount_fee")
    private Integer preDiscountFee;

    @Column(name="subscribe_fee")
    private Integer subscribeFee;

    @Column(name="visit_point_fee")
    private Integer visitPointFee;

    @Column(name="visit_min_price")
    private Integer visitMinPrice;

    @Column(name="echoss_id")
    private String echossId;

    @Column(name="rider_fee")
    private Integer riderFee;

    @Column(name="rider_min_fee")
    private Integer riderMinFee;

    @Column(name="rider_distance")
    private Float riderDistance;

    @Column(name="rider_area_type")
    private String riderAreaType;

    @Column(name="business_hours_type")//1:매일(일~토 7개) 2:평일/주말(일~토 7개) 3:요일별(일~토 7개) 4:24시간영업
    private Integer businessHoursType;


    @Column(name="order_type")
    private String orderType;

    @Column(name="min_order_price")
    private Float minOrderPrice;

    @Column(name="cooking_time")
    private Integer cookingTime;

    @Column(name="rider_free_price")
    private Float riderFreePrice;

    @Column(name="origin")
    private String origin;

    @Column(name="order_info")
    private String orderInfo;

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

    private Boolean orderable;

    @Column(name="rider_code")
    private String riderCode;

    @Column(name="rider_company")
    private String riderCompany;

    @Column(name="rider_accept_no")
    private String riderAcceptNo;


    @Column(name="rider_mapping_result")
    private String riderMappingResult;

    @Column(name="rider_type")
    private Integer riderType;


    @Column(name="wholesale_seq_no")
    private Long wholesaleSeqNo;


    @Column(name="distributor_seq_no")
    private Long distributorSeqNo;

    @Column(name="dealer_seq_no")
    private Long dealerSeqNo;


    @Column(name="add_rider_fee_distance")
    private Float addRiderFeeDistance;

    @Column(name="add_rider_fee")
    private Float addRiderFee;

    @Column(name = "business_category")
    private String businessCategory;// service, restaurant

    @Column(name = "visit_benefit_type")
    private String visitBenefitType; // discount, free, none
    @Column(name = "visit_benefit")
    private String visitBenefit;



    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="page_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    @OrderBy("day ASC")
    private Set<PageBusinessHours> pageBusinessHoursList;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="page_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    @OrderBy("week ASC")
    private Set<PageDayOff> pageDayOffList;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="page_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    @OrderBy("seq_no ASC")
    private Set<PageTimeOff> pageTimeOffList;

    @Key
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "agent_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private Agent agent;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="page_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    @Where(clause = "status = 'normal'")
    @OrderBy("seq_no ASC")
    private Set<Prepayment> prepaymentList;

}
