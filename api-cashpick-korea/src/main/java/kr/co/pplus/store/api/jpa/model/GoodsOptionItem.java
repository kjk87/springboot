package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="goodsOptionItem")
@Table(name="goods_option_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsOptionItem {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	@Column(name = "goods_seq_no")
	private Long goodsSeqNo;
	@Column(name = "option_seq_no")
	private Long optionSeqNo;
	private String item;
}
