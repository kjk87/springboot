package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="productOption")
@Table(name="product_option")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductOption {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	@Column(name="product_seq_no")
	private Long productSeqNo;
	private String name;
	private String item;
}
