package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("Event")
@ToString(callSuper = true, includeFieldNames = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event extends NoOnlyKey implements Serializable {

    private static final long serialVersionUID = -1774624433212171653L;

    private String title;
    private String code;
    private String status;
    private String path;
    private String primaryType;
    private String secondaryType;
    private String winAnnounceType;
    private String winSelectType;
    private Date winAnnounceDate;
    private String winnerAlert;
    private String winnerDesc;
    private String winPushType;
    private String winPushTitle;
    private String winPushBody;
    private Attachment winImage;
    private Long winImageSeqNo;
    private Attachment winDetailImage;
    private Long winDetailImageNo;
    private String displayTimeType;
    private Duration duration;
    private Duration displayDuration;
    private Boolean aos;
    private Boolean ios;
    private Boolean cms;
    private Boolean mobileweb;
    private Boolean pcweb;
    private Boolean electron;
    private String contentsType;
    private String contents;
    private String joinType;
    private Integer joinLimitCount;
    private Integer joinTerm;
    private Float reward;
    private String rewardType;
    private Boolean gift;
    private String bizType;
    private Boolean displayUserApp;
    private Boolean displayBizApp;
    private String adType;
    private String adRegistType;
    private Integer adRegistCount;
    private Integer adPrice;
    private Attachment bizImage;
    private Long bizImageSeqNo;
    private String targetType;
    private Integer joinCount;
    private Integer minJoinCount;
    private Integer maxJoinCount;
    private Integer winnerCount;
    private Integer totalGiftCount;
    private Integer bannerPageView;
    private Integer bannerUserView;
    private String moveType;
    private String moveTargetString;
    private Long moveTargetNumber;
    private String moveMethod;
    private Attachment bannerImage;
    private Long bannerSeqNo;
    private Long detailSeqNo;
    private String virtualNumber;
    private Map<String, Object> properties;
    private String etc;
    private Integer priority;
    private Boolean man;
    private Boolean woman;
    private Boolean age10;
    private Boolean age20;
    private Boolean age30;
    private Boolean age40;
    private Boolean age50;
    private Boolean age60;
    private Boolean married;
    private Boolean notMarried;
    private Boolean hasChild;
    private Boolean noChild;
    private Boolean allAddress;
    private Date regDate;
    public String winCode;
    private Integer lottoTimes;
    private Long lottoPrice;
    private Integer lottoMatchNum;
    private EventGroup group;
    private Long groupSeqNo;

    private List<TimeDuration> displayTimeList;
    private List<EventJoin> joinList;
    private Boolean isBatch;
    private String contents2;
    private Long pageSeqNo;
    private Long clickPrice;
    private Long impPrice;
    private Boolean house;
    private Boolean isPlus;
    private Boolean allInterest;
    private String interestMajor;
    private String interestMinor;
    private String appType;
    private String buyType;
    private Integer buyLimitCount;
    private String rewardPlayType;
    private Float rewardPlay;
    private String winAnnounceRandomDatetime;
    private Float earnedPoint;
    private String earnedPointType;
    private Boolean autoRegist;
    private Date lastJoinDatetime;
    private Attachment totalGiftImage;
    private Long totalGiftImageNo;
    private String bannerImageUrl;
    private String winImageUrl;
    private String totalGiftImageUrl;
    private String eventLink;
    private Boolean agreement;
    private Integer agreement2;
    private Boolean deliveryInfo;
    private Boolean isDb;
    private Integer detailType;//1:상세없음 2:url 3:자체상세
    private String detailTitle;
    private String detailExplain;
    private String personalTitle;
    private String personalContents;
    private Long campaignSeqNo;
    private Boolean useDetailItem;
    private Boolean isLotto;
    private String mainBannerImage;
    private String eventDetailImage;
    private Boolean mainBannerDisplay;
    private Integer mainBannerArray;
    private String liveUrl;
    private String winAnnounceUrl;
    private String hint;
    private List<LottoWinNumber> lottoWinNumberList;
    private List<EventGift> eventGiftList;
}
