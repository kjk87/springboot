package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="buyGoodsOption")
@Table(name="buy_goods_option")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyGoodsOption {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;
	@Column(name="buy_seq_no")
	private Long buySeqNo;
	@Column(name="buy_goods_seq_no")
	private Long buyGoodsSeqNo;
	@Column(name="goods_seq_no")
	private Long goodsSeqNo;
	@Column(name="goods_option_detail_seq_no")
	private Long goodsOptionDetailSeqNo;
	private Integer amount;
	private Integer price;
	private String depth1;
	private String depth2;
	private String depth3;
}
