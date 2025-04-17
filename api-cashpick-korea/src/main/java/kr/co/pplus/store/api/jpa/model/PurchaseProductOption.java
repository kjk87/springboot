package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="purchaseProductOption")
@Table(name="purchase_product_option")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseProductOption {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;
	@Column(name="purchase_seq_no")
	private Long purchaseSeqNo;
	@Column(name="purchase_product_seq_no")
	private Long purchaseProductSeqNo;
	@Column(name="product_seq_no")
	private Long productSeqNo;
	@Column(name="product_option_detail_seq_no")
	private Long productOptionDetailSeqNo;
	private Integer amount;
	private Integer price;
	private String depth1;
	private String depth2;
	
}
