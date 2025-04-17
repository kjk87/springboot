package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidCardException extends ResultCodeException {
	public InvalidCardException() {
		super(Const.E_INVALID_CARD);
	}

	public InvalidCardException(Object...args) {
		super(Const.E_INVALID_CARD);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
