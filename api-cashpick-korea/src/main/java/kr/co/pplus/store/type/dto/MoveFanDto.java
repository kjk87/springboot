package kr.co.pplus.store.type.dto;

import kr.co.pplus.store.type.model.AbstractModel;
import kr.co.pplus.store.type.model.Fan;
import kr.co.pplus.store.type.model.FanGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MoveFanDto")
public class MoveFanDto extends AbstractModel {
	private Fan fan;
	private FanGroup src;
	private FanGroup dest;
}
