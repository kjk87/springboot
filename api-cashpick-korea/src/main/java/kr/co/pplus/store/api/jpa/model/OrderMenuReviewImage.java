package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="orderMenuReviewImage")
@Table(name="order_menu_review_image")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderMenuReviewImage {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="order_menu_review_seq_no")
	private Long orderMenuReviewSeqNo;
	private String image;
	private Integer array;
	
}
