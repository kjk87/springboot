package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.type.Const;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NotMatchedVerificationException extends ResultCodeException {
	private static final long serialVersionUID = 770825371978344305L;

	public NotMatchedVerificationException() {
		super(Const.E_NOTMATCH_VERIFICATION);
	}
}
