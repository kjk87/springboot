package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity(name = "orderMenuReview")
@Table(name = "order_menu_review")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderMenuReview {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
	private Long seqNo ;

    @Column(name="member_seq_no", updatable = false)
    private Long memberSeqNo = null ;

    @Column(name="page_seq_no", updatable = false)
    private Long pageSeqNo  = null ;

    @Column(name="order_purchase_seq_no")
    private Long orderPurchaseSeqNo;

    @Column(name="review")
    private String review = null ; //'구매 상품 리뷰',

    @Column(name="eval")
    private Integer eval; // '구매 상품 평가 점수:1-5',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    String modDatetime; //'변경시각',
    
    @Column(name="review_reply")
    private String reviewReply;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "review_reply_date")
    String reviewReplyDate;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable=false, updatable=false)
    Member member = null ; //'구매 사용자 순번',
    
    // image 저장 테이블 만들기
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="order_menu_review_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    @OrderBy("array ASC")
    private Set<OrderMenuReviewImage> imageList;
    
}
