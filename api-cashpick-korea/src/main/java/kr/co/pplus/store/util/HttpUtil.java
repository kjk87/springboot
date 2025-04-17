package kr.co.pplus.store.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class HttpUtil {
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	
	private static DateFormat jsonDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	private static ObjectMapper getObjectMapper() {

		return getObjectMapper(false, false);
	}

	public static String beautify(String json) {
		ObjectMapper mapper = getObjectMapper();
		Object obj;
		try {
			obj = mapper.readValue(json, Object.class);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (Exception e) {
			return json;
		}
	}

	public static String beautify(Object obj) {
		ObjectMapper mapper = getObjectMapper();

		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (Exception e) {
			return "";
		}
	}

	static ObjectMapper objectMapper = null;
	static ObjectMapper objectMapperIncNull = null;

	private static ObjectMapper getObjectMapper(Boolean incNull, Boolean indent) {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			//objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
	
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, indent);
	
			if (incNull != null)
				objectMapper.setSerializationInclusion(incNull ? JsonInclude.Include.ALWAYS : JsonInclude.Include.NON_NULL);
			
			objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			objectMapper.setDateFormat(jsonDateFormat);
		
		}
		return objectMapper;
	}
	
	
	public static String requestPostString(String strUrl, int connectionTimeout, int readTimeout, Map<String, String> params) throws IOException {
		URL url = null;
		url = new URL(strUrl);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setInstanceFollowRedirects(true);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setConnectTimeout(connectionTimeout * 1000);
		conn.setReadTimeout(readTimeout * 1000);
		conn.setUseCaches(false);
		conn.setDefaultUseCaches(false);
		
		if (params != null && params.size() > 0) {
			StringBuffer buf = new StringBuffer();
			boolean first = true;
			for(Map.Entry<String, String> entry : params.entrySet()) {
				if (first) {
					buf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
					first = false;
				} else {
					buf.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
				}
			}
			
			DataOutputStream out = null;
			
			try {
				out = new DataOutputStream(conn.getOutputStream());
				out.writeBytes(buf.toString());
				out.flush();
			} finally {
				if (out != null)
					out.close();
			}
		}
		
		InputStream is = conn.getInputStream();
		Scanner scan = new Scanner(is);
		StringBuffer out = new StringBuffer();
		int line = 1;
		while (scan.hasNext()) {
			out.append(scan.nextLine()).append("\r\n");
		}
		scan.close();
		return out.toString();
	}
	
	public static String requestGetString(String strUrl, int connectionTimeout, int readTimeout, Map<String, String> header) throws IOException {
		URL url = null;
		url = new URL(strUrl);
		URLConnection conn = url.openConnection();
		
		if (header != null && header.size() > 0) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		
		conn.setConnectTimeout(connectionTimeout * 1000);
		conn.setReadTimeout(readTimeout * 1000);
		conn.connect();

		InputStream is = conn.getInputStream();
		Scanner scan = new Scanner(is);
		StringBuffer out = new StringBuffer();
		int line = 1;
		while (scan.hasNext()) {
			out.append(scan.nextLine()).append("\r\n");
		}
		scan.close();
		return out.toString();
	}

	public static String requestGetString(String strUrl, String charsetName, int connectionTimeout, int readTimeout, Map<String, String> header) throws IOException {
		URL url = null;
		url = new URL(strUrl);
		URLConnection conn = url.openConnection();
		
		if (header != null && header.size() > 0) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		
		conn.setConnectTimeout(connectionTimeout * 1000);
		conn.setReadTimeout(readTimeout * 1000);
		conn.connect();

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charsetName));
		
		StringBuffer out = new StringBuffer();
		String line = null;
		while ((line = in.readLine()) != null) {
			out.append(line).append("\n");
		}
		in.close();
		return out.toString();
	}
	
	private static void writeWriter(Object obj, OutputStream os) throws IOException, JsonGenerationException, JsonMappingException {
		ObjectMapper om = getObjectMapper();
		om.writeValue(os, obj);
	}
	
	public static <T>  T requestJsonObject(String strUrl, String charsetName, int connectionTimeout, int readTimeout, Map<String, String> header, Object input, Class<T> resCls) throws IOException, JsonGenerationException, JsonMappingException {
		URL url = null;
		url = new URL(strUrl);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setInstanceFollowRedirects(true);
		if (header != null && header.size() > 0) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		conn.setConnectTimeout(connectionTimeout);
		conn.setReadTimeout(readTimeout);
		conn.connect();
		
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		writeWriter(input, out);
		out.flush();
		
		InputStream is = conn.getInputStream();
		
		if (resCls.equals(File.class)) {
			File f = File.createTempFile("json.",  ".tmp");
			FileOutputStream fos = new FileOutputStream(f);
			byte[] buf = new byte[1024];
			while (true) {
				int r = is.read(buf, 0, 1024);
				if (r < 0) {
					break;
				}
				fos.write(buf, 0, r);
			}
			fos.close();
			return (T)f;
		} else if (resCls.equals(String.class)) {
			Scanner scan = new Scanner(is);
			StringBuffer buf = new StringBuffer();
			int line = 1;
			while (scan.hasNext()) {
				buf.append(scan.nextLine()).append("\r\n");
			}
			scan.close();
			return (T)out.toString();
		}
		
		T result = getObjectMapper().readValue(is, resCls);
		is.close();
		out.close();
		return result;
	}
	
	public static String downloadFile(String downloadUrl, File f)
			throws Exception {
		OutputStream os = null;
		URLConnection conn = null;
		InputStream is = null;

		try {
			URL url;
			byte[] buf;
			int byteRead;

			url = new URL(downloadUrl);
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			conn = url.openConnection();
			is = conn.getInputStream();
			buf = new byte[1024];
			os = new BufferedOutputStream(new FileOutputStream(f));
			while ((byteRead = is.read(buf)) != -1) {
				os.write(buf, 0, byteRead);
			}

		} catch (Exception ex) {
			logger.error("url:" + downloadUrl, ex.getMessage());

			throw ex;
		} finally {
			try {
				if (is != null)
					is.close();
				if (os != null)
					os.close();
			} catch (IOException ex) {
				logger.error("close:" + downloadUrl, ex.getMessage());
				throw ex;
			}
		}
		return f.getAbsolutePath();
	}	

	
	private static boolean initTrust = false;

	public static void trustEveryone() {
		if (initTrust)
			return;
		initTrust = true;
		try {
			HttpsURLConnection
					.setDefaultHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String hostname,
								SSLSession session) {
							return true;
						}
					});
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context
					.getSocketFactory());
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}
	}
	
	static {
		trustEveryone();
	}
}
