package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PincruxReward")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PincruxReward extends AbstractModel {

	private Integer appkey;
	private Integer pubkey;
	private String usrkey;
	private String app_title;
	private Integer coin;
	private String transid;
	private String resign_flag;

}
