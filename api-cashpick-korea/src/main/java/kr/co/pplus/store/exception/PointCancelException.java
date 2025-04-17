package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class PointCancelException extends ResultCodeException {
	public PointCancelException() {
		super(Const.E_INVALID_POINT_CANCEL);
	}

	public PointCancelException(Object...args) {
		super(Const.E_INVALID_POINT_CANCEL);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
 }
