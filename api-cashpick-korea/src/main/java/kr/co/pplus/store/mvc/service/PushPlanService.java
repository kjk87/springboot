package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.StoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
public class PushPlanService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(PushPlanService.class);

	@Autowired
	UserService userSvc;
	
	@Autowired
	PageService pageSvc;

	public List<PushPlan> getAllByStatus(SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("opt", opt) ;
		return sqlSession.selectList("PushPlan.getAllByStatus", map);
	}
	
	public List<User> getTargetAll(PushPlan plan) {
		List<User> userList = null;
		ParamMap map = new ParamMap() ;

		List<String> addressList = null;
		if (plan.getAllAddress() == false) {
			addressList = sqlSession.selectList("PushPlan.getAddressAllByPushPlan", plan.getNo());
		}
		
		
		if ("user".equals(plan.getType()) || "event".equals(plan.getType())) {
			if ("all".equals(plan.getTargetType())) {
				SearchOpt opt = new SearchOpt();
				opt.setFilter(new ArrayList<String>());
				opt.getFilter().add("normal");
				userList = userSvc.getUserAllByStatus(opt, Const.APP_TYPE_LUCKYBOL);
			} else if ("target".equals(plan.getTargetType())) {
				if (plan.getAllAddress() == false) {
					Map<Long, User> userMap = new HashMap<Long, User>();
					for (String address : addressList) {
						SearchOpt opt = new SearchOpt();
						opt.setSearch(address + "%");
						map.clear() ;
						map.put("plan", plan) ;
						map.put("opt", opt) ;
						List<User> tmpList = sqlSession.selectList("PushPlan.getMatchUserAllByPushPlan02", map) ;
						for (User user : tmpList) {
							userMap.put(user.getNo(), user);
						}
					}
					
					userList = new ArrayList<User>();
					for (Map.Entry<Long, User> entry : userMap.entrySet()) {
						userList.add(entry.getValue());
					}
				} else {
					map.clear() ;
					map.put("plan", plan) ;
					userList = sqlSession.selectList("PushPlan.getMatchUserAllByPushPlan01", map);
				}
			} else if ("user".equals(plan.getTargetType())) {
				map.clear() ;
				map.put("plan", plan) ;
				userList = sqlSession.selectList("PushPlan.getDirectUserAllByPushPlan", map);
			}
		} else if ("page".equals(plan.getType())) {
			if ("all".equals(plan.getTargetType())) {
				SearchOpt opt = new SearchOpt();
				opt.setFilter(new ArrayList<String>());
				opt.getFilter().add("normal");
				List<Page> pageList = pageSvc.getPageAllByStatus(opt);
				userList = new ArrayList<User>();
				for (Page page : pageList) {
					userList.add(page.getUser());
				}
			} else if ("target".equals(plan.getTargetType())) {
				if (plan.getAllAddress() == false) {
					Map<Long, User> userMap = new HashMap<Long, User>();
					for (String address : addressList) {
						SearchOpt opt = new SearchOpt();
						opt.setSearch(address + "%");
						map.clear() ;
						map.put("plan", plan) ;
						map.put("opt", opt) ;
						List<User> tmpList = sqlSession.selectList("PushPlan.getMatchPageUserAllByPushPlan02", map) ;
						for (User user : tmpList) {
							userMap.put(user.getNo(), user);
						}
					}
					
					userList = new ArrayList<User>();
					for (Map.Entry<Long, User> entry : userMap.entrySet()) {
						userList.add(entry.getValue());
					}
				} else {
					map.clear() ;
					map.put("plan", plan) ;
					userList = sqlSession.selectList("PushPlan.getMatchPageUserAllByPushPlan01", map);
				}
			} else if ("user".equals(plan.getTargetType())) {
				map.clear() ;
				map.put("plan", plan) ;
				userList = sqlSession.selectList("PushPlan.getDirectUserAllByPushPlan", map);
			}
		}
		return userList;
	}
	
	public Msg convert(PushPlan plan) throws ResultCodeException {
		List<User> targetList = getTargetAll(plan);
		return convert(plan, targetList);
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Msg convert(PushPlan plan, List<User> targetList) throws ResultCodeException {
		
		if (targetList != null && targetList.size() > 0) {
				
			Msg msg = new Msg();
			msg.setIncludeMe(false);
			msg.setReserved(false);
			msg.setStatus(msg.getReserved() == true ? "reserved" : "ready");
			msg.setPushCase(Const.PUSH_FORCED);
			msg.setPayType(Const.MSG_PAY_TYPE_NONE);
			msg.setTotalPrice(0L);
			msg.setInput("system");
			msg.setType("push");
			msg.setTargetCount(targetList.size());

			if(plan.getType().equals("user")){
				msg.setAppType(Const.APP_TYPE_LUCKYBOL);
			}else if(plan.getType().equals("event")){
				msg.setAppType(Const.APP_TYPE_LUCKYBOL);
			}else{
				msg.setAppType(Const.APP_TYPE_BIZ);
			}
			
			msg.setSubject(plan.getTitle());
			msg.setContents(plan.getContents());
			msg.setMoveType1(plan.getMoveType1());
			msg.setMoveType2(plan.getMoveType2());
			msg.setMoveTargetString(plan.getMoveTargetString());
			if (plan.getMoveTargetNo() != null)
				msg.setMoveTarget(new NoOnlyKey(plan.getMoveTargetNo()));
			msg.setAuthor(StoreUtil.getCommonAdmin());
			
			if (plan.getImage() != null) {
				if (msg.getProperties() == null)
					msg.setProperties(new HashMap<String, Object>());
				msg.getProperties().put("imagePath", plan.getImage().getUrl());
			}
			
			if ("target".equals(plan.getTargetType()) 
					&& (plan.getIos() == false || plan.getAos() == false)) {
				if (msg.getProperties() == null)
					msg.setProperties(new HashMap<String, Object>());
				if (plan.getAos() == false)
					msg.getProperties().put("iosOnly", true);
				else if (plan.getIos() == false)
					msg.getProperties().put("aosOnly", true);
			}
			
			int effected = sqlSession.insert("Msg.insertMsg", msg);
			if (effected > 0) {
				//List<MsgTarget> msgTargetList = new ArrayList<MsgTarget>();
				//Bulk로 수정할 부분

				PushTarget target = new PushTarget();
				target.setStatus("ready");
				ParamMap map = new ParamMap() ;
				map.put("msg", msg) ;
				map.put("target", target) ;
				map.put("list",targetList);
				sqlSession.insert("Msg.insertPushTargetUserList", map) ; //MGK msg, target);

				map.clear() ;
				map.put("list", targetList);
				map.put("msg", msg) ;
				map.put("type", msg.getAppType()) ;
				sqlSession.insert("Msg.insertMsgBoxUserList", map) ; //MGK user, msg, msg.getAppType());

//				for (User user : targetList) {
//					PushTarget target = new PushTarget();
//					target.setUser(user);
//					target.setStatus("ready");
//					ParamMap map = new ParamMap() ;
//					map.put("msg", msg) ;
//					map.put("target", target) ;
//					sqlSession.insert("Msg.insertPushTarget", map) ; //MGK msg, target);
//
//					map.clear() ;
//					map.put("user", user);
//					map.put("msg", msg) ;
//					map.put("type", msg.getAppType()) ;
//					sqlSession.insert("Msg.insertMsgBox", map) ; //MGK user, msg, msg.getAppType());
//					//msgTargetList.add(target);
//				}
				//msg.setTargetList(msgTargetList);
				plan.setStatus("complete");
				plan.setMsg(msg);
				sqlSession.update("PushPlan.updateMsgAndStatus", plan);
			}
			return msg;
		} else {
			logger.info("pushplan(" + plan.getNo() + ") - Target Not Found.");
			plan.setStatus("complete");
			sqlSession.update("PushPlan.updateMsgAndStatus", plan);
			return null;
		}
	}
}
