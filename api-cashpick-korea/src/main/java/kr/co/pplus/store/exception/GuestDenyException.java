package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class GuestDenyException extends ResultCodeException {
	public GuestDenyException() {
		super(Const.E_GUESTDENY);
	}
}
