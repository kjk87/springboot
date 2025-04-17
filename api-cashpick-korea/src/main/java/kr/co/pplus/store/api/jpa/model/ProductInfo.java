package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="productInfo")
@Table(name="product_info")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductInfo {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	
	@Column(name="product_seq_no")
	private Long productSeqNo;
	
	private String model;
	@Column(name = "model_code")
	private String modelCode;
	private String brand;
	private String menufacturer; // 제조사
	@Column(name = "origin_type")
	private String originType; // 원산지타입 (domestic, imported, etc)
	private String origin; // 원산지
	@Column(name = "under_age")
	private Boolean underAge; // 미성년자구매
	@Column(name = "menufactured_date")
	private String menufacturedDate; // 제조일 
	@Column(name = "effective_date")
	private String effectiveDate; // 유효일자 
	
}
