package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity(name = "todayPickWithQuestion")
@Table(name = "today_pick")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodayPickWithQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    private Boolean aos;
    private Boolean ios;
    private String title;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "open_start_date")
    private String openStartDate;//노출

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "open_end_date")
    private String openEndDate;//노출

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "event_start_date")
    private String eventStartDate;//참여

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "event_end_date")
    private String eventEndDate;//참여

    @Column(name = "gift_type")
    private String giftType; // goods, money, point, cash, bol, coin
    @Column(name = "gift_image")
    private String giftImage;
    @Column(name = "gift_title")
    private String giftTitle;
    private Integer money;
    private String status; // active, inactive, complete, expire

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable = false)
    String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime", updatable = false)
    String modDatetime;

    @Column(name = "category_image")
    private String categoryImage;
    @Column(name = "detail_image")
    private String detailImage;
    @Column(name = "banner_image")
    private String bannerImage;

    @Column(name = "move_type")
    private String moveType;//none, inner, external

    @Column(name = "inner_move_type")
    private String innerMoveType; // event, goods, shopping

    @Column(name = "move_target")
    private String moveTarget;//

    @Column(name = "review_question")
    private String reviewQuestion;

    @Column(name = "target_type")
    private String targetType;//all, target


    private Boolean man;
    private Boolean woman;
    private Boolean age10;
    private Boolean age20;
    private Boolean age30;
    private Boolean age40;
    private Boolean age50;
    private Boolean age60;

    @Column(name = "add_gift")
    private Boolean addGift;

    @Formula("(case when status = 'active' then 1 else (case when status = 'expire' then 2 else 3 end) end)")
    private Integer array;

    @Formula("(select count(1) from today_pick_join tj where tj.today_pick_seq_no = seq_no)")
    private Integer joinCount;

    @Formula("(select count(1) from today_pick_review tr where tr.today_pick_seq_no = seq_no and tr.status = 'active')")
    private Integer replyCount;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "today_pick_seq_no", referencedColumnName = "seq_no", updatable = false, insertable = false)
    @OrderBy(value = "array asc")
    private Set<TodayPickQuestion> questionList;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "today_pick_seq_no", referencedColumnName = "seq_no", updatable = false, insertable = false)
    @OrderBy(value = "array asc")
    private Set<TodayPickAddGift> addGiftList;

}
