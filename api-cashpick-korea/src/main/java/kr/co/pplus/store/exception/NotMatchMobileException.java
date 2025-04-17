package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotMatchMobileException extends ResultCodeException {
	public NotMatchMobileException() {
		super(Const.E_NOTMATCHED_MOBILE);
	}
	
	public NotMatchMobileException(Object...args) {
		this();
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
