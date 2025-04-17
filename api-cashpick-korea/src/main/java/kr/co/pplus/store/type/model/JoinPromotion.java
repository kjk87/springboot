package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("JoinPromotion")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JoinPromotion extends NoOnlyKey {
	private Promotion promotion;
	private User user;
	private String joinResult;
	private String impression;
	private String status;
	private PromotionLotsConfig lots;
}
