package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Advertise")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Advertise extends NoOnlyKey {

	private static final long serialVersionUID = 4798791871981075044L;

	private User user;
	private String type;
	private String status;
	private Duration duration;
	private Integer totalCount;
	private Integer currentCount;
	private Long cost;
	private Long baseCost;
	private Integer serviceReward;
	private Integer reward;
	private Long refundCost;
	private Long refundReward;
	private Boolean last;
	private Boolean like;
	private Boolean free;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date likeDate;
	private Map<String, Object> properties;
	private Integer contactCount;
	private Integer likeCount;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
}
