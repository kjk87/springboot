package kr.co.pplus.store.type.dto;

import java.util.List;

import kr.co.pplus.store.type.model.AbstractModel;
import kr.co.pplus.store.type.model.FanGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("FanGroupListDto")
public class FanGroupListDto extends AbstractModel {
	private List<FanGroup> groupList;
}
