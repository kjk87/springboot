package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidBuyException extends ResultCodeException {
	public InvalidBuyException() {
		super(Const.E_INVALID_BUY);
	}

	public InvalidBuyException(Object...args) {
		super(Const.E_INVALID_BUY);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
