package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("AdvertiseDurationStatistics")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvertiseDurationStatistics extends AbstractModel {

	private static final long serialVersionUID = -8848881544323550677L;

	private Integer totalCount;
	private Long totalCost;
	private Long totalBol;
	private Integer totalLikeCount;
}
