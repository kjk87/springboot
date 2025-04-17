package kr.co.pplus.store.type.dto;

import java.util.List;

import kr.co.pplus.store.type.model.AbstractModel;
import kr.co.pplus.store.type.model.Fan;
import kr.co.pplus.store.type.model.FanGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MoveFanListDto")
public class MoveFanListDto extends AbstractModel {
	private FanGroup src;
	private FanGroup dest;
	private FanGroup group;
	private List<Fan> fanList;
}
