package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="productOptionItem")
@Table(name="product_option_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductOptionItem {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	@Column(name="product_seq_no")
	private Long productSeqNo;
	@Column(name = "option_seq_no")
	private Long optionSeqNo;
	private String item;
	
}
