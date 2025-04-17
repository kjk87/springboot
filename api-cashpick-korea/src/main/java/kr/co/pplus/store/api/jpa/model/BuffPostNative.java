package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity(name="buffPostNative")
@Table(name="buff_post")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffPostNative {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "buff_seq_no")
    private Long buffSeqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    private String type; // normal, shoppingBuff, lottoBuff , eventBuff, eventGift
    private String title;
    private String content;

    @Column(name = "divide_amount")
    private Float divideAmount;

    @Column(name = "divide_type")
    private String divideType;//bol, point

    @Column(name = "product_price_seq_no")
    private Long productPriceSeqNo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;

    private Boolean hidden;
    private Boolean deleted;

    @Column(name = "win_price")
    private Float winPrice;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "is_friend")
    private Boolean isFriend;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "reply_count")
    private Integer replyCount;

    @Column(name = "is_like")
    private Boolean isLike;

    private String thumbnail;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "buff_post_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    @OrderBy("array ASC")
    private Set<BuffPostImage> imageList;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "member_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private Member member;
}
