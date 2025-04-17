package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DuplicateLoginIdException extends ResultCodeException {
	private static final long serialVersionUID = 7879574398461734568L;

	public DuplicateLoginIdException() {
		super(Const.E_DUPL_LOGINID);
	}
}
