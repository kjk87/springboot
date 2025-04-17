package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;

public class NotPermissionException extends ResultCodeException {
	public NotPermissionException() {
		super(Const.E_NOTPERMISSION);
	}
	
	public NotPermissionException(Map<String, Object> extra) {
		super(Const.E_NOTPERMISSION);
		setExtra(extra);
	}
	
	public NotPermissionException(Object...args) {
		super(Const.E_NOTPERMISSION);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
