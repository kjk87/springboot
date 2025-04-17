package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("AdpopcornReward")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdpopcornReward extends AbstractModel {
	private static final long serialVersionUID = -6924796812462916663L;

	private String signed_value;
	private Long usn;
	private String reward_key;
	private Integer quantity;
	private String campaign_key;
}
