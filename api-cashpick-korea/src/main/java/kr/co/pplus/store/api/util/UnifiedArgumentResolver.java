package kr.co.pplus.store.api.util;

import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import kr.co.pplus.store.StoreApplication;
import kr.co.pplus.store.api.annotation.AgentSessionUser;
import kr.co.pplus.store.api.annotation.GuestSessionUser;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.UnknownException;
import kr.co.pplus.store.type.model.Agent;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.exception.SessionNotFoundException;
import kr.co.pplus.store.mvc.service.AuthService;
import kr.co.pplus.store.type.model.Session;

public class UnifiedArgumentResolver implements HandlerMethodArgumentResolver, ApplicationContextAware {

	protected static final Logger logger = LoggerFactory.getLogger(UnifiedArgumentResolver.class);

    @Autowired
	ApplicationContext context;
	
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		String sessionKey = ((HttpServletRequest)webRequest.getNativeRequest()).getHeader("sessionKey");
		if (sessionKey == null || sessionKey.isEmpty()) {
			Cookie[] cookies = ((HttpServletRequest) webRequest.getNativeRequest()).getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equalsIgnoreCase("pplus-sessionKey")) {
						sessionKey = cookie.getValue();
						break;
					}
				}
			}
			
			if (sessionKey == null || sessionKey.isEmpty())
				sessionKey = ((HttpServletRequest)webRequest.getNativeRequest()).getParameter("sessionKey");
			
		}
		
		Method method = parameter.getMethod();
		boolean possibleSkip = method.isAnnotationPresent(SkipSessionCheck.class);

		if (possibleSkip && StringUtils.isEmpty(sessionKey))
			return null;

		// 외부 업체 Agent API 연동시...
		boolean agentAccessToken = method.isAnnotationPresent(AgentSessionUser.class);
		// 비회원 인증
		boolean guestSessionUser = method.isAnnotationPresent(GuestSessionUser.class);
		if( agentAccessToken ) {

			try {

				AuthService authSvc = context.getBean(AuthService.class) ;
				String agentId = ((HttpServletRequest) webRequest.getNativeRequest()).getHeader("User-Agent") ;
				String timestamp = ((HttpServletRequest) webRequest.getNativeRequest()).getHeader("timestamp") ;
				System.out.println("Agent sessionKey : " + sessionKey) ;
				System.out.println("Agent id : " + agentId) ;
				System.out.println("Agent timestamp : " + timestamp) ;
				logger.info("Agent sessionKey : " + sessionKey) ;
				logger.info("Agent id : " + agentId) ;
				logger.info("Agent timestamp : " + timestamp) ;
				Enumeration<String> headerNames = ((HttpServletRequest) webRequest.getNativeRequest()).getHeaderNames() ;
				if (headerNames != null) {
					while (headerNames.hasMoreElements()) {
						String header = headerNames.nextElement() ;
						String value = ((HttpServletRequest) webRequest.getNativeRequest()).getHeader(header);
						System.out.println("Agent header:value - " + header + ":" + value) ;
						logger.info("Agent header:value - " + header + ":" + value) ;
					}
				}
				Agent agent = authSvc.getAgentSession(agentId, timestamp, sessionKey);
				return agent ;
			} catch (SessionNotFoundException ex) {
				logger.error(AppUtil.excetionToString(ex)) ;
				return null ;
			} catch (Exception ex) {
				if (ex instanceof ResultCodeException) {
					logger.error(AppUtil.excetionToString(ex));
					return ResultBuilder.build((ResultCodeException) ex);
				} else {
					logger.error(AppUtil.excetionToString(ex));
					throw ex;
				}
			}

		} else if( guestSessionUser ) {
			AuthService authSvc = context.getBean(AuthService.class) ;
			System.out.println("Guest sessionKey : " + sessionKey) ;
			logger.info("Guest sessionKey : " + sessionKey) ;
			Session session = authSvc.getGuestSession(sessionKey);
			return session ;
		} else {

			try {

				// 로칼 테스트 용
//				if( StoreApplication.SERVER_NAME.equals("LOCAL") ) {
//					return StoreUtil.getTestSession() ;
//				}

				logger.info("context : " + context.toString());
				AuthService authSvc = context.getBean(AuthService.class);
				logger.info("authSvc : " + authSvc);
				logger.info("sessionKey : " + sessionKey);
				Session session = authSvc.getSession(sessionKey);
				logger.debug("Session data : " + session) ;
				if(!session.getRestrictionStatus().equals("none")){
					throw new Exception("restrictionStatus is not none");
				}
				return session ;
			} catch (SessionNotFoundException ex) {
				if (!possibleSkip) {
					logger.error(AppUtil.excetionToString(ex));
					return null;
				}
			} catch (Exception ex) {
				if (ex instanceof ResultCodeException) {
					logger.error(AppUtil.excetionToString(ex));
					return ex ;
				} else {
					logger.error(AppUtil.excetionToString(ex));
					throw new UnknownException("UnknownException : " , "UnifiedArgumentResolver Error : " + ex.getMessage());
				}
			}
		}
		return null;
	}

	@Override
	public boolean supportsParameter(MethodParameter arg) {
		if (Session.class.isAssignableFrom(arg.getParameterType()))
			return true;
		else if(Agent.class.isAssignableFrom(arg.getParameterType()))
			return true;
		else
			return false;
	}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext ;
    }
}
