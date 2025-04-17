package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class RemoveBasicGoodsCategoryException extends ResultCodeException {
	public RemoveBasicGoodsCategoryException() {
		super(Const.E_REMOVE_BASIC_GOODS_CATEGORY);
	}

	public RemoveBasicGoodsCategoryException(Object...args) {
		super(Const.E_REMOVE_BASIC_GOODS_CATEGORY);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
