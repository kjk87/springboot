package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class AlreadyLimitException extends ResultCodeException {
	public AlreadyLimitException() {
		super(Const.E_ALREADY_LIMIT);
	}
	
	public AlreadyLimitException(Object...args) {
		super(Const.E_ALREADY_LIMIT);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
