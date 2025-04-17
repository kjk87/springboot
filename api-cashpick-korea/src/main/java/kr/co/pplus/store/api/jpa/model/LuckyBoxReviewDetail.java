package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name="luckyboxReviewDetail") // This tells Hibernate to make a table out of this class
@Table(name="luckybox_review")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBoxReviewDetail {

    public LuckyBoxReviewDetail(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ;

    @Column(name="member_seq_no", updatable = false)
    Long memberSeqNo = null ;

    @Column(name="luckybox_purchase_item_seq_no", updatable = false)
    Long luckyBoxPurchaseItemSeqNo = null ;

    @Column(name="review")
    String review = null ; //'구매 상품 리뷰',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; //'등록시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; //'변경시각',

    Integer status;

    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="luckybox_review_seq_no", insertable = false, updatable = false)
    @Where(clause = "type = 'thumbnail'")
    private List<LuckyBoxReviewImage> imageList = new ArrayList<>();


    @Column(name="reply_count")
    private Integer replyCount;

    private Boolean friend;

    @Key
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    Member member = null ;

    @Key
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "luckybox_purchase_item_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    LuckyBoxPurchaseItemOnly luckyBoxPurchaseItem = null ;

}
