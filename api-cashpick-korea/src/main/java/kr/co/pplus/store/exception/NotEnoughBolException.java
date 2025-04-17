package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotEnoughBolException extends ResultCodeException {
	public NotEnoughBolException() {
		super(Const.E_NOTENOUGH_BOL);
	}
	
	public NotEnoughBolException(Object...args) {
		super(Const.E_NOTENOUGH_BOL);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
