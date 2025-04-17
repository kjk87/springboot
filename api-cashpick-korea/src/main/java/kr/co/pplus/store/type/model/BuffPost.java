package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("BuffPost")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffPost extends AbstractModel {


	private Long seqNo;
	private Long buffSeqNo;
	private Long memberSeqNo;

	private String type; // normal, productBuff, lottoBuff , eventBuff, eventGift
	private String title;
	private String content;

	private Float divideAmount;

	private String divideType;//bol, point

	private Long productPriceSeqNo;

	private String regDatetime;

	private Date modDatetime;

	private Boolean hidden;
	private Boolean deleted;

	private Float winPrice;
	
	private String thumbnail;
	
}
