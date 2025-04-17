package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidEventRevewException extends ResultCodeException {
	public InvalidEventRevewException() {
		super(Const.E_INVALID_EVENT_REVIEW);
	}

	public InvalidEventRevewException(Object...args) {
		super(Const.E_INVALID_EVENT_REVIEW);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
