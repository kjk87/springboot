package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RefreshKeyNotFoundException extends ResultCodeException {
	private static final long serialVersionUID = -1405913614878694489L;

	public RefreshKeyNotFoundException(Object...args) {
		super(Const.E_REFRESH_KEY_EXPIRED);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
