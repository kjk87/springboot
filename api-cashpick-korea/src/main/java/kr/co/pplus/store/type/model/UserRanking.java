package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("UserRanking")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRanking extends User {
	private static final long serialVersionUID = -6151706236298019011L;
	private Integer ranking;
	private Integer totalRanking;
	private Integer rankingCount;
	private Integer totalRankingCount;
}
