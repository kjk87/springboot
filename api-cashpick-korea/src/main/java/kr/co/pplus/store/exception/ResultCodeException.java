package kr.co.pplus.store.exception;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ResultCodeException extends Exception implements Map<String, Object> {

	private static final long serialVersionUID = 4761139815769693305L;
	
	private Integer resultCode = 501;

	private Map<String, Object> extra = new HashMap<String, Object>() ;
	
	public ResultCodeException(Integer resultCode) {
		this.resultCode = resultCode;
		this.put("resultCode", resultCode) ;
	}
	
	public ResultCodeException(Integer resultCode, Map<String, Object> extra) {
		this.resultCode = resultCode;
		this.clear() ;
		this.putAll(extra) ;
		this.put("resultCode", resultCode) ;
	}

	public void setExtra(Map<String, Object> map) {
		this.extra = map ;
		this.extra.put("resultCode", this.resultCode) ;
	}

	@Override
	public int size() {
		return this.extra.size();
	}

	@Override
	public boolean isEmpty() {
		return this.extra.isEmpty() ;
	}

	@Override
	public boolean containsKey(Object key) {
		return this.extra.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.extra.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return this.extra.containsValue(key);
	}

	@Override
	public Object put(String key, Object value) {
		return this.extra.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return this.extra.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		this.extra.putAll(m);
	}


	@Override
	public void clear() {
		this.extra.clear() ;
	}

	@Override
	public Set<String> keySet() {
		return this.extra.keySet() ;
	}

	@Override
	public Collection<Object> values() {
		return this.extra.values() ;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return this.extra.entrySet() ;
	}
}
