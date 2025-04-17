package kr.co.pplus.store.mvc.service;

import java.util.Date;
import java.util.List;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ParamMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import kr.co.pplus.store.type.model.Duration;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.type.model.UserRanking;
import kr.co.pplus.store.util.DateUtil;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "transactionManager")
public class RankingService extends RootService {


	public List<UserRanking> inviteView(Duration duration) {
		try {
			List<UserRanking> userList = sqlSession.selectList("Ranking.inviteView",duration);
			int i = 0;
			for (UserRanking user : userList) {
				try {
					user.setRanking(i+1);
					i++;
			 	} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			return userList;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<UserRanking> invite(Duration duration) {
		try {
			if (duration.getStart() == null)
				duration.setStart("2017-01-01 00:00:00");
			
			if (duration.getEnd() == null)
				duration.setEnd(DateUtil.getDateString("yyyy-MM-dd HH:mm:ss", new Date()));
			
			return sqlSession.selectList("Ranking.invite",duration);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public UserRanking userInvite(User user, Duration duration) {
		try {
			if (duration.getStart() == null)
				duration.setStart("2017-01-01 00:00:00");
			
			if (duration.getEnd() == null)
				duration.setEnd(DateUtil.getDateString("yyyy-MM-dd HH:mm:ss", new Date()));

			ParamMap map = new ParamMap() ;
			map.put("user", user) ;
			map.put("duration", duration) ;
			return sqlSession.selectOne("Ranking.userInvite", map) ;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public List<UserRanking> reward(String appType, Duration duration) {
		try {
			if (duration.getStart() == null)
				duration.setStart("2017-01-01 00:00:00");
			
			if (duration.getEnd() == null)
				duration.setEnd(DateUtil.getDateString("yyyy-MM-dd HH:mm:ss", new Date()));

			if(StringUtils.isEmpty(appType)){
				appType = "pplus";
			}

			ParamMap map = new ParamMap() ;
			map.put("appType", appType) ;
			map.put("duration", duration) ;

			return sqlSession.selectList("Ranking.reward",map);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
		
	}


	public UserRanking userReward(User user, Duration duration) {
		try {
			if (duration.getStart() == null)
				duration.setStart("2017-01-01 00:00:00");
			
			if (duration.getEnd() == null)
				duration.setEnd(DateUtil.getDateString("yyyy-MM-dd HH:mm:ss", new Date()));

			ParamMap map = new ParamMap() ;
			map.put("user", user) ;
			map.put("duration", duration) ;
			return sqlSession.selectOne("Ranking.userReward", map) ;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
		
	}

	public int updateWeekRanking() {
		try {
			 sqlSession.update("Ranking.truncateWeekView");
			 sqlSession.insert("Ranking.inesrtWeekView");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return Const.E_SUCCESS;
	}

	public int updateAllRanking() {
		try {
			sqlSession.update("Ranking.truncateAllView");
			sqlSession.insert("Ranking.inesrtAllView");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return Const.E_SUCCESS;
	}



}
