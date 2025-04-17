package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UnknownException extends ResultCodeException {
	public UnknownException() {
		super(Const.E_UNKNOWN);
	}
	
	public UnknownException(Object...args) {
		super(Const.E_UNKNOWN);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
