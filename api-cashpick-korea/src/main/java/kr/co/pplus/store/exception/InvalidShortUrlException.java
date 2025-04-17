package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidShortUrlException extends ResultCodeException {
	public InvalidShortUrlException() {
		super(Const.E_INVALID_SHORTURL);
	}

	public InvalidShortUrlException(Object...args) {
		super(Const.E_INVALID_SHORTURL);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
