package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="productReviewImage")
@Table(name="product_review_image")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductReviewImage {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="product_review_seq_no")
	private Long productReviewSeqNo;
	private String image;
	private Integer array;
	private String type;
	
}
