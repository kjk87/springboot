package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidPageGoodsInfoException extends ResultCodeException {
	public InvalidPageGoodsInfoException() {
		super(Const.E_INVALID_PAGE_GOODS_INFO);
	}

	public InvalidPageGoodsInfoException(Object...args) {
		super(Const.E_INVALID_PAGE_GOODS_INFO);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
