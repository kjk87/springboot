package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NotMatchPwdException extends ResultCodeException {
	private static final long serialVersionUID = 6091850234536713124L;

	public NotMatchPwdException() {
		super(Const.E_NOTMATCHEDPWD);
	}
}
