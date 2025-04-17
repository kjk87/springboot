package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidPageGoodsCategoryException extends ResultCodeException {
	public InvalidPageGoodsCategoryException() {
		super(Const.E_INVALID_PAGE_GOODS_CATEGORY);
	}

	public InvalidPageGoodsCategoryException(Object...args) {
		super(Const.E_INVALID_PAGE_GOODS_CATEGORY);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
