package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("Page")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Page extends CouponPublisher {

    private static final long serialVersionUID = -4662176839949725323L;

    private User user;
    private Cooperation cooperation;
    private String type = "store";
    private Integer level = 1;
    private String name;
    private String code;
    private String coopStatus = "normal";
    private String status = "normal";
    private String phone;
    private Address address;
    private String openBound = "everybody";
    private Double latitude;
    private Double longitude;
    private Double distance;
    private String catchphrase;
    private String introduction;
    private PageCategory category;
    private Theme theme;
    private String categoryText;
    private Long todayViewCount = 0L;
    private Long totalViewCount = 0L;
    private Boolean blind = true;
    private String talkRecvBound;
    private String talkDenyDay;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date talkDenyStartTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date talkDenyEndTime;
    private Integer customerCount = 0;
    private Integer plusCount = 0;
    private String mainMovieUrl;
    private Long valuationCount = 0L;
    private Long valuationPoint = 0L;
    private String searchKeyword;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date offerLimitDate;
    private Boolean virtualPage = false;
    private String authCode;
    private Integer incorrectAuthCodeCount = 0;
    private String recommendationCode;
    private String settlementUrl;
    private List<PageIntroImage> introImageList;
    private List<PageIntroMovie> introMovieList;
    private Map<String, Object> properties;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date regDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modDate;
    private User modUser;
    private Boolean plus;
    private Agent agent;
    private BulletinBoard prBoard;
    private BulletinBoard reviewBoard;
    private Long mainGoodsSeqNo;
    private Long reviewCount;
    private Long goodsCount;
    private Double avgEval;
    private Boolean isSeller;
    private Integer sellerStatus;
    private Boolean isLink;
    private String homepageLink;
    private String hashtag;
    private Long themaSeqNo;
    private Boolean isHolidayClosed;
    private Boolean isShopOrderable;
    private Boolean isPackingOrderable;
    private Boolean isDeliveryOrderable;
    private Double deliveryRadius;
    private Float deliveryMinPrice;
    private Float deliveryFee;
    private Integer refundDeliveryFee;
    private Integer deliveryAddFee;
    private List<PageOpentime> opentimeList;
    private List<PageClosed> closedList;
    private Boolean isParkingAvailable;
    private Boolean isValetParkingAvailable;
    private Boolean isChain = false;
    private Boolean isDelivery = false;
    private Long parentSeqNo = null;
    private Boolean usePrnumber = false;
    private List<VirtualNumberManage> numberList;

    private String distributorAgentCode;
    private String salesAgentCode;
    private String managementAgentCode;
    private Boolean isBrand = false;
    private String bank;
    private String bankAccount;
    private String depositor;
    private String reason;
    private String email;
    private String licenseImage;
    private Boolean ableNfc;
    private String shopCode;
    private String openHours;
    private String holiday;
    private String thumbnail;
    private String goodsNotiType;
    private String goodsNotification;
    private Float point;
    private Boolean woodongyi;
    private String qrImage;
    private String storeType;
    private Long categoryMinorSeqNo;
    private Long categoryMajorSeqNo;
    private BusinessLicense businessLicense;
    private String marketType;
    private String pageOperatorType; // 회사형태 corperate, indivisual, none(비사업자)
    private String area; // 활동지역
    private String backgroundImageUrl;
    private String plusImage;
    private String plusInfo;
    private String shoppingMall;
    private Long mainProductPriceSeqNo;
    private String noneStatus;
    private String noneResultStatus;
    private String noneReason;
    private String snsPoint;
    private Integer visitPoint;
    private Integer visitCount;
    private Boolean useSns;
    private String benefit;
    private Integer preDiscountFee;
    private Integer subscribeFee;
    private Integer visitPointFee;
    private Integer visitMinPrice;
    private String echossId;

    public Page() {
        super();
    }

    public Page(Long no) {
        super("page", no);
    }

    public void setPageNo(Long no) {
        setNo(no);
    }

    public void setPageType(String type) {
        setType(type);
    }
}
