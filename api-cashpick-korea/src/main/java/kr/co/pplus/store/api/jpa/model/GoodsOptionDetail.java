package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Data
@Entity(name="goodsOptionDetail")
@Table(name="goods_option_detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsOptionDetail {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;
	@Column(name = "goods_seq_no")
	private Long goodsSeqNo;
	
	@Column(name = "depth1_item_seq_no")
	private Long depth1ItemSeqNo;
	@Column(name = "depth2_item_seq_no")
	private Long depth2ItemSeqNo;
	@Column(name = "depth3_item_seq_no")
	private Long depth3ItemSeqNo;
	
	private Integer amount;
	@Column(name="sold_count")
	private Integer soldCount;
	private Integer price;
	private Boolean flag; // 수정시 삭제할지 체크 
	
	@NotFound(action = NotFoundAction.IGNORE)
    @OneToOne()
    @JoinColumn(name = "depth1_item_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private GoodsOptionItem item1;
	
	@NotFound(action = NotFoundAction.IGNORE)
    @OneToOne()
    @JoinColumn(name = "depth2_item_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private GoodsOptionItem item2;
	
	@NotFound(action = NotFoundAction.IGNORE)
    @OneToOne()
    @JoinColumn(name = "depth3_item_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private GoodsOptionItem item3;
}
