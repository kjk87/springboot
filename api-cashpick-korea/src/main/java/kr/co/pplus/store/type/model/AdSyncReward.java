package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("AdSyncReward")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdSyncReward extends AbstractModel {
	private static final long serialVersionUID = -6924796812462916663L;

	private String partner;
	private String cust_id;
	private String ad_no;
	private String seq_id;
	private Integer point;
	private String ad_title;
	private String valid_key;
}
