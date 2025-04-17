package kr.co.pplus.store.exception;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;

import java.util.Map;

public class SqlException extends ResultCodeException {
	private static final long serialVersionUID = 5240805879962555237L;

	public SqlException() {
		super(Const.E_INVALID_SQL);
	}

	public SqlException(Object...args) {
		super(Const.E_INVALID_SQL);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
