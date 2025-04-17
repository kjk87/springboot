package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidBuyGoodsProcessException extends ResultCodeException {
	public InvalidBuyGoodsProcessException() {
		super(Const.E_INVALID_BUY_GOODS_PROCESS);
	}

	public InvalidBuyGoodsProcessException(Object...args) {
		super(Const.E_INVALID_BUY_GOODS_PROCESS);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
