package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

import java.util.Map;

public class InvalidCashExchangeException extends ResultCodeException {
	public InvalidCashExchangeException() {
		super(Const.E_INVALID_CASH_EXCAHNGE);
	}

	public InvalidCashExchangeException(Object...args) {
		super(Const.E_INVALID_CASH_EXCAHNGE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
