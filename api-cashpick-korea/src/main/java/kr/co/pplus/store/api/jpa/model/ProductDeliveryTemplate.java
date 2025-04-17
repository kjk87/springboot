package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="productDeliveryTemplate")
@Table(name="product_delivery_template")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDeliveryTemplate {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;
	
	private String name;
	
	@Column(name="page_seq_no")
	private Long pageSeqNo;
	
	private Integer method; // 배송방법 ( 1:택배/우편, 2:직접전달(화물)
	
	private Integer type; // 1:무료, 2:유료, 3:조건부 무료
	
	@Column(name="shipping_company")
	private String shippingCompany; // 택배사
	@Column(name="forwarding_addr")
	private Long forwardingAddr; // 출고지 (pageAddress seqNo)
	@Column(name="return_addr")
	private Long returnAddr; // 반품/교환 주소 (pageAddress seqNo)
	
	@Column(name="payment_method")
	private String paymentMethod; // 배송비 결제방식 before, after
	@Column(name="delivery_fee")
	private Float deliveryFee; // 배송비 
	
	@Column(name="is_add_fee")
	private Boolean isAddFee; // 도서산간 추가배송비 설정
	
	@Column(name="delivery_add_fee1")
	private Float deliveryAddFee1; // 제주지역 추가배송비
	@Column(name="delivery_add_fee2")
	private Float deliveryAddFee2; // 도서산간 추가배송비
	@Column(name="delivery_min_price")
	private Float deliveryMinPrice; // 무료배송비 
	
	@Column(name="delivery_return_fee")
	private Float deliveryReturnFee; // 반품 배송비
	@Column(name="delivery_exchange_fee")
	private Float deliveryExchangeFee; // 교환배송비 (왕복)
	
	@Column(name="as_tel")
	private String asTel; // A/S 전화번호
	@Column(name="as_ment")
	private String asMent; // A/S 안내
	@Column(name="special_note")
	private String specialNote; // 반품/교환 안내(판매자 특이사항)

	@Convert(converter = JpaConverterDatetime.class)
	@Column(name = "reg_datetime")
	String regDatetime;
	
	
	@NotFound(action = NotFoundAction.IGNORE)
    @OneToOne()
    @JoinColumn(name = "forwarding_addr", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private PageAddress forwardAddress;   
	
	@NotFound(action = NotFoundAction.IGNORE)
    @OneToOne()
    @JoinColumn(name = "return_addr", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private PageAddress returnAddress;   
	
	
}
