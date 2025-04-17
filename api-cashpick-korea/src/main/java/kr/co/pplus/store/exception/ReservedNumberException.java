package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ReservedNumberException extends ResultCodeException {

	private static final long serialVersionUID = 5240805879962555233L;

	public ReservedNumberException() {
		super(Const.E_RESERVED_NUMBER);
	}
	
	public ReservedNumberException(Object...args) {
		super(Const.E_RESERVED_NUMBER);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
