package kr.co.pplus.store.api.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.output.TeeOutputStream;

public class ResponseWrapper extends HttpServletResponseWrapper {
	private ByteArrayOutputStream bos;
	private PrintWriter writer;
	private long id;
	
	public ResponseWrapper(Long requestId, HttpServletResponse response) {
		super(response);
		this.id = requestId;
		bos = new ByteArrayOutputStream();
		writer = new PrintWriter(bos);
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return new DelegatingServletOutputStream(new TeeOutputStream(super.getOutputStream(), bos));
	}

	@Override
	public ServletResponse getResponse() {
		return this;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new TeePrintWriter(super.getWriter(), writer);
	}

	public byte[] getBytes() {
		writer.flush();
		return bos.toByteArray();
	}
	
	public byte[] toByteArray() {
		return bos.toByteArray();
	}
}
