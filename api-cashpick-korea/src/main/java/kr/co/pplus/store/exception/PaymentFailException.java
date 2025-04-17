package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class PaymentFailException extends ResultCodeException {
	public PaymentFailException() {
		super(Const.E_PAYMENT_FAIL);
	}
	
	public PaymentFailException(Object...args) {
		super(Const.E_PAYMENT_FAIL);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
