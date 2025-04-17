package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class AlreadyJoinException extends ResultCodeException {
	public AlreadyJoinException() {
		super(Const.E_ALREADY_JOIN);
	}
	
	public AlreadyJoinException(Object...args) {
		super(Const.E_ALREADY_JOIN);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
