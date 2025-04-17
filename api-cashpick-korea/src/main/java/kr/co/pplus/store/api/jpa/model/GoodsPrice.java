package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Data
@Entity(name="goodsPrice")
@Table(name="goods_price")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsPrice {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;

	@Column(name="goods_seq_no")
	private Long goodsSeqNo;

	@Column(name="page_seq_no")
	private Long pageSeqNo;

	@Column(name="supply_price")
	private Float supplyPrice;

	@Column(name="consumer_price")
	private Float consumerPrice;

	@Column(name="maximum_price")
	private Float maximumPrice;

	@Column(name="origin_price")
	private Float originPrice;

	@Column(name="price")
	private Float price;

	@Column(name="discount_ratio")
	Float discountRatio  = null ;

	@Convert(converter = JpaConverterDatetime.class)
	@Column(name = "reg_datetime")
	String regDatetime;

	@Convert(converter = JpaConverterBoolean.class)
	@Column(name="is_luckyball")
	Boolean isLuckyball;

	@Convert(converter = JpaConverterBoolean.class)
	@Column(name="is_wholesale")
	Boolean isWholesale;

	@Column(name="status")
	Integer status;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "goods_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
	GoodsRefDetail goods = null;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "page_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
	PageRefDetail page = null ; // '구매 페이지 정보',

	@Column(name="avg_eval")
	private Float avgEval;
}
