package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidPageSellerException extends ResultCodeException {
	public InvalidPageSellerException() {
		super(Const.E_INVALID_PAGE_SELLER);
	}

	public InvalidPageSellerException(Object...args) {
		super(Const.E_INVALID_PAGE_SELLER);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
