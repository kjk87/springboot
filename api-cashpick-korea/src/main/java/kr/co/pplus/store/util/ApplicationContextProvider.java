package kr.co.pplus.store.util;

import java.util.List;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
	public static ApplicationContext context;
	public static ObjectMapper objectMapper;

	
	public static ApplicationContext getApplicationContecxt() {
		return context;
	}
	
	public static <T> T getBean(Class<T> clz) {
		return ApplicationContextProvider.getApplicationContecxt().getBean(clz);
	}
	
	public static Object getBean(String beanName) {
		return ApplicationContextProvider.getApplicationContecxt().getBean(beanName);
	}
	
	public static Object getConfigValue(Object key) {
		Properties config = getBean(Properties.class);
		if (config == null)
			return null;
		
		return config.get(key);
	}
	
	public static String getConfigValue(String key) {
		Properties config = getBean(Properties.class);
		if (config == null)
			return null;
		
		return config.getProperty(key);
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		ApplicationContextProvider.context = context; 

	}

	public static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			RequestMappingHandlerAdapter a = getApplicationContecxt()
					.getBean(RequestMappingHandlerAdapter.class);
			List<HttpMessageConverter<?>> c = a.getMessageConverters();
			for (HttpMessageConverter<?> item : c) {
				if (item instanceof MappingJackson2HttpMessageConverter) {
					objectMapper = ((MappingJackson2HttpMessageConverter) item).getObjectMapper();
					break;
				}
			}
		}
		return objectMapper;
	}

}
