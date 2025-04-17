package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidSnsVerificationException extends ResultCodeException {
	
	private static final long serialVersionUID = -3141584391804624566L;

	public InvalidSnsVerificationException() {
		super(Const.E_INVALID_SNS_VERIFICATION);
	}
	
	public InvalidSnsVerificationException(Object...args) {
		super(Const.E_INVALID_SNS_VERIFICATION);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
