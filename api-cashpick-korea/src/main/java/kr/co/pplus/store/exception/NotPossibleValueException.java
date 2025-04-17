package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotPossibleValueException extends ResultCodeException {
	public NotPossibleValueException() {
		super(Const.E_NOTPOSSIBLEVALUE);
	}
	
	public NotPossibleValueException(Map<String, Object> extra) {
		super(Const.E_NOTPOSSIBLEVALUE);
		setExtra(extra);
	}
	
	public NotPossibleValueException(Object...args) {
		super(Const.E_NOTPOSSIBLEVALUE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
