package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidGoodsCategoryException extends ResultCodeException {
	public InvalidGoodsCategoryException() {
		super(Const.E_INVALID_GOODS_CATEGORY);
	}

	public InvalidGoodsCategoryException(Object...args) {
		super(Const.E_INVALID_GOODS_CATEGORY);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
