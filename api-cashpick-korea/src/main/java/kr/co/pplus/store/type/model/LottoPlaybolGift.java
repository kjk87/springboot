package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("LottoPlaybolGift")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LottoPlaybolGift extends AbstractModel {

	private static final long serialVersionUID = -7406071437654048858L;

	private Integer lottoTimes ;
	private Long winnerCount ;
	private EventGift gift ;
	
}
