package kr.co.pplus.store.api.jpa.model.delivery;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterSeoulDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterSeoulDatetime2;
import kr.co.pplus.store.api.jpa.model.BuyGoodsDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name="deliveryDetail") // This tells Hibernate to make a table out of this class
@Table(name="delivery")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryDetail {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ;

    @Column(name="agent_seq_no")
    Long agentSeqNo ;

    @Column(name="company_seq_no")
    Long companySeqNo ;


    @Column(name="page_seq_no")
    Long pageSeqNo ;

    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_seq_no")
    private List<DeliveryGoods> deliveryGoodsList = new ArrayList<DeliveryGoods>();

    @Column(name="id")
    String id ; //배민의 주문번호 등 향후 타 시스템의 주문번호

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ;  //'구매 시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ;  //'변경 시각',


    @Column(name="client_address")
    String clientAddress ;

    @Column(name="client_tel")
    String clientTel ;

    @Column(name="client_memo")
    String clientMemo ;

    @Column(name="total_price")
    Float totalPrice ;

    @Column(name="payment")
    String payment ; //결제 형태 : 현금, 선결제, 카드
}
