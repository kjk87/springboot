package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DuplicateNicknameException extends ResultCodeException {

	private static final long serialVersionUID = 2399712115006233103L;

	public DuplicateNicknameException() {
		super(Const.E_DUPL_NICKNAME);
	}
	

}
