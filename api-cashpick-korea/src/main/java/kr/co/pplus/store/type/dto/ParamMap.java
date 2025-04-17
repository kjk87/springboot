package kr.co.pplus.store.type.dto;

import java.util.HashMap;

import kr.co.pplus.store.type.model.Model;

public class ParamMap extends HashMap<String, Object> {
	public ParamMap(Object...args) {
		put("resultCode", 501);
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg == null)
				continue;
			
			if (arg instanceof Model) {
				
				Model dto = (Model)arg;
				dto.toMap(this, true);
			} else {
				String key = (String)args[i++];
				Object value = args[i];
				if (value == null)
					continue;
				
				if (value instanceof Model) {
					((Model)value).toMap(this, true);
				} else {
					put(key, value);
				}
			}
		}
	}
	
	public Integer getResultCode() {
		return (Integer)get("resultCode");
	}
}
