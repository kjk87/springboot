package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.exception.AlreadyExistsException;
import kr.co.pplus.store.exception.AlreadyJoinException;
import kr.co.pplus.store.exception.AlreadyLimitException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.StoreUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(transactionManager = "transactionManager")
public class PromotionService extends RootService {

	public List<Promotion> getList(SearchOpt opt) {
		return sqlSession.selectList("Promotion.getList", opt);
	}
	
	public Promotion getByNumber(String number) {
		return sqlSession.selectOne("Promotion.getByNumber", number);
	}
	
	public Promotion get(Long no) {
		return sqlSession.selectOne("Promotion.get", no);
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public JoinPromotion join(User user, Promotion promotion) throws ResultCodeException {

		ParamMap map = new ParamMap() ;
		//우선 참여 가능한지 확인해야 한다.
		Promotion saved = sqlSession.selectOne("Promotion.get", promotion.getNo());
		if ("once".equals(saved.getLimitType())) {
			map.put("user", user) ;
			map.put("duration", saved.getDuration()) ;
			int joinCount = sqlSession.selectOne("Promotion.getUserJoinCountByDuration", map) ; //MGK  user, saved.getDuration());
			if (joinCount > 0)
				throw new AlreadyJoinException("join", "" + joinCount);
		} else if ("total".equals(saved.getLimitType())) {
			int joinCount = sqlSession.selectOne("Promotion.getJoinCountByDuration",saved.getDuration());
			if (joinCount >= saved.getLimitCount())
				throw new AlreadyLimitException();
		} else if ("daily".equals(saved.getLimitType()) 
				|| "weekly".equals(saved.getLimitType()) 
				|| "monthly".equals(saved.getLimitType())) {
			Date lastJoin = sqlSession.selectOne("Promotion.getLastJoinDate", user);
			if (lastJoin != null) {
				Date now = DateUtil.getCurrentDate();
				
				if ("daily".equals(saved.getLimitType()) && lastJoin.getYear() == now.getYear()
						&& lastJoin.getMonth() == now.getMonth()
						&& lastJoin.getDay() == now.getDay()) {
					throw new AlreadyExistsException();
				} else if ("weekly".equals(saved.getLimitType()) 
						&& lastJoin.getYear() == now.getYear()
						&& DateUtil.getWeekOfYear(lastJoin) == DateUtil.getWeekOfYear(now)
						) {
					throw new AlreadyExistsException();
				} else if ("monthly".equals(saved.getLimitType())
						&& lastJoin.getYear() == now.getYear()
						&& lastJoin.getMonth() == now.getMonth()) {
					throw new AlreadyExistsException();
				}
			}
		}
		
		
		JoinPromotion join = new JoinPromotion();
		join.setPromotion(saved);
		join.setUser(user);
		
		
		if ("lots".equals(saved.getType())) {
			//
			join.setJoinResult("lose");
			//당청 로직 돌린다.
			
			for (PromotionLotsConfig lots : saved.getLotsConfigList()) {
				
				if (lots.getLimitCount() == lots.getWinCount()) {
					join.setJoinResult("lose");
					break;
				}
				
				if (lots(lots.getProbability())) {
					join.setJoinResult("win");
					join.setLots(lots);
					break;
				}
			}
			
			join.setStatus("complete");
		} else if ("join".equals(saved.getType())) {
			join.setJoinResult("join");
			join.setStatus("complete");
		} else if ("manual".equals(saved.getType())) {
			join.setJoinResult("pending");
			join.setStatus("pending");
		} else {
			return join;
		}
		
		//insert join
		sqlSession.insert("Promotion.insertJoin", join);
		
		//lots increase count
		if ("win".equals(join.getJoinResult()) && join.getLots() != null)
			sqlSession.update("Promotion.win", join.getLots());
			
		return join;
	}
	
	private boolean lots(int probability) {
		return StoreUtil.lots(100, probability);
	}
}
