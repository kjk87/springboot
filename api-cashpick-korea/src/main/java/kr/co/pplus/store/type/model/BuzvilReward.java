package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("BuzvilReward")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuzvilReward extends AbstractModel {

	private String unit_id;
	private String transaction_id;
	private String user_id;
	private Long campaign_id;
	private String campaign_name;
	private String title;
	private Integer point;
	private Integer base_point;
	private Boolean is_media;
	private String revenue_type;
	private String action_type;
	private Long event_at;
	private String extra;
	private Float unit_price;
	private String custom;
	private String ifa;
	private Integer reward;
	private Boolean allow_multiple_conversions;
	private String data;

}
