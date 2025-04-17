package kr.co.pplus.store.type.dto;

import kr.co.pplus.store.type.model.AbstractModel;
import kr.co.pplus.store.type.model.Plus;
import kr.co.pplus.store.type.model.PlusGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MovePlusDto")
public class MovePlusDto extends AbstractModel {
	private Plus plus;
	private PlusGroup src;
	private PlusGroup dest;
}
