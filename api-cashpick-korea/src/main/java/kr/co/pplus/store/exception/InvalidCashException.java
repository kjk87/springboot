package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidCashException extends ResultCodeException {
	public InvalidCashException() {
		super(Const.E_INVALID_CASH_CHARGE);
	}

	public InvalidCashException(Object...args) {
		super(Const.E_INVALID_CASH_CHARGE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
