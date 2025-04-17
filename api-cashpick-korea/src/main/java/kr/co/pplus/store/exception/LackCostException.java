package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LackCostException extends ResultCodeException {
	private static final long serialVersionUID = 7670379816814280211L;

	public LackCostException() {
		super(Const.E_LACK_COST);
	}

	public LackCostException(Object...args) {
		super(Const.E_LACK_COST);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
