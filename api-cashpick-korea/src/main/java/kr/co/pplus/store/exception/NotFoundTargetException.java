package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NotFoundTargetException extends ResultCodeException {
	public NotFoundTargetException() {
		super(Const.E_NOTFOUND);
	}
	
	public NotFoundTargetException(Object...args) {
		super(Const.E_NOTFOUND);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
