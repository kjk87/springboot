package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="goodsImage")
@Table(name="goods_image")
public class GoodsImage {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="goods_seq_no")
	private Long goodsSeqNo;
	private String image;
	private Integer array;
	private String type;
	
}
