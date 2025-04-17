package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="goodsOption")
@Table(name="goods_option")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsOption {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	@Column(name = "goods_seq_no")
	private Long goodsSeqNo;
	private String name;
	
}
