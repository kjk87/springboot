package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Entity(name="buffPost")
@Table(name="buff_post")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffPost {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "buff_seq_no")
    private Long buffSeqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    private String type; // normal, productBuff, lottoBuff , eventBuff, eventGift
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

    private String thumbnail;

    @Transient
    List<BuffPostImage> imageList;
}
