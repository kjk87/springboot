package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UnknownRecommenderException extends ResultCodeException {

	private static final long serialVersionUID = 9196820256260346862L;

	public UnknownRecommenderException() {
		super(Const.E_UNKNOWNRECOMMEND);
	}
	

}
