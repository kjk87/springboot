package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidGoodsException extends ResultCodeException {
	public InvalidGoodsException() {
		super(Const.E_INVALID_GOODS);
	}

	public InvalidGoodsException(Object...args) {
		super(Const.E_INVALID_GOODS);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
