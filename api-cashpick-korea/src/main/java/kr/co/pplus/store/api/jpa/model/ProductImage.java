package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="productImage")
@Table(name="product_image")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductImage {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="product_seq_no")
	private Long productSeqNo;
	private String image;
	private Integer array;
	private Boolean deligate; // 대표 이미지 
	
}
