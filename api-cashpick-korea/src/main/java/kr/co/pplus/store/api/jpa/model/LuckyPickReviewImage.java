package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="luckyPickReviewImage")
@Table(name="lucky_pick_review_image")
public class LuckyPickReviewImage {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="seq_no")
	Long seqNo ;
	@Column(name="lucky_pick_review_seq_no")
	private Long luckyPickReviewSeqNo;
	private String image;
	private Integer array;
	private String type;
	
}
