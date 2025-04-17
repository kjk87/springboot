package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidDeliveryException extends ResultCodeException {
	public InvalidDeliveryException() {
		super(Const.E_INVALID_DELIVERY);
	}

	public InvalidDeliveryException(Object...args) {
		super(Const.E_INVALID_DELIVERY);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
