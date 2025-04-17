package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="luckyboxReviewImage")
@Table(name="luckybox_review_image")
public class LuckyBoxReviewImage {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="seq_no")
	Long seqNo ;
	@Column(name="luckybox_review_seq_no")
	private Long luckyBoxReviewSeqNo;
	private String image;
	private Integer array;
	private String type;
	
}
