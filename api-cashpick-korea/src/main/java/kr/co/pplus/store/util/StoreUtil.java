package kr.co.pplus.store.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.type.dto.DaumCoordResult;
import kr.co.pplus.store.type.model.*;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class StoreUtil {
	private static final String CRLF = "\r\n";

	private static Random random = new Random();
	private static SimpleDateFormat sf;
	private static StringBuilder sb;

	public static String getLottoID(String type) {

		sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		sb = new StringBuilder();

		int randNum = random.nextInt(10000) + 1000;
		if (randNum > 10000) {
			randNum = randNum - 1000;
		}

		String nowDate = sf.format(new Date());
		sb.append(type);
		sb.append(nowDate);
		sb.append(randNum);


		return sb.toString().substring(0, 21);

	}

	public static String getRandomOrderId() throws Exception{
		SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG", "SUN");
		int number = secureRandomGenerator.nextInt(90000) + 10000;
		int oneDigit = secureRandomGenerator.nextInt(10);

		Date now = new Date();
		SimpleDateFormat transFormat = new SimpleDateFormat("yyMMddHHmmss");
		String to = transFormat.format(now);

		String orderId = to + oneDigit + number;
		return orderId;
	}
	
	public static String getRandomKeyByUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String getRandomString(String chars[], int length) {
		StringBuffer buf = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			buf.append(chars[random.nextInt(chars.length)]);
		}
		return buf.toString();
	}

	public static <T> T getBean(Class<T> clz) {
		return ApplicationContextProvider.getBean(clz);
	}
	
	public static int getRandomNumber(int total) {
		Random random = new Random();
		return random.nextInt(total);
	}
	
	public static boolean lots(int total, int probability) {
		int r = getRandomNumber(total);
		return r <= probability;
	}
	
	public static boolean lots(Double percent) {

		int total = (int)Math.ceil(100/percent);
		Random random = new Random();
		return random.nextInt(total) == random.nextInt(total);
	}

