package kr.co.pplus.store.type.dto;

import java.util.List;

import kr.co.pplus.store.type.model.AbstractModel;
import kr.co.pplus.store.type.model.Plus;
import kr.co.pplus.store.type.model.PlusGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MovePlusListDto")
public class MovePlusListDto extends AbstractModel {
	private PlusGroup src;
	private PlusGroup dest;
	private PlusGroup group;
	private List<Plus> plusList;
}
