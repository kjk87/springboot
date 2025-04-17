package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotYetElapseTimeException extends ResultCodeException {

	private static final long serialVersionUID = 7025398857607070710L;

	public NotYetElapseTimeException() {
		super(Const.E_NOT_YET_ELAPSE_TIME);
	}
	
	public NotYetElapseTimeException(Object...args) {
		super(Const.E_NOT_YET_ELAPSE_TIME);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
