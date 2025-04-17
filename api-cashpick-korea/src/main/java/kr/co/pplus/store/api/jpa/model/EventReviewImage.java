package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="eventReviewImage")
@Table(name="event_review_image")
public class EventReviewImage {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="seq_no")
	Long seqNo ;
	@Column(name="event_review_seq_no")
	private Long eventReviewSeqNo;
	private String image;
	private Integer array;
	private String type;
	
}
