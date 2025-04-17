package kr.co.pplus.store.api.util;

import java.util.Map;

import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.dto.ResultMap;

public class ResultBuilder {
	public static Map<String,Object> build(Integer resultCode) {
		return new ResultMap(resultCode);
	}
	
	public static Map<String,Object> build(Integer resultCode, Map<String, Object> map) {
		return new ResultMap(resultCode, map);
	}
	
	public static Map<String,Object> build(Integer resultCode, Object...args) {
		return new ResultMap(resultCode, args);
	}
	
	public static Map<String,Object> build(ResultCodeException ex) {
		if (ex.getExtra() == null)
			return new ResultMap(ex.getResultCode());
		else
			return new ResultMap(ex.getResultCode(), "errorExtra", ex.getExtra());
	}

	public static Map<String,Object> build(String requestId, ResultCodeException ex) {
		if (ex.getExtra() == null)
			return new ResultMap(ex.getResultCode());
		else
			return new ResultMap(ex.getResultCode(), "errorExtra", ex.getExtra());
	}
}
