package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.Article;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "transactionManager")
public class FeedService extends RootService {

	public int getFeedCount(User user) {
		return sqlSession.selectOne("Feed.getFeedCount", user);
		
	}
	
	public List<Article> getFeedList(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Feed.getFeedList", map) ;
	}
}
