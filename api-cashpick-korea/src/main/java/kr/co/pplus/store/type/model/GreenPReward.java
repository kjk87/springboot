package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("GreenPReward")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GreenPReward extends AbstractModel {
	private static final long serialVersionUID = -6924796812462916663L;

	private String ads_idx;
	private String etc;
	private String ads_name;
	private Integer rwd_cost;
}
