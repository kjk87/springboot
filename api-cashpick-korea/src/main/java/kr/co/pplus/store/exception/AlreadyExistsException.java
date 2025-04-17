package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AlreadyExistsException extends ResultCodeException {

	private static final long serialVersionUID = 1756502187423843041L;

	public AlreadyExistsException() {
		super(Const.E_ALREADYEXISTS);
	}
	
	public AlreadyExistsException(Object...args) {
		super(Const.E_ALREADYEXISTS);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
