package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class ExpiredException extends ResultCodeException {
	public ExpiredException() {
		super(Const.E_EXPIREDEXCEPTION);
	}
	
	public ExpiredException(Object...args) {
		super(Const.E_EXPIREDEXCEPTION);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
