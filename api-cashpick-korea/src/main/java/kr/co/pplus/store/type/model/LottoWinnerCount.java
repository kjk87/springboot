package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("LottoWinnerCount")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LottoWinnerCount extends AbstractModel {


	private static final long serialVersionUID = 4459639400277795459L;
	private Integer lottoTimes ;
	private Long lottoTotalPrice ;
	private Long winnerCount ;
}
