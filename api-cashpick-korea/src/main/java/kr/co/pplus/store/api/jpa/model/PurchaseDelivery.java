package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="purchaseDelivery")
@Table(name="purchase_delivery")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseDelivery {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;
	@Column(name="purchase_seq_no")
	private Long purchaseSeqNo;
	@Column(name="purchase_product_seq_no")
	private Long purchaseProductSeqNo;
	
	private Integer type; // 1:무료, 2:유료, 3:조건부 무료
	private Integer method; // 배송방법 ( 1:택배/우편, 2:직접전달(화물)
	@Column(name="payment_method")
	private String paymentMethod; // 배송비 결제방식 before, after
	
	@Column(name="receiver_name")
	private String receiverName;
	
	@Column(name="receiver_tel")
	private String receiverTel;
    
    @Column(name="receiver_post_code")
    private String receiverPostCode;
    
    @Column(name="receiver_address")
    private String receiverAddress;

    @Column(name="receiver_address_detail")
    private String receiverAddressDetail;
    
    @Column(name="delivery_memo")
    private String deliveryMemo;
    
    @Column(name="delivery_fee")
    private Float deliveryFee;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="delivery_start_datetime")
    private String deliveryStartDatetime ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="delivery_complete_datetime")
    private String deliveryCompleteDatetime ;
    
    @Column(name = "delivery_add_fee1")
    private Float deliveryAddFee1; // 제주 추가배송비
    
    @Column(name = "delivery_add_fee2")
    private Float deliveryAddFee2; // 도서산간 추가배송비
    
    
    @Column(name="shipping_company")
	private String shippingCompany; // 택배사
    
    @Column(name="transport_number")
    private String transportNumber; // 송장번호
    
    
    @Column(name="shipping_company_code")
    private String shippingCompanyCode;
    
    

}
