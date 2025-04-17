package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PasswordFailLimited extends ResultCodeException {
	public PasswordFailLimited() {
		super(Const.E_MAX_PASSWORD_FAILED);
	}
}
