package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidCartException extends ResultCodeException {
	public InvalidCartException() {
		super(Const.E_INVALID_CART);
	}

	public InvalidCartException(Object...args) {
		super(Const.E_INVALID_CART);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
