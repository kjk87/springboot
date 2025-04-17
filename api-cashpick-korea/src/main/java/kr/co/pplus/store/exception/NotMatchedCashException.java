package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotMatchedCashException extends ResultCodeException {
	public NotMatchedCashException() {
		super(Const.E_NOTMATCHED_CASH);
	}
	
	public NotMatchedCashException(Object...args) {
		super(Const.E_NOTMATCHED_CASH);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
