package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NeedAppUpdateException extends ResultCodeException {

	public NeedAppUpdateException() {
		super(Const.E_NEED_APP_UPDATE);
	}
	
	public NeedAppUpdateException(Object...args) {
		super(Const.E_NEED_APP_UPDATE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
