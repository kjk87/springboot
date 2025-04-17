package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class PaymentValidationFailException extends ResultCodeException {
	public PaymentValidationFailException() {
		super(Const.E_PAYMENT_VALIDATION_FAIL);
	}
	
	public PaymentValidationFailException(Object...args) {
		super(Const.E_PAYMENT_VALIDATION_FAIL);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
