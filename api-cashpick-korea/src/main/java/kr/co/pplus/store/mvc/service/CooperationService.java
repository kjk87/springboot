package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.Cooperation;
import kr.co.pplus.store.type.model.CooperationGroup;
import kr.co.pplus.store.type.model.Page;
import kr.co.pplus.store.type.model.SearchOpt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "transactionManager")
public class CooperationService extends RootService {
//	@Autowired
//	CooperationDao dao;
	
	public Cooperation get(Cooperation coop) {
		return sqlSession.selectOne("Cooperation.get", coop.getNo());
	}
	
	public Cooperation getByPage(Page page) {
		return sqlSession.selectOne("Cooperation.getByPage", page.getNo());
	}
	
	public List<CooperationGroup> getGroupAll() {
		return sqlSession.selectList("Cooperation.getGroupAll");
	}
	
	public int getCount(CooperationGroup group, SearchOpt opt) {
		if (group == null  || group.getNo() == null) {
			return sqlSession.selectOne("Cooperation.getCount", opt);
		} else {
			ParamMap map = new ParamMap() ;
			map.put("group", group) ;
			map.put("opt", opt) ;
			return sqlSession.selectOne("Cooperation.getCountByGroup", map) ;
		}
	}
	
	public List<Cooperation> getList(CooperationGroup group, SearchOpt opt) {
		if (group == null  || group.getNo() == null) {
			return sqlSession.selectList("Cooperation.getList", opt);
		} else {
			ParamMap map = new ParamMap() ;
			map.put("group", group) ;
			map.put("opt", opt) ;
			return sqlSession.selectList("Cooperation.getListByGroup", map) ;
		}
	}

}
