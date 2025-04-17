package kr.co.pplus.store.api.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.input.TeeInputStream;

public class RequestWrapper extends HttpServletRequestWrapper {
	private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
	private long id;
	
	public RequestWrapper(Long requestId, HttpServletRequest request) throws IOException {
		super(request);
		this.id = requestId;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new DelegatingServletInputStream(new TeeInputStream(super.getInputStream(), bos));
	}
	
	public byte[] toByteArray() {
		return bos.toByteArray();
	}
	
	
}
