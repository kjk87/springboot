package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="productAuth")
@Table(name="product_auth")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductAuth {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	
	@Column(name="product_seq_no")
	private Long productSeqNo;
	
	private String type; // 인증유형
	private String agency; // 인증기관
	@Column(name = "auth_no")
	private String authNo; // 인증번호 
	
	
}
