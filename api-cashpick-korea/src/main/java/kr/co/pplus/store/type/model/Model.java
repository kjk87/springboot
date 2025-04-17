package kr.co.pplus.store.type.model;

import java.io.Serializable;
import java.util.Map;

public interface Model extends Serializable {
	public Map<String, Object> toMap(Map dest, boolean excludeNull);
	
	public Model fromMap(Map<String, Object> src, boolean excludeNull);

}
