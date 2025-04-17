package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotPermissionForOfferException extends ResultCodeException {
	public NotPermissionForOfferException() {
		super(Const.E_NOTPERMISSIONFOROFFER);
	}
	
	public NotPermissionForOfferException(Object...args) {
		super(Const.E_NOTPERMISSIONFOROFFER);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
