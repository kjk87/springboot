package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidGoodsDeleteException extends ResultCodeException {
	public InvalidGoodsDeleteException() {
		super(Const.E_INVALID_GOODS_DELETE);
	}

	public InvalidGoodsDeleteException(Object...args) {
		super(Const.E_INVALID_GOODS_DELETE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
