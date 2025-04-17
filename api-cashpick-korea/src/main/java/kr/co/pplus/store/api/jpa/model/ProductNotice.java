package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="productNotice")
@Table(name="product_notice")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductNotice {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	
	@Column(name="notice_group")
	private String noticeGroup;
	
	@Column(name="product_seq_no")
	private Long productSeqNo;
	
	private String title;
	private String note;
}
