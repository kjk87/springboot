package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidGoodsImageException extends ResultCodeException {
	public InvalidGoodsImageException() {
		super(Const.E_INVALID_GOODS_IMAGE);
	}

	public InvalidGoodsImageException(Object...args) {
		super(Const.E_INVALID_GOODS_IMAGE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
