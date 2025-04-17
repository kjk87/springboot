package kr.co.pplus.store.type.dto;

import java.util.HashMap;
import java.util.Map;

import kr.co.pplus.store.type.Const;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("ResultMap")
public class ResultMap extends HashMap<String, Object> {
	private Integer resultCode;
	private String requestId = "";
	
	public ResultMap() {
		resultCode = Const.E_UNKNOWN;
	}
	
	public ResultMap(Integer resultCode) {
		this.resultCode = resultCode;
		put("resultCode", resultCode);
	}
	
	public ResultMap(Integer resultCode, Map<String, Object> map) {
		this.resultCode = resultCode;
		put("resultCode", resultCode);
		for (Entry<String, Object> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	public ResultMap(Integer resultCode, Object...args) {
		this.resultCode = resultCode;
		put("resultCode", resultCode);
		for (int i = 0; i < args.length; i = i + 2) {
			String key = (String)args[i];
			Object value = args[i + 1];
			put(key, value);
		}
	}

	public ResultMap(Integer resultCode, String reqId) {
		this.resultCode = resultCode;
		this.requestId = reqId ;
		put("resultCode", resultCode);
		put("requestId", reqId);
	}
	
}
