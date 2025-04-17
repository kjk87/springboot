package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("FlexReward")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlexReward extends AbstractModel {

	private String userkey;
	private String flexcode;
	private Integer publisher_price;
	private Integer user_price;
	private String ad_title;
	private String ad_division;
}
