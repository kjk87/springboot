package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidShippingSiteException extends ResultCodeException {
	public InvalidShippingSiteException() {
		super(Const.E_INVALID_SHIPPING_SITE);
	}

	public InvalidShippingSiteException(Object...args) {
		super(Const.E_INVALID_SHIPPING_SITE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
