package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NotMatchUserException extends ResultCodeException {
	private static final long serialVersionUID = -2531664780094308655L;

	public NotMatchUserException() {
		super(Const.E_NOTMATCHEDUSER);
	}
	
	public NotMatchUserException(Object...args) {
		super(Const.E_NOTMATCHEDUSER);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
