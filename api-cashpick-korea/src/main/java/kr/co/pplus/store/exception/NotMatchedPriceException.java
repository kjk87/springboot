package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotMatchedPriceException extends ResultCodeException {
	public NotMatchedPriceException() {
		super(Const.E_NOTMATCHED_PRICE);
	}
	
	public NotMatchedPriceException(Object...args) {
		super(Const.E_NOTMATCHED_PRICE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
