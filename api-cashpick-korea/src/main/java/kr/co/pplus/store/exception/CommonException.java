package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.util.StoreUtil;

public class CommonException extends ResultCodeException {
	public CommonException(int code) {
		super(code);
	}
	
	public CommonException(int code, Map<String, Object> extra) {
		this(code);
		setExtra(extra);
	}
	
	public CommonException(int code, Object...args) {
		this(code);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}	
}
