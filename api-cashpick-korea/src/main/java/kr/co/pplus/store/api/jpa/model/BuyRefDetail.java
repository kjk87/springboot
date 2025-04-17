package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import kr.co.pplus.store.api.jpa.converter.JpaConverterSeoulDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity(name="buyRefDetail") // This tells Hibernate to make a table out of this class
@Table(name="buy")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyRefDetail {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '상품 구매 순번',

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="buy_seq_no")
    private List<BuyGoods> buyGoodsList = new ArrayList<BuyGoods>();

    @Key
    @Column(name="member_seq_no")
    Long memberSeqNo = null ;  //'상품 구매 사용자 순번',

    @Key
    @Column(name="page_seq_no")
    Long pageSeqNo = null ;  //'상점 순번',

    @Key
    @Column(name="type")
    Integer type = null ;  //구매 상품 타입(0 :  메뉴 오더 상품, 1 : 일반 구매 상품)

    @Key
    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_hotdeal")
    Boolean isHotdeal ;

    @Key
    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_plus")
    Boolean isPlus ;

    @Key
    @Column(name="order_id")
    String orderId = null ;  //'결제 주문 아이디',

    @Column(name="pay_method")
    String payMethod = null ;  //'결제 유형',

    @Column(name="pg")
    String pg = null ; //'PG사 아이디',

    @Column(name="process")
    Integer process = 0; // '0: 결제요청(승인대기), 1:결제승인완료, 2:결제취소, 3:사용완료,4:사용완료 후 환불',

    @Column(name="memo")
    String memo  = null ;  //취소 메모

    @Column(name="cancel_memo")
    String cancelMemo  = null ;  //취소 메모

    @Column(name="delivery_fee")
    Integer deliveryFee = null ;

    @Key
    @Column(name="pg_tran_id")
    String pgTranId = null ; // 'PG사 결제번호 아이디',

    @Key
    @Column(name="pg_accept_id")
    String pgAcceptId = null ; // 'PG사 승인번호 아이디',

    @Convert(converter = JpaConverterJson.class)
    @Column(name="carts")
    Map<String, Object> carts  = null ; //'장바구니 구매 정보 : { "seqNoList" : [ 1,3,7] } ',


    @Column(name="price")
    Float price = null ; //'구매 상품 전체 가격',

    @Column(name="goods_price")
    Float goodsPrice = null ;

    @Column(name="option_price")
    Float optionPrice = null ;

    @Column(name="vat")
    Float vat = 0.0f ; //'구매 상품 전체 VAT',

    @Column(name="order_type")
    Integer orderType = 0 ; //0: 매장주문, 1포장주문, 2:배달주문, 3:배송(reserved)

    @Column(name="order_process")
    Integer orderProcess = 0 ; //0: 접수대기, 1:접수완료, 2:거래완료 3:배달완료, 4: 주문최소, 5:배송중(reserved), 6:배송완료(reserved)


    @Column(name="title")
    String title = null ;

    @Column(name="buyer_email")
    String buyerEmail = null ;

    @Column(name="buyer_name")
    String buyerName = null ;

    @Column(name="buyer_tel")
    String buyerTel = null ;

    @Column(name="buyer_address")
    String buyerAddress = null ;

    @Column(name="buyer_postcode")
    String buyerPostcode = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ;  //'구매 시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ;  //'변경 시각',


    @Convert(converter = JpaConverterSeoulDatetime.class)
    @Column(name="confirm_datetime")
    String confirmDatetime  = null ;

    @Convert(converter = JpaConverterSeoulDatetime.class)
    @Column(name="cancel_datetime")
    String cancelDatetime  = null ;

    @Convert(converter = JpaConverterSeoulDatetime.class)
    @Column(name="complete_datetime")
    String completeDatetime  = null ;

    @Convert(converter = JpaConverterSeoulDatetime.class)
    @Column(name="book_datetime")
    String bookDatetime  = null ;  //예약 시각

    @Convert(converter = JpaConverterSeoulDatetime.class)
    @Column(name="order_datetime")
    String orderDatetime  = null ;

    @Column(name="point_ratio")
    Float pointRatio  = null ;

    @Column(name="commission_ratio")
    Float commissionRatio  = null ;

    @Column(name="saved_point")
    Integer savedPoint  = null ;

    @Column(name="agent_seq_no")
    Long agentSeqNo  = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_payment_point")
    Boolean isPaymentPoint   = false ;

    @Column(name="member_login_id")
    String memberLoginId  = null;

    @Column(name="pay_type")
    String payType  = null;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="member_woodongyi")
    Boolean memberWoodongyi = false ;

    @Column(name="room_id")
    String roomId  = null;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="pay_datetime" ,insertable=true, updatable=true)
    String payDatetime  = null ; // '결제승인 시각',

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="non_member")
    Boolean nonMember   = false ;

    @Column(name="finteck_result")
    private Float finteckResult;//핀테크 반환금액
}
