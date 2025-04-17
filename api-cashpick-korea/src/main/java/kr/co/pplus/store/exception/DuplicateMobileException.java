package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;

public class DuplicateMobileException extends ResultCodeException {
	public DuplicateMobileException() {
		super(Const.E_ALREADYEXISTS);
	}
}
