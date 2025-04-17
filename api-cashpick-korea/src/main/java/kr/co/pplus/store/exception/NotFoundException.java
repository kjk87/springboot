package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotFoundException extends ResultCodeException {
	public NotFoundException() {
		super(Const.E_NOTFOUND);
	}

	public NotFoundException(Object...args) {
		super(Const.E_NOTFOUND);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
