package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidBuyGoodsException extends ResultCodeException {
	public InvalidBuyGoodsException() {
		super(Const.E_INVALID_BUY_GOODS);
	}

	public InvalidBuyGoodsException(Object...args) {
		super(Const.E_INVALID_BUY_GOODS);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
