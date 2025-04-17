package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidPageException extends ResultCodeException {
	public InvalidPageException() {
		super(Const.E_INVALID_PAGE);
	}
	
	public InvalidPageException(Object...args) {
		super(Const.E_INVALID_PAGE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
