package kr.co.pplus.store.api.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SecurityInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

	private static String PAGE_URL = "pageUrl";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {


		String uri = request.getRequestURI();
		HttpSession session = request.getSession();
		

		try {
			if (request.getMethod().toUpperCase().equals("OPTIONS")) {

				if ( uri.startsWith("/store/") || uri.startsWith("/api/") ) {
					response.reset();
					response.setHeader("Access-Control-Allow-Origin", "*");
					response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT");
					response.setHeader("Access-Control-Max-Age", "3600");
					response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, sessionKey, Cache-Control, Content-Type, Accept, Authorization");
					response.setContentType("application/json; charset=utf-8");
					response.setCharacterEncoding("utf-8");
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().write("{\"success\":true}");
					response.getWriter().flush();
					return true;
				} else {
					response.reset();
					response.setHeader("Access-Control-Allow-Origin", "*");
					response.setHeader("Access-Control-Allow-Methods", "");
					response.setHeader("Access-Control-Max-Age", "10");
					response.setContentType("application/json; charset=utf-8");
					response.setCharacterEncoding("utf-8");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.getWriter().write("{\"success\":false}");
					response.getWriter().flush();
					return false;
				}
			}
			else  {
				if ( uri.startsWith("/store/") || uri.startsWith("/api/") ) {
					response.setHeader("Access-Control-Allow-Origin", "*");
					response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT");
					response.setHeader("Access-Control-Max-Age", "3600");
					response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, sessionKey, Cache-Control, Content-Type, Accept, Authorization");
					return true;
				}
				else {
					response.reset();
					response.setHeader("Access-Control-Allow-Origin", "*");
					response.setHeader("Access-Control-Allow-Methods", "");
					response.setHeader("Access-Control-Max-Age", "10");
					response.setContentType("application/json; charset=utf-8");
					response.setCharacterEncoding("utf-8");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.getWriter().write("{\"success\":false}");
					response.getWriter().flush();
					return false;
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			response.reset();
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Methods", "");
			response.setHeader("Access-Control-Max-Age", "10");
			response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
			response.setContentType("application/json; charset=utf-8");
			response.setCharacterEncoding("utf-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write(e.getMessage()) ;
			response.getWriter().flush();
			return false ;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
