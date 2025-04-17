package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Data
@Entity(name="productOptionDetail")
@Table(name="product_option_detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductOptionDetail {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	@Column(name="product_seq_no")
	private Long productSeqNo;
	
	@Column(name="option_seq_no")
	private Long optionSeqNo;
	
	@Column(name = "depth1_item_seq_no")
	private Long depth1ItemSeqNo;
	@Column(name = "depth2_item_seq_no")
	private Long depth2ItemSeqNo;
	
	private Integer amount;
	@Column(name="sold_count")
	private Integer soldCount;
	private Integer price;
	private Boolean flag; // 수정시 삭제할지 체크 
	
	private Integer status; // '상품상태 1:판매중, 0:판매완료 soldout, -1:판매종료(expire), -2:판매중지, -999: 삭제
	private Boolean usable; // 사용여부

	@Column(name="domeme_code")
	private String domemeCode; // 도매매 코드
	private String domaemae; // 도매매 코드

	
	@NotFound(action = NotFoundAction.IGNORE)
    @OneToOne()
    @JoinColumn(name = "depth1_item_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private ProductOptionItem item1;
	
	@NotFound(action = NotFoundAction.IGNORE)
    @OneToOne()
    @JoinColumn(name = "depth2_item_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private ProductOptionItem item2;
	
	@NotFound(action = NotFoundAction.IGNORE)
    @OneToOne()
    @JoinColumn(name = "option_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private ProductOption option;
	
}
