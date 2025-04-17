package kr.co.pplus.store.api.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.co.pplus.store.exception.SessionNotFoundException;
import kr.co.pplus.store.util.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggingFilter implements Filter {

	protected static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
	private static final String CRLF = "\n";
	private static final String REQUEST_PREFIX = "Request: ";
	private static final String RESPONSE_PREFIX = "Response: ";
	private AtomicLong id = new AtomicLong(1);
	

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String reqIdStr = KeyGenerator.generateOrderNo() ;
		long requestId = id.incrementAndGet() ;
		long startTime = System.nanoTime();

		HttpServletRequest req = new RequestWrapper(requestId, (HttpServletRequest)request);
		String reqUri = req.getRequestURI();

		if(reqUri.contains("/store/v3/api-docs")){
			ByteResponseWrapper byteResponseWrapper = new ByteResponseWrapper((HttpServletResponse) response);
			chain.doFilter(req, byteResponseWrapper);

			String jsonResponse = new String(byteResponseWrapper.getBytes(), response.getCharacterEncoding());
			response.getOutputStream().write((new com.google.gson.JsonParser().parse(jsonResponse).getAsString())
					.getBytes(response.getCharacterEncoding()));

		}else{

			HttpServletResponse res = new ResponseWrapper(requestId, (HttpServletResponse)response);

			boolean exception = false;

			try {
				final String url = ((HttpServletRequest) req).getRequestURI();
				if(url.matches("/(health|.+\\.(ico|js))")) {
					req.setAttribute("ignoreLogging", true);
				}

				//logger.debug( "###request### : " + req.getRequestURI() + req.getAttribute("systemBaseUrl")) ;
//				StringBuilder msg = new StringBuilder();
//				msg.append(CRLF).append("################################").append(CRLF).append(REQUEST_PREFIX).append(((HttpServletRequest) request).getRequestURI()).append(":").append(requestId);
//				logger.debug(msg.toString()) ;

				chain.doFilter(req, res) ;


			}
			catch (ServletException ex) {
				exception = true;
				ex.printStackTrace();
				throw ex;
			} catch (IOException ex) {
				exception = true;
				ex.printStackTrace();
				throw ex;
			} catch (Exception ex) {
				exception = true;
				ex.printStackTrace();
				throw ex;
			}
			finally {
				if (logger.isDebugEnabled()) {
					logging(startTime, reqIdStr, exception, req, (ResponseWrapper)res);
				}
			}
		}



		
	}
	
	private boolean isHtml(HttpServletRequest request) {
		try {
			String uri = request.getRequestURI().toLowerCase();
			if (uri.indexOf(".ftl") > 0 || uri.indexOf(".html") > 0
					|| uri.indexOf(".js") > 0 || uri.indexOf(".htm") > 0
					|| uri.indexOf("/jsp") > 0 || uri.indexOf(".css") > 0)
				return true;
			else
				return false;
		} catch (Exception ex) {
			return false;
		}
	}

	private boolean isMultipart(HttpServletRequest request) {
		return request.getContentType() != null
				&& request.getContentType().startsWith("multipart/form-data");
	}

	private boolean isFormSubmit(HttpServletRequest request) {
		return request.getContentType() != null
				&& request.getContentType().startsWith(
						"application/x-www-form-urlencoded");
	}
	
	private String getSessionKey(HttpServletRequest request) {
		String sessionKey = request.getHeader("sessionKey");
		if (sessionKey == null || sessionKey.isEmpty()) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equalsIgnoreCase("push-sessionKey")) {
						sessionKey = cookie.getValue();
						break;
					}
				}
			}
			
			if (sessionKey == null || sessionKey.isEmpty())
				sessionKey = request.getParameter("sessionKey");
		}
		
		return sessionKey;
	}

	private void logging(long startTime, String requestId, boolean exception, HttpServletRequest request, ResponseWrapper response) {
		boolean logging = true;
		boolean incRes = false;
		
		if (!isHtml(request))
			incRes = true;
		
		
		if (isMultipart(request)) {
			StringBuilder tm = new StringBuilder();
			Enumeration en = request.getHeaderNames();
			tm.append(CRLF).append("Request Header").append(CRLF);
			while (en.hasMoreElements()) {
				String hn = (String)en.nextElement();
				String hv = request.getHeader(hn);
				if (hn != null && hv != null)
					tm.append(hn).append(": ").append(hv).append(CRLF);
			}
			
			logger.debug(tm.toString());
		}

		if (logging && !(request.getRequestURI().equals("/store/api/tool/returnTest"))) {
			StringBuilder msg = new StringBuilder();
			String reqUri = request.getRequestURI();
			if (reqUri.contains("Payment") || reqUri.contains("INIpay") || reqUri.contains("payment"))
				incRes = true;
			
			if (!exception) {
				/*
				msg.append(CRLF).append("################################")
						.append(CRLF).append(REQUEST_PREFIX)
					.append(CRLF).append(REQUEST_PREFIX)
					.append(reqUri).append(":").append(requestId);
					*/
			} else {
				msg.append(CRLF).append("###Exception####################")
				.append(CRLF).append(REQUEST_PREFIX)
				.append(reqUri).append(":").append(requestId);
			}
			
			
			if (request.getQueryString() != null
					&& !request.getQueryString().isEmpty())
				msg.append('?').append(request.getQueryString());
			msg.append(" ").append(request.getMethod());
			HttpSession session = request.getSession(false);
			if (session != null) {
				msg.append(CRLF).append("SESSION ID: ").append(session.getId());
			}
			
			String sessionKey = getSessionKey(request);
			if (sessionKey != null && sessionKey.length() > 0) {
				msg.append(CRLF).append("SessionKey: ").append(sessionKey);
				System.out.println("sessionKey : " + sessionKey);
			}
			
			/*msg.append(CRLF).append("Remote-IP: ")
					.append(ThcUtil.getClientIP(request));*/

			msg.append(CRLF).append("Content-Type: ")
					.append(request.getContentType());
			if (request instanceof RequestWrapper) {
				msg.append(CRLF).append("Transaction ID: ")
						.append(((RequestWrapper) request).getId());
			}


			if (request instanceof RequestWrapper && !isMultipart(request)) {
				RequestWrapper requestWrapper = (RequestWrapper) request;
				msg.append(CRLF).append("Request Body: ").append(reqUri).append(":").append(requestId).append(CRLF);
				if (isFormSubmit(request)) {
					boolean firstParam = true;
					for (@SuppressWarnings("rawtypes")
					Enumeration e = request.getParameterNames(); e
							.hasMoreElements();) {
						String pn = (String) e.nextElement();
						String pv[] = request.getParameterValues(pn);
						for (int l = 0; l < pv.length; l++) {
							if (firstParam) {
								msg.append(pn + "=" + pv[l]);
								firstParam = false;
							} else {
								msg.append("&").append(pn).append("=")
										.append(pv[l]);
							}
						}

					}
				}
				else {
					msg.append(new String(requestWrapper.toByteArray()));
				}
			}

			msg.append(CRLF).append("Elapsed Time: ").append(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)).append(" msec");

			if( reqUri.indexOf("/attachment/image")>=0 || reqUri.indexOf("/console/font")>=0 ) {

				incRes = false;
			}
			
			if (!exception && incRes) {
				msg.append(CRLF).append("-------------------------------")
					.append(CRLF).append(RESPONSE_PREFIX);
				msg.append(CRLF).append(new String(response.toByteArray()));
			}
			
			msg.append(CRLF).append("################################").append(requestId);
			
			if (exception) {
				logger.error(msg.toString().replaceAll("\\t","\t").replaceAll("\\n","\n"));
			} else {
				logger.debug(msg.length() > 20480 ? msg.substring(0, 20480)
					+ "..... (more data. len:" + msg.length() + ")" : msg
					.toString().replaceAll("\\t","\t").replaceAll("\\n","\n"));
			}
		}		
	}
}
