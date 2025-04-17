package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="pageImage")
@Table(name="page_image")
public class PageImage {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="page_seq_no")
	private Long pageSeqNo;
	private String image;
	private Integer array;
	
}
