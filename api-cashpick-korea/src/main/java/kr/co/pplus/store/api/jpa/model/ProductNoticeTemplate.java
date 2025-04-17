package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="productNoticeTemplate")
@Table(name="product_notice_template")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductNoticeTemplate {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	
	@Column(name="page_seq_no")
	private Long pageSeqNo;
	
	@Column(name="group_seq_no")
	private Long groupSeqNo;
	
	@Column(name="notice_group")
	private String noticeGroup;
	
	private String title;
	private String note;
	
}
