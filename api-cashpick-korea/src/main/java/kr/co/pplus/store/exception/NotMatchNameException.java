package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotMatchNameException extends ResultCodeException {
	public NotMatchNameException() {
		super(Const.E_NOTMATCHED_NAME);
	}
	
	public NotMatchNameException(Object...args) {
		super(Const.E_NOTMATCHED_NAME);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
