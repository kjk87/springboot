package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

public class NotPossibleDeleteException extends ResultCodeException {
	public NotPossibleDeleteException() {
		super(Const.E_NOTPOSSIBLEDELETE);
	}
	
	public NotPossibleDeleteException(Map<String, Object> extra) {
		super(Const.E_NOTPOSSIBLEDELETE);
		setExtra(extra);
	}
	
	public NotPossibleDeleteException(Object...args) {
		super(Const.E_NOTPOSSIBLEDELETE);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
