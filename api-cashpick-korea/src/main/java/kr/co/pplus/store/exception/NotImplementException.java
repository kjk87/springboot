package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NotImplementException extends ResultCodeException {
	public NotImplementException() {
		super(Const.E_NOTIMPLEMENT);
	}
}
