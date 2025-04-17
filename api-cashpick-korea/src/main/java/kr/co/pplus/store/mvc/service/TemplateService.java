package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.exception.NotFoundTargetException;
import kr.co.pplus.store.type.model.SysTemplate;
import kr.co.pplus.store.util.StoreUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
public class TemplateService extends RootService {
	
	public SysTemplate get(String code, Map<String, String> variableMap) throws NotFoundTargetException {
		SysTemplate template = sqlSession.selectOne("Template.get", code);
		if (template == null)
			throw new NotFoundTargetException();
		
		if (variableMap != null) {
			template.setContents(StoreUtil.applyVariable(template.getContents(), variableMap));
		}
		return template;
	}
}
