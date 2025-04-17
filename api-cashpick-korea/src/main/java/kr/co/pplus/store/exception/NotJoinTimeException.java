package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NotJoinTimeException extends ResultCodeException {

	private static final long serialVersionUID = 1756502187423843041L;

	public NotJoinTimeException() {
		super(Const.E_NOT_JOIN_TIME);
	}

	public NotJoinTimeException(Object...args) {
		super(Const.E_NOT_JOIN_TIME);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
