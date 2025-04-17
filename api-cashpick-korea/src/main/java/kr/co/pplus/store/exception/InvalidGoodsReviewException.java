package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class InvalidGoodsReviewException extends ResultCodeException {
	public InvalidGoodsReviewException() {
		super(Const.E_INVALID_GOODS_REVIEW);
	}

	public InvalidGoodsReviewException(Object...args) {
		super(Const.E_INVALID_GOODS_REVIEW);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
