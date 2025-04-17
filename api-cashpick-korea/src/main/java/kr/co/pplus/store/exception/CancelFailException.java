package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class CancelFailException extends ResultCodeException {
	public CancelFailException() {
		super(Const.E_CANCEL_FAIL);
	}

	public CancelFailException(Object...args) {
		super(Const.E_CANCEL_FAIL);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
