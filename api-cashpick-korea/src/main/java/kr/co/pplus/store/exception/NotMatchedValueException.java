package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotMatchedValueException extends ResultCodeException {
	public NotMatchedValueException() {
		super(Const.E_NOTMATCHED_VALUE);
	}
	
	public NotMatchedValueException(Object...args) {
		super(Const.E_NOTMATCHED_VALUE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