//	public static Session getTestSession() {
//		Session session = new Session() ;
//		User user = new User() ;
//		user.setNo(1L) ;
//		user.setEmail("mgkaki@daum.net");
//		user.setMobile("01099902251");
//		user.setNickname("Peter");
//		user.setAccountType("pplus");
//		user.setUseStatus("normal");
//		user.setBaseAddr("서울시 성동구 송정4길 18");
//		user.setCertificationLevel((short)1);
//		user.setTotalBol(100000000f);
//		user.setTotalCash(100000000L);
//		session.setModUser(user);
//		session.setUserNo(user.getNo());
//		Page page = new Page() ;
//		page.setNo(1L) ;
//		page.setName("PR NUMBER");
//		page.setStatus("normal");
//		page.setUser(user) ;
//		page.setModUser(user) ;
//		page.setTalkRecvBound("everybody");
//		page.setValuationPoint(0L);
//		session.setPage(page);
//		session.setSessionKey("-test-");
//		session.setMobile("01012345678");
//		session.setCertificationLevel((short)1);
//		session.setUseStatus("normal");
//		session.setAccountType("pplus") ;
//		session.setEmail("mgkaki@daum.net");
//		session.setCountry(new Country());
//		session.getCountry().setNo(1L);
//		session.setTotalBol(100000000f);
//		session.setTotalCash(100000000L);
//		return session ;
//	}

	public static User getCommonAdmin() {
		User user = new User();
		user.setNo(2L);
		user.setName("오리마켓 운영팀");
		return user;
	}

	public static Page getCommonAdminPage() {
		Page page = new Page();
		page.setNo(1L);
		page.setName("오리마켓 운영팀") ;
		User user = new User();
		user.setNo(2L);
		user.setName("오리마켓 운영팀");
		page.setUser(user) ;
		return page;
	}

	public static String getTemplateSubCode(String type) {
		String code = null;

		if ("findId".equals(type))
			code = "AD101";
		else if ("findPassword".equals(type))
			code = "AD102";
		else if ("join".equals(type))
			code = "AD103";
		else if ("profile".equals(type) || "changeMobile".equals(type))
			code = "AD104";
		else if ("leave".equals(type))
			code = "AD105";
		else if ("cancelLeave".equals(type))
			code = "AD106";
		return code;
	}
	
	public static String generateRandomNumber(int length) {
		SecureRandom random = new SecureRandom();
		char[] arr = new char[length];
		for (int i = 0; i < length; i++)
			arr[i] = (char)(random.nextInt(9) + 48);
		return String.valueOf(arr);
	}
	
	public static String generateVerificationToken(String number) {
		byte[] bytes = new byte[24];

		UUID uuid = UUID.randomUUID();
		NumberUtil.copyLongToByteArray(bytes, 0, uuid.getMostSignificantBits());
		NumberUtil.copyLongToByteArray(bytes, 8, uuid.getLeastSignificantBits());
		NumberUtil.copyLongToByteArray(bytes, 16, Long.parseLong(number));
		return new String(NumberUtil.bytesToHexString(bytes));
		
	}
	
	public static String applyVariable(String src, Map<String, String> variableMap) {
		for (Entry<String, String> entry : variableMap.entrySet()) {
			src = src.replaceAll(entry.getKey(), entry.getValue());
		}
		return src;
	}
	
	public static Map<String, Object> convertStringKeyMap(Object...args) {
		if (args == null)
			return null;
		
		Map<String, Object> m = new HashMap<String, Object>();
		for (int i = 0; i < args.length; i++) {
			try {
				String key = (String)args[i++];
				Object value = args[i];
				if( value instanceof  Exception) {
					String errMsg = ((Exception) value).getMessage() + ":" + AppUtil.excetionToString((Exception)value) ;
					m.put(key, errMsg);
				} else {
					m.put(key, value);
				}
			} catch (Exception ex) {
				
			}
		}
		return m;
	}
	
	public static String urlEncoding(String value, String charset) throws UnsupportedEncodingException {
		if (value == null)
			return value;
		return URLEncoder.encode(value, charset);
	}
	
	public static String postRequest(String urlStr, Map<String, String> params, String charset, int connectionTimeout, int readTimeout) throws Exception {
		OutputStream os = null;
		HttpURLConnection conn = null;
		URL url = null;
		PrintWriter writer = null;
		
		url = new URL(urlStr);
		conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(connectionTimeout);
		conn.setReadTimeout(readTimeout);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept-Charset", charset);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		
		os = conn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(os, charset), true);
		if (params != null) {
			StringBuffer buf = new StringBuffer();
			boolean first = true;
			for (Entry<String, String> entry : params.entrySet()) {
				if (first) {
					buf.append(entry.getKey()).append("=").append(urlEncoding(entry.getValue(), charset));
					first = false;
				} else {
					buf.append("&").append(entry.getKey()).append("=").append(urlEncoding(entry.getValue(), charset));
				}
			}
			
			if (!first) {
				writer.append(buf.toString());
				System.out.println(urlStr + "?" + buf.toString());
			}
			
		}
		
		writer.close();
		
		int status = conn.getResponseCode();
		StringBuilder sb = new StringBuilder();
		if (status == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + CRLF);
			}
			reader.close();
			conn.disconnect();
		} else {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line + CRLF);
			}
			reader.close();
			conn.disconnect();			
			throw new IOException("Server returned non-OK status: " + urlStr + "," + status);
		}
		return sb.toString();
	}

	public static String getRequest(String urlStr, Map<String, String> params, String charset, int connectionTimeout, int readTimeout) throws Exception {
		OutputStream os = null;
		HttpURLConnection conn = null;
		URL url = null;
		PrintWriter writer = null;

		if (params != null) {
			StringBuffer buf = new StringBuffer();
			boolean first = true;
			for (Entry<String, String> entry : params.entrySet()) {
				if (first) {
					buf.append(entry.getKey()).append("=").append(urlEncoding(entry.getValue(), charset));
					first = false;
				} else {
					buf.append("&").append(entry.getKey()).append("=").append(urlEncoding(entry.getValue(), charset));
				}
			}
			
			if (!first) {
				urlStr += "?" + buf.toString();
			}
		}
		
		url = new URL(urlStr);
		conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(connectionTimeout);
		conn.setReadTimeout(readTimeout);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept-Charset", charset);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setUseCaches(false);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
		StringBuffer sb = new StringBuffer();
		int read = 0;
		char[] buf = new char[1024];
		while ((read = br.read(buf)) > 0) {
			sb.append(buf, 0, read);
		}
		br.close();
		
		return sb.toString();
	}

	public static Duration getDefaultAllocateNumberDuration() {
//		Duration d = new Duration();
//		d.setStart(DateUtil.getCurrentDate());
//		try {
//			d.setEnd(DateUtil.getDate(DateUtil.PATTERN, "20991231235959"));
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
		Duration d = new Duration();

		try {
			d.setStart(DateUtil.getDateString("yyyy-MM-dd HH:mm:ss", new Date()));
			d.setEnd("2099-12-31 23:59:59");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return d;
	}
	
	public static String changeMask(String src, int idx, boolean yes) {
		if (idx > 0)
			return src.substring(0, idx) + (yes ? "1" : "0") + src.substring(idx + 1, src.length());
		else
			return (yes ? "1" : "0") + src.substring(1, src.length());
	}
	
	public static boolean getMask(String src, int idx) {
		if (idx > 0)
			return src.substring(idx, idx+1).equals("1") ? true : false;
		else
			return src.substring(0, 1).equals("1") ? true : false;
	}
	
	public static void exceptAttachment(List<Attachment> src, Attachment dest) {
		int idx = -1;
		for (int j = 0; j < src.size(); j++) {
			Attachment s = src.get(j);
			if (dest.getNo().equals(s.getNo())) {
				idx = j;
				break;
			}
		}
		
		if (idx >= 0)
			src.remove(idx);
		return;
		
	}
	
	public static Coord converAddressToCoord(ObjectMapper om, String urlStr, String authKey, String address) throws IOException {
		Map<String, String> header = new HashMap<String, String>();
		header.put("Authorization", authKey);
		String url = urlStr + "?query=" + URLEncoder.encode(address, "utf-8");
		String res = HttpUtil.requestGetString(url, 10, 10, header);
		DaumCoordResult m = om.readValue(res, DaumCoordResult.class);
		if (m.getDocuments() != null && m.getDocuments().size() > 0) {
			Map d = m.getDocuments().get(0);
			if (d.containsKey("x") && d.containsKey("y")) {
				Coord coord = new Coord();
				coord.setX(Double.parseDouble((String)d.get("x")));
				coord.setY(Double.parseDouble((String)d.get("y")));
				return coord;
			}
		}
		return null;
	}

	public static Map convertCoordToAddress(ObjectMapper om, String urlStr, String authKey, String x, String y) throws IOException {
		Map<String, String> header = new HashMap<String, String>();
		header.put("Authorization", authKey);
		String url = urlStr + "?x=" + x + "&y="+y;
		String res = HttpUtil.requestGetString(url, 10, 10, header);
		DaumCoordResult m = om.readValue(res, DaumCoordResult.class);
		if (m.getDocuments() != null && m.getDocuments().size() > 0) {
			Map d = m.getDocuments().get(0);
			return d;
		}
		return null;
	}
	
	public static String getValidatePhoneNumber(String src) {
		if (src == null)
			return null;
		
		return src.replaceAll("[\\s\\-\\+\\(\\)]+", "");
	}

	public static boolean isCellPhoneNumber(String number) {

		String regex = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$";
		return Pattern.matches(regex, number);
	}

	public static File toFile(URL url) {
		if (url == null || !url.getProtocol().equals("file")) {
			return null;
		} else {
			String filename = url.getFile().replace('/', File.separatorChar);
			int pos =0;
			while ((pos = filename.indexOf('%', pos)) >= 0) {
				if (pos + 2 < filename.length()) {
					String hexStr = filename.substring(pos + 1, pos + 3);
					char ch = (char) Integer.parseInt(hexStr, 16);
					filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
				}
			}
			return new File(filename);
		}
	}

	public static InputStream getClassLoaderFile(String filename) throws Exception  {
		// note that this method is used when initializing logging, so it must
		// not attempt to log anything.

		File file = null;
		ClassLoader loader = StoreUtil.class.getClassLoader();
		InputStream inputStream = loader.getResourceAsStream(filename);
		if( inputStream != null ) {
			return inputStream ;
		} else {
			URL url = loader.getResource(filename);
			if (url == null) {
				url = ClassLoader.getSystemResource(filename);
				if (url == null) {
					throw new Exception("Unable to find " + filename);
				}
				file = toFile(url);
			} else {
				file = toFile(url);
			}
			if (file == null || !file.exists()) {
				return null;
			}
			return new FileInputStream(file);
		}
	}



	public static void executeTask(Scheduler scheduler, String taskName) throws Exception {
		for (String groupName : scheduler.getJobGroupNames()) {
			for (JobKey k : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				String jobName = k.getName();
				String jobGroup = k.getGroup();
				System.out.println("JobName=" + jobName + ", JobGroup=" + jobGroup);
			}
		}
	}
	
	public static void printTaskAll(Scheduler scheduler) throws SchedulerException {
		for (String groupName : scheduler.getJobGroupNames()) {
			for (JobKey k : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				String jobName = k.getName();
				String jobGroup = k.getGroup();
				System.out.println("JobName=" + jobName + ", JobGroup=" + jobGroup);
			}
		}
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


	public static void main(String argv[]){


		ZonedDateTime ldt =  ZonedDateTime.of(LocalDateTime.now(ZoneId.systemDefault()).plus(java.time.Duration.ofMinutes(-10L)), ZoneId.systemDefault());
		System.out.println(" date plus min : " + Date.from(ldt.toInstant())) ;
	}
}
