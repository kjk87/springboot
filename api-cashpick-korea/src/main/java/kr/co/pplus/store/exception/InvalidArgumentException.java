package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class InvalidArgumentException extends ResultCodeException {
	private static final long serialVersionUID = 7670379816814280211L;

	public InvalidArgumentException() {
		super(Const.E_INVALID_ARG);
	}
	
	public InvalidArgumentException(Object...args) {
		super(Const.E_INVALID_ARG);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
