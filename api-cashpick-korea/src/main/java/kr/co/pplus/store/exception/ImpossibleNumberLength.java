package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;

public class ImpossibleNumberLength extends ResultCodeException {
	
	private static final long serialVersionUID = -870387959351015652L;

	public ImpossibleNumberLength() {
		super(Const.E_IMPOSSIBLE_NUMBER_LENGTH);
	}
	
}
