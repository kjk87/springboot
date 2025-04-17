package kr.co.pplus.store.api.aop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.co.pplus.store.StoreApplication;
import kr.co.pplus.store.api.annotation.AgentSessionUser;
import kr.co.pplus.store.api.annotation.DenyGuest;
import kr.co.pplus.store.api.annotation.GuestSessionUser;
import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.jpa.model.RequestLog;
import kr.co.pplus.store.api.jpa.repository.RequestLogRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.GuestDenyException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.SessionNotFoundException;
import kr.co.pplus.store.exception.UnknownException;
import kr.co.pplus.store.type.model.Agent;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.util.DefaultObjectMapper;
import kr.co.pplus.store.util.KeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

@Aspect
@Configuration
public class ControllerProxy {
	private Logger logger = LoggerFactory.getLogger(ControllerProxy.class);

	@Autowired
	ApplicationContext context;


	RequestLogRepository exceptionLogRepository = null ;


	protected Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().create();



	@Around("execution(* kr.co.pplus.store.api.controller.*.*(..)) || execution(* kr.co.pplus.store.api.jpa.controller.*.*(..))")
	public Object around(ProceedingJoinPoint call) throws Throwable {
		Object returnValue = null;

		MethodSignature sig = (MethodSignature) call.getSignature();
		Method method = sig.getMethod();

		Object[] args = call.getArgs();

//		logger.debug("around AOP in : " + args) ;
//		System.out.println("around AOP in : " + args) ;

		String requestId = KeyGenerator.generateOrderNo() ;
		MDC.put("requestId", "--:" + KeyGenerator.generateOrderNo() + ":--") ;


		Long sessionMemberSeqNo = null ;

		if ( !method.isAnnotationPresent(SkipSessionCheck.class) &&
				!method.isAnnotationPresent(AgentSessionUser.class) &&
				!method.isAnnotationPresent(GuestSessionUser.class) ) {
			Type[] genericParameterTypes = method.getGenericParameterTypes();
			Class<?> returnType = method.getReturnType();
			int i = 0;
			boolean existSession = false ;
			for (Type genericParameterType : genericParameterTypes) {
				if (genericParameterType instanceof ParameterizedType) {
					continue;
				} else {
					Class cls = (Class) genericParameterType;
					if (Session.class.isAssignableFrom(cls)) {
						Session session = (Session) args[i];

						if (session == null) {


							if (returnType.isAssignableFrom(ModelAndView.class)) {

								ModelAndView mv = new ModelAndView(
										"uploadResult");
								mv.addObject("json", new DefaultObjectMapper()
										.writeValueAsString(new SessionNotFoundException()));
								return mv;
							} else {
								SessionNotFoundException sne = new SessionNotFoundException();
								sne.put("requestId", requestId);
								return sne;
							}
						} else {

							sessionMemberSeqNo = session.getNo() ;
							existSession = true ;

							logger.debug("session : " + session.toString()) ;
							logger.debug("sessionKey : " +  session.getSessionKey()) ;


							boolean denyGuest = method
									.isAnnotationPresent(DenyGuest.class);
							if (denyGuest && AppUtil.isGuest(session)) {
								if (returnType
										.isAssignableFrom(ModelAndView.class)) {
									ModelAndView mv = new ModelAndView(
											"uploadResult");
									mv.addObject("json", new DefaultObjectMapper()
											.writeValueAsString(new GuestDenyException()));
									return mv;
								} else {

									GuestDenyException gde = new GuestDenyException();
									gde.put("requestId", requestId);
									return gde ;
								}
							}
						}
					}
				}
				i++;
			}

//			if( !existSession && StoreApplication.SERVER_NAME.equals("LOCAL") ) {
//				existSession = true ;
//			}

			if( !existSession ) {
				SessionNotFoundException sne = new SessionNotFoundException("AOP ERROR", "Session Not Found Exception") ;
				sne.put("requestId", requestId);
				return sne ;
			}
		} else if( method.isAnnotationPresent(AgentSessionUser.class) ) {
			Type[] genericParameterTypes = method.getGenericParameterTypes();
			Class<?> returnType = method.getReturnType();
			int i = 0;
			boolean existAgentAccessToken = false ;
			for (Type genericParameterType : genericParameterTypes) {
				if (genericParameterType instanceof ParameterizedType) {
					continue;
				} else {
					Class cls = (Class) genericParameterType;
					if (Agent.class.isAssignableFrom(cls)) {

						Agent posAgent = (Agent) args[i];
						if (posAgent != null) {
							existAgentAccessToken = true;
							sessionMemberSeqNo = posAgent.getNo() ;
							break ;
						}
					}
				}
			}

			if( !existAgentAccessToken ) {
				SessionNotFoundException sne = new SessionNotFoundException("AOP ERROR", "Agent SessionKey Not Found");
				sne.put("requestId", requestId);
				return sne ;
			}
		} else if( method.isAnnotationPresent(GuestSessionUser.class) ) {
			Type[] genericParameterTypes = method.getGenericParameterTypes();
			Class<?> returnType = method.getReturnType();
			int i = 0;
			boolean existGuestSessionUser = false ;
			for (Type genericParameterType : genericParameterTypes) {
				if (genericParameterType instanceof ParameterizedType) {
					continue;
				} else {
					Class cls = (Class) genericParameterType;
					if (Session.class.isAssignableFrom(cls)) {

						Session session = (Session) args[i];
						if (session != null) {
							existGuestSessionUser = true;
							sessionMemberSeqNo = session.getNo() ;
							break ;
						}
					}
				}
			}

			if( !existGuestSessionUser ) {
				SessionNotFoundException sne = new SessionNotFoundException("AOP ERROR", "GuestSessionUser SessionKey Not Found");
				sne.put("requestId", requestId);
				return sne ;
			}
		}

		try {
			returnValue = call.proceed(args);

			if( returnValue instanceof  Map<?,?> ) {
				((Map<String, Object>) returnValue).put("requestId", requestId);
			}

		} catch (ResultCodeException ex) {
			String errMsg = AppUtil.excetionToString(ex).replaceAll("\\t","    ").replaceAll("\\n","\n") ;
			logger.error("proceed error return", errMsg);
			ex.put("requestId", requestId);
			if( StoreApplication.SERVER_NAME.equals("DEV") ) {
				try {
					HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
					HttpServletResponse res = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
					exceptionLogRepository = context.getBean(RequestLogRepository.class);
					RequestLog exlog = new RequestLog();
					exlog.setMemberSeqNo(sessionMemberSeqNo);
					exlog.setIsException(true);
					exlog.setResponse(errMsg.length() > 8192 ? errMsg.substring(0, 8192) : errMsg);
					exlog.setRequestId(requestId);
					exlog.setUri(req.getRequestURI());
					exlog.setRegDatetime(AppUtil.localDatetimeNowString());
					exlog.setResultCoce((Integer) ex.get("resultCode"));
					exlog.setHttpStatus(res.getStatus());
					exlog.setIp(req.getRemoteAddr());
					exlog.setServer(StoreApplication.SERVER_NAME);
					exceptionLogRepository.saveAndFlush(exlog);
				} catch (Exception e) {
				}
			}
			return ex ;
		} catch (Exception ex) {

			UnknownException uex = new UnknownException("proceed fail", ex);
			if( !method.isAnnotationPresent(SkipSessionCheck.class) &&
					!method.isAnnotationPresent(AgentSessionUser.class) &&
					!method.isAnnotationPresent(GuestSessionUser.class) ) {
				uex.put("stackTrace", AppUtil.excetionToString(ex)) ;
			}
			uex.put("requestId", requestId) ;
			String errMsg = AppUtil.excetionToString(uex).replaceAll("\\t","    ").replaceAll("\\n","\n") ;
			logger.error("proceed error return 2", errMsg) ;

			if( StoreApplication.SERVER_NAME.equals("DEV") ) {
				try {
					HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
					HttpServletResponse res = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
					exceptionLogRepository = context.getBean(RequestLogRepository.class);
					RequestLog exlog = new RequestLog();
					exlog.setMemberSeqNo(sessionMemberSeqNo);
					exlog.setIsException(true);
					exlog.setResponse(errMsg.length() > 8192 ? errMsg.substring(0, 8192) : errMsg);
					exlog.setRequestId(requestId);
					exlog.setUri(req.getRequestURI());
					exlog.setRegDatetime(AppUtil.localDatetimeNowString());
					exlog.setResultCoce((Integer) uex.get("resultCode"));
					exlog.setHttpStatus(res.getStatus());
					exlog.setIp(req.getRemoteAddr());
					exlog.setServer(StoreApplication.SERVER_NAME);
					exceptionLogRepository.saveAndFlush(exlog);
				} catch (Exception e) {
				}
			}

			return uex ;
		}

		/*
		if( StoreApplication.SERVER_NAME.equals("DEV") ) {
			try { // 정상 리턴인 경우
				HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
				HttpServletResponse res = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
				exceptionLogRepository = context.getBean(RequestLogRepository.class);
				RequestLog requestLog = new RequestLog();
				requestLog.setMemberSeqNo(sessionMemberSeqNo);
				requestLog.setIsException(false);
				requestLog.setResponse("success");
				requestLog.setRequestId(requestId);
				requestLog.setUri(req.getRequestURI());
				requestLog.setRegDatetime(AppUtil.localDatetimeNowString());
				requestLog.setResultCoce(200);
				requestLog.setHttpStatus(res.getStatus());
				requestLog.setIp(req.getRemoteAddr());
				requestLog.setServer(StoreApplication.SERVER_NAME);
				exceptionLogRepository.saveAndFlush(requestLog);
			} catch (Exception e) {
			}
		}
		 */

		return returnValue;
	}
}
