package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Data
@Entity(name="lotto") // This tells Hibernate to make a table out of this class
@Table(name="lotto")
@Alias("Lotto")
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lotto {


    public Lotto(){

    }

    @Id
    @Column(name="seq_no")
    Long seqNo ; // '장바구니 상품 순번',

    @Column(name="lotto_times")
    Integer lottoTimes  = null ; // '사용자 순번',

    @Column(name="lotto_prev_times")
    Integer lottoPrevTimes  = null ; // '상점페이지 순번',

    @Column(name="win_code")
    String winCode = null ;

    @Column(name="join_luckybol")
    Long joinLuckybol ;

    @Column(name="lotto_luckybol")
    Long lottoLuckybol ;

    @Column(name="lotto_5_luckybol")
    Long lotto5Luckybol ;

    @Column(name="lotto_4_luckybol")
    Long lotto4Luckybol ;

    @Column(name="lotto_3_luckybol")
    Long lotto3Luckybol ;

    @Column(name="lotto_gift_type")
    String lottoGiftType ;

    @Column(name="lotto_5_gift_type")
    String lotto5GiftType ;

    @Column(name="lotto_4_gift_type")
    String lotto4GiftType ;

    @Column(name="lotto_3_gift_type")
    String lotto3GiftType ;

    @Column(name="url1")
    String url1  = null ;

    @Column(name="selector1")
    String selector1  = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="success1")
    Boolean success1  = true ;

    @Column(name="url2")
    String url2  = null ;

    @Column(name="selector2")
    String selector2  = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="success2")
    Boolean success2  = true ;

    @Column(name="url3")
    String url3  = null ;

    @Column(name="selector3")
    String selector3  = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="success3")
    Boolean success3  = true ;

    @Column(name="banner_seq_no")
    Long bannerSeqNo  ;

    @Column(name="banner_id")
    String bannerId  ;

    @Column(name="join_ticket_num")
    Long joinTicketNum ;

    @Column(name="recommend_ticket_num")
    Long recommendTicketNum ;

    @Column(name="recommendee_ticket_num")
    Long recommendeeTicketNum ;

    @Column(name="activate_recommend_ticket_num")
    Long activateRecommendTicketNum ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; // '장바구니 상품 변경 시각',
}
