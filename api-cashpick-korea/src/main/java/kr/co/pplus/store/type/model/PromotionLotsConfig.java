package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PromotionLotsConfig")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionLotsConfig extends NoOnlyKey {

	private static final long serialVersionUID = -159657615841441195L;
	
	private String title;
	private Short probability;
	private Integer limitCount;
	private Integer winCount;
	private Attachment lotsImage;
	
	
	
}
