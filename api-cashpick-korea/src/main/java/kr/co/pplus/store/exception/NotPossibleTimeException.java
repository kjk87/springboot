package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotPossibleTimeException extends ResultCodeException {
	public NotPossibleTimeException() {
		super(Const.E_NOTPOSSIBLETIME);
	}
	
	public NotPossibleTimeException(Map<String, Object> extra) {
		super(Const.E_NOTPOSSIBLETIME);
		setExtra(extra);
	}
	
	public NotPossibleTimeException(Object...args) {
		super(Const.E_NOTPOSSIBLETIME);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
