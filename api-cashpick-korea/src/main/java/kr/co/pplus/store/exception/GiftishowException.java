package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class GiftishowException extends ResultCodeException {


	public GiftishowException() {
		super(Const.E_GIFTISHOW_ERROR);
	}

	public GiftishowException(Object...args) {
		super(Const.E_GIFTISHOW_ERROR);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
