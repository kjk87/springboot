package kr.co.pplus.store.api.util;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class ByteResponseWrapper extends HttpServletResponseWrapper {
	private PrintWriter writer;
	private ByteOutputStream output;

	public byte[] getBytes() {
		writer.flush();
		return output.getBytes();
	}

	public ByteResponseWrapper(HttpServletResponse response) {
		super(response);
		output = new ByteOutputStream();
		writer = new PrintWriter(output);
	}

	@Override
	public PrintWriter getWriter() {
		return writer;
	}

	@Override
	public ServletOutputStream getOutputStream() {
		return output;
	}

	static class ByteOutputStream extends ServletOutputStream {

		private ByteArrayOutputStream bos = new ByteArrayOutputStream();

		@Override
		public void write(int b) {
			bos.write(b);
		}

		public byte[] getBytes() {
			return bos.toByteArray();
		}

		@Override
		public boolean isReady() {
			return false;
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {

		}
	}

}
