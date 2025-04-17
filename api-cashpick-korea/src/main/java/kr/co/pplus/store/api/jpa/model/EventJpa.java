package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;

@Entity(name = "event") // This tells Hibernate to make a table out of this class
@Table(name = "event")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventJpa implements Serializable {

    public EventJpa() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    Long seqNo;

    String title = null;
    String status = null;

    @Column(name = "primary_type")
    String primaryType = null;

    @Column(name = "secondary_type")
    String secondaryType = null;

    @Column(name = "win_announce_type")
    String winAnnounceType = null;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "start_datetime")
    String startDatetime = null;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "end_datetime")
    String endDatetime = null;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "display_start_datetime")
    String displayStartDatetime = null;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "display_end_datetime")
    String displayEndDatetime = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "android")
    Boolean android = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "ios")
    Boolean ios = null;

    @Column(name = "contents_type")
    String contentsType = null;

    @Column(name = "contents")
    String contents = null;

    @Column(name = "join_type")
    String joinType = null;

    @Column(name = "join_limit_count")
    Integer joinLimitCount = null;

    @Column(name = "reward")
    Float reward = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "gift")
    Boolean gift = null;

    @Column(name = "guess_join_count")
    Integer guessJoinCount = null;

    @Column(name = "biz_type")
    String bizType = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "display_user_app")
    Boolean displayUserApp = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "display_biz_app")
    Boolean displayBizApp = null;

    @Column(name = "advertise_type")
    String advertiseType = null;

    @Column(name = "advertise_reg_type")
    String advertiseRegType = null;

    @Column(name = "advertise_reg_count")
    Integer advertiseRegCount = null;

    @Column(name = "advertise_pay")
    Integer advertisePay = null;

    @Column(name = "biz_image_seq_no")
    Long bizImageSeqNo = null;

    @Column(name = "target_type")
    String targetType = null;

    @Column(name = "join_count")
    Integer joinCount = null;

    @Column(name = "min_join_count")
    Integer minJoinCount = null;

    @Column(name = "max_join_count")
    Integer maxJoinCount = null;

    @Column(name = "winner_count")
    Integer winnerCount = null;

    @Column(name = "total_gift_count")
    Integer totalGiftCount = null;

    @Column(name = "code")
    String code = null;

    @Column(name = "display_time_type")
    String displayTimeType = null;

    @Column(name = "path")
    String path = null;

    @Column(name = "move_type")
    String moveType = null;

    @Column(name = "move_target_string")
    String moveTargetString = null;

    @Column(name = "move_target_number")
    Long moveTargetNumber = null;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "win_announce_datetime")
    String winAnnounceDatetime = null;

    @Column(name = "winner_desc")
    String winnerDesc = null;

    @Column(name = "win_push_type")
    String winPushType = null;

    @Column(name = "win_push_title")
    String winPushTitle = null;

    @Column(name = "win_push_body")
    String winPushBody = null;

    @Column(name = "win_image_seq_no")
    Long winImageSeqNo = null;

    @Column(name = "win_detail_image_no")
    Long winDetailImageNo = null;

    @Column(name = "move_method")
    String moveMethod = null;

    @Column(name = "winner_alert")
    String winnerAlert = null;

    @Column(name = "win_select_type")
    String winSelectType = null;

    @Column(name = "win_desc_type")
    String winDescType = null;

    @Column(name = "win_desc_reg_type")
    String winDescRegType = null;

    @Column(name = "banner_seq_no")
    Long bannerSeqNo = null;

    @Column(name = "detail_seq_no")
    Long detailSeqNo = null;

    @Column(name = "virtual_number")
    String virtualNumber = null;

    @Column(name = "reward_type")
    String rewardType = null;

    @Column(name = "banner_pv")
    Integer bannerPv = null;

    @Column(name = "banner_uv")
    Integer bannerUv = null;

    @Column(name = "event_prop")
    String eventProp = null;

    @Column(name = "etc")
    String etc = null;

    @Column(name = "priority")
    Integer priority = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "man")
    Boolean man = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "woman")
    Boolean woman = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "10age")
    Boolean age10 = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "20age")
    Boolean age20 = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "30age")
    Boolean age30 = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "40age")
    Boolean age40 = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "50age")
    Boolean age50 = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "60age")
    Boolean age60 = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "married")
    Boolean married = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "not_married")
    Boolean notMarried = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "hasChild")
    Boolean hasChild = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "noChild")
    Boolean noChild = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "all_address")
    Boolean allAddress = null;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable = false)
    String regDatetime = null;

    @Column(name = "group_seq_no")
    Long groupSeqNo = null;

    @Column(name = "win_code")
    String winCode = null;

    @Column(name = "lotto_times")
    Integer lottoTimes = null;

    @Column(name = "lotto_price")
    Long lottoPrice = null;

    @Column(name = "lotto_match_num")
    Integer lottoMatchNum = null;

    @Column(name = "contents2")
    String contents2 = null;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name = "is_batch")
    Boolean isBatch = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "cms")
    Boolean cms = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "pcweb")
    Boolean pcweb = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "mobileweb")
    Boolean mobileweb = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "electron")
    Boolean electron = null;

    @Column(name = "page_seq_no")
    Long pageSeqNo = null;

    @Column(name = "click_price")
    Long clickPrice = null;

    @Column(name = "imp_price")
    Long impPrice = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "house")
    Boolean house = null;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name = "is_plus")
    Boolean isPlus = null;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name = "all_interest")
    Boolean allInterest = null;

    @Column(name = "interest_major")
    String interestMajor = null;

    @Column(name = "interest_minor")
    String interestMinor = null;

    @Column(name = "app_type")
    String appType = null;

    @Column(name = "total_gift_image_no")
    Long totalGiftImageNo = null;

    @Column(name = "buy_type")
    String buyType = null;

    @Column(name = "buy_limit_count")
    Integer buyLimitCount = null;

    @Column(name = "reward_play_type")
    String rewardPlayType = null;

    @Column(name = "reward_play")
    Float rewardPlay = null;

    @Column(name = "win_announce_random_datetime")
    String winAnnounceRandomDatetime = null;

    @Column(name = "earned_point")
    private Float earnedPoint;

    @Column(name = "earned_point_type")
    private String earnedPointType;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name = "auto_regist")
    Boolean autoRegist = null;

    @Column(name = "banner_image_url")
    private String bannerImageUrl;

    @Column(name = "win_image_url")
    private String winImageUrl;

    @Column(name = "total_gift_image_url")
    private String totalGiftImageUrl;

    @Column(name = "event_link")
    private String eventLink;

    private Integer agreement;

    @Column(name = "delivery_info")
    private Boolean deliveryInfo;

    @Column(name = "is_db")
    private Boolean isDb;

    @Column(name = "detail_type")
    private Integer detailType;//1:상세없음 2:url 3:자체상세

    @Column(name = "detail_title")
    private String detailTitle;

    @Column(name = "detail_explain")
    private String detailExplain;

    @Column(name = "personal_title")
    private String personalTitle;

    @Column(name = "personal_contents")
    private String personalContents;

    @Column(name = "campaign_seq_no")
    private Long campaignSeqNo;

    @Column(name = "is_lotto")
    private Boolean isLotto;

    @Column(name = "main_banner_image")
    private String mainBannerImage;

    @Column(name = "event_detail_image")
    private String eventDetailImage;

    @Column(name = "main_banner_display")
    private Boolean mainBannerDisplay;

    @Column(name = "main_banner_array")
    private Integer mainBannerArray;

    @Column(name = "live_url")
    private String liveUrl;

    @Column(name = "win_announce_url")
    private String winAnnounceUrl;

    private String hint;
}
