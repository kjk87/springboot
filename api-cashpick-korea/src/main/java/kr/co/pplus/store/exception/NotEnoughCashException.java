package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotEnoughCashException extends ResultCodeException {
	public NotEnoughCashException() {
		super(Const.E_NOTENOUGH_CASH);
	}
	
	public NotEnoughCashException(Object...args) {
		super(Const.E_NOTENOUGH_CASH);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
