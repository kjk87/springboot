package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class LpngCancelPeriodException extends ResultCodeException {
	public LpngCancelPeriodException() {
		super(Const.E_LPNG_CANCEL_PERIOD_EXPIRED);
	}

	public LpngCancelPeriodException(Object...args) {
		super(Const.E_LPNG_CANCEL_PERIOD_EXPIRED);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
