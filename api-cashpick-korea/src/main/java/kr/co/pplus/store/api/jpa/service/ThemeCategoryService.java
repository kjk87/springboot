package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.ThemeCategory;
import kr.co.pplus.store.api.jpa.repository.ThemeCategoryRepository;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class ThemeCategoryService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(ThemeCategoryService.class);

	@Autowired
	private ThemeCategoryRepository themeCategoryRepository;

	public List<ThemeCategory> getList(){
		return themeCategoryRepository.findAllByStatusOrderByArrayAsc("active");
	}
}
