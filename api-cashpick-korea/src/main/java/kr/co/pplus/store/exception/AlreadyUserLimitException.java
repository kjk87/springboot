package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class AlreadyUserLimitException extends ResultCodeException {
	public AlreadyUserLimitException() {
		super(Const.E_ALREADY_USER_LIMIT);
	}

	public AlreadyUserLimitException(Object...args) {
		super(Const.E_ALREADY_USER_LIMIT);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
