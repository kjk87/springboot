package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidProductException extends ResultCodeException {
	public InvalidProductException() {
		super(Const.E_INVALID_PRODUCT);
	}

	public InvalidProductException(Object...args) {
		super(Const.E_INVALID_PRODUCT);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
