package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class InvalidOauth2TokenException extends ResultCodeException {

	private static final long serialVersionUID = 3756502187423843047L;

	public InvalidOauth2TokenException() {
		super(Const.E_INVALID_OAUTH);
	}

	public InvalidOauth2TokenException(Object...args) {
		super(Const.E_INVALID_OAUTH);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
