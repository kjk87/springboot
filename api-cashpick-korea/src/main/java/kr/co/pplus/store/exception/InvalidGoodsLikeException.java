package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidGoodsLikeException extends ResultCodeException {
	public InvalidGoodsLikeException() {
		super(Const.E_INVALID_BUY_GOODS_LIKE);
	}

	public InvalidGoodsLikeException(Object...args) {
		super(Const.E_INVALID_BUY_GOODS_LIKE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
