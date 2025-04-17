package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="goodsSalesType")
@Table(name="goods_sales_type")
public class GoodsSalesType {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="seq_no")
	Long seqNo ;

	@Column(name="goods_seq_no")
	private Long goodsSeqNo;

	@Column(name="sales_type_seq_no")
	private Long salesTypeSeqNo;
	
}
