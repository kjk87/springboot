package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("TNKReward")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TNKReward extends AbstractModel {

	private String seq_id;
	private Integer pay_pnt;
	private String md_user_nm;
	private String md_chk;
	private String app_id;
	private Long pay_dt;
	private String app_nm;
}
