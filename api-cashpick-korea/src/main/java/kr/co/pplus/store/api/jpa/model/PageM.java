package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;

@Entity(name="pageM") // This tells Hibernate to make a table out of this class
@Table(name="page")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageM {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long no ; // '페이지 순번',

    @Column(name="member_seq_no")
    Long memberSeqNo  = null ; //`회원 순번',

    @JsonIgnore
    @Column(name="coop_seq_no")
    Long coopSeqNo  = null ; // '제휴사 순번',

    @JsonIgnore
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

    @Column(name="profile_seq_no")
    Long profileSeqNo  = null ; // '프로필 이미지',

    @JsonIgnore
    @Column(name="bg_seq_no")
    Long bgSeqNo  = null ; // '배경 이미지',

    @JsonIgnore
    @Column(name="valuation_count")
    Long valuationCount  = null ; //`평점 부여자 수',

    @JsonIgnore
    @Column(name="valuation_point")
    Long valuationPoint  = null ; //`총 평점',

    @JsonIgnore
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="offer_res_datetime")
    String offerLimitDate  = null ; // '의뢰 가능 일시',

    @JsonIgnore
    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="virtual_page")
    Boolean virtualPage  = false ; //`가상 여부',

    @Column(name="plus_count")
    Integer plusCount  = null ; //`플러스 고객 수',

    @JsonIgnore
    @Column(name="search_keyword")
    String searchKeyword  = null ; // '검색 키워드',

    @Column(name="code")
    String code  = null ; // '코드',

    @Column(name="introduction")
    String introduction  = null ; // '소개 글',

    @JsonIgnore
    @Column(name="coop_status")
    String coopStatus  = null ; //`가맹점 상태',

    @Column(name="main_movie_url")
    String mainMovieUrl  = null ; // '메인 소개 동영상',

    @JsonIgnore
    @Column(name="page_level")
    Integer pageLevel  = null ; //`페이지 레빌',

    @JsonIgnore
    @Column(name="auth_code")
    String authCode  = null ; // '사용 인증번호',

    @JsonIgnore
    @Column(name="incorrect_auth_code_count")
    Integer incorrectAuthCodeCount  = null ; //`인증번호 틀린 횟수',

    @JsonIgnore
    @Column(name="agent_seq_no")
    Long agentSeqNo  = null ; // '대행사 순번',

    @JsonIgnore
    @Column(name="recommendation_code")
    String recommendationCode  = null ; // '추천인 코드',

    @JsonIgnore
    @Column(name="settlement_url")
    String settlementUrl  = null ; // '정상 링크 URL',

    @Column(name="main_goods_seq_no")
    Long mainGoodsSeqNo = null ;

    @Formula("(select IFNULL(ps.is_seller, false) from page_seller ps where ps.page_seq_no = seq_no)")
    private Boolean isSeller = false;

    @Formula("(select IFNULL(ps.status, 0) from page_seller ps where ps.page_seq_no = seq_no)")
    private Integer sellerStatus = 0;

    @Column(name="is_link")
    Boolean isLink ;

    @Column(name="homepage_link")
    String homepageLink ;

    @Column(name="hashtag")
    String hashtag ;

    @Column(name="thema_seq_no")
    Long themaSeqNo ;

    @Column(name="is_holiday_closed")
    Boolean isHolidayClosed = false ;

    @Column(name="is_shop_orderable")
    Boolean isShopOrderable = true ;

    @Column(name="is_packing_orderable")
    Boolean isPackingOrderable = false ;

    @Column(name="is_delivery_orderable")
    Boolean isDeliveryOrderable = false ;

    @Column(name="delivery_radius")
    Double deliveryRadius = 5.0 ;

    @Column(name="is_parking_available")
    Boolean isParkingAvailable ;

    @Column(name="is_valet_parking_available")
    Boolean isValetParkingAvailable ;

    @Column(name="is_chain")
    Boolean isChain ;

    @Column(name="is_delivery")
    Boolean isDelivery ;

    @Column(name="parent_seq_no")
    Long parentSeqNo ;

    @Column(name="use_prnumber")
    Long usePrnumber ;


    @Column(name="distributor_agent_code")
    String distributorAgentCode ;


    @Column(name="sales_agent_code")
    String salesAgentCode ;


    @Column(name="management_agent_code")
    String managementAgentCode ;

    @JsonIgnore
    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="is_brand")
    Boolean isBrand;

}
