package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name="luckyboxReview") // This tells Hibernate to make a table out of this class
@Table(name="luckybox_review")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBoxReview {

    public LuckyBoxReview(){

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

    @Transient
    private List<LuckyBoxReviewImage> imageList = new ArrayList<>();

}
