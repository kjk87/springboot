package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotAllowCharacterException extends ResultCodeException {
	public NotAllowCharacterException() {
		super(Const.E_NOTALLOWED_CHAR);
	}
	
	public NotAllowCharacterException(Object...args) {
		super(Const.E_NOTALLOWED_CHAR);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
