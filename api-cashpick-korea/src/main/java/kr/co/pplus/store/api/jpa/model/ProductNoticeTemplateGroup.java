package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="productNoticeTemplateGroup")
@Table(name="product_notice_template_group")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductNoticeTemplateGroup {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	
	@Column(name="page_seq_no")
	private Long pageSeqNo;
	
	private String name;
	
}
