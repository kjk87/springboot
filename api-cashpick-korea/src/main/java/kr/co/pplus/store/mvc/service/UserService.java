package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.JoinUser;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.FTLinkPayApi;
import kr.co.pplus.store.util.RedisUtil;
import kr.co.pplus.store.util.SecureUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
public class UserService extends RootService {

	private final static Logger logger = LoggerFactory.getLogger(UserService.class);

	@Value("${STORE.REDIS_PREFIX}")
	static String REDIS_PREFIX = "pplus-";

//	@Autowired
//	UserDao dao;
	
	@Autowired
	ContactService contactSvc;
	
	@Autowired
	VerificationService verificationSvc;

	@Autowired
	PlusService plusService;

	@Autowired
	CashBolService cashBolSvc;

	@Autowired
	CommonService commonService;
	
	private User GUEST = null;
	
	public User getGuest() {
		if (GUEST == null)
			GUEST = getUser(Const.GUEST_USER_NO);
		return GUEST;
	}
	
	public User getUserByLoginId(String loginId) {
		return sqlSession.selectOne("User.getUserByLoginId", loginId);
	}
	
	public User getUser(Long userNo) {
		return sqlSession.selectOne("User.getUser", userNo);
	}
	
	public User getUserByMobile(String mobile) {
		return sqlSession.selectOne("User.getUserByMobile", mobile);
	}
	
	public User getUserByEmail(String email) {
		return sqlSession.selectOne("User.getUserByEmail", email);
	}

	public List<User> getUserMobileNumberList() {
		List<User> list = sqlSession.selectList("User.getUserMobileNumberList");
		List<User> result = new ArrayList<>();
		for(User user : list){
			user.setVerification(null);
			if(!user.getMobile().contains("luckyball##")){
				User saved = sqlSession.selectOne("User.getUserByMobile", "luckyball##"+user.getMobile());
				if(saved == null){
					result.add(user);
				}

			}
		}
		return result;
	}

	public void updatetMobileLuckyball(){
		List<User> list = getUserMobileNumberList();
		for(User user : list){
			user.setMobile("luckyball##"+user.getMobile());
		}
		ParamMap map = new ParamMap() ;
		map.put("list", list) ;
		sqlSession.update("User.updatetMobileLuckyball", map);
	}

	public User getUserByNickname(String nickname, String appType) {
		ParamMap map = new ParamMap() ;
		map.put("nickname", nickname) ;
		map.put("appType", appType) ;
		return sqlSession.selectOne("User.getUserByNickname", nickname);
	}

	public int updateSnsUser(String snsId, String loginId, String password) {
		ParamMap map = new ParamMap() ;
		map.put("snsId", snsId) ;
		map.put("loginId", loginId) ;
		map.put("password", SecureUtil.encryptPassword(loginId,password)) ;
		return sqlSession.update("User.updateSnsUser", map) ;
	}

	public int increaseRecommendCount(Long memberSeqNo) {
		return sqlSession.update("User.increaseRecommendCount", memberSeqNo) ;
	}

	public int insertInviteReward(InviteReward inviteReward){
		return sqlSession.insert("User.insertInviteReward", inviteReward);
	}

	public int updateUseStatusWithAgreeTerms(JoinUser user) {

		sqlSession.delete("Auth.deleteAgreeTerms", user.getNo());
		if (user.getTermsList() != null && user.getTermsList().size() > 0) {
			ParamMap params = new ParamMap("userNo", user.getNo());
			for (Terms terms : user.getTermsList()) {
				params.put("termsNo", terms.getNo());

				sqlSession.insert("Auth.agreeTerms", params);
			}
		}
		user.setPassword(SecureUtil.encryptPassword(user.getLoginId(), user.getPassword())) ;
		return sqlSession.update("User.updateUseStatusAndIdPass", user) ;
	}

	public User getUserByRecommendKey(String recommendKey) {
		return sqlSession.selectOne("User.getUserByRecommendKey", recommendKey);
	}
	
	public int getUserCountByRecommendationCode(String recommendationCode) {
		return sqlSession.selectOne("User.getUserCountByRecommendationCode", recommendationCode);
	}
	
	public List<User> getUserListByRecommendationCode(String recommendationCode) {
		return sqlSession.selectList("User.getUserListByRecommendationCode", recommendationCode);
	}
	
	public boolean existsUser(Long no) {
		return ((Integer)sqlSession.selectOne("User.existsUser", no) > 0) ? true : false;
	}
	
	public boolean existsUserByLoginId(String loginId) {
		return (((Integer)sqlSession.selectOne("User.existsUserByLoginId", loginId)) > 0) ? true : false;
	}

	public boolean existsUserByMobile(String mobile) {
		return ((Integer)sqlSession.selectOne("User.existsUserByMobile", mobile) > 0) ? true : false;
	}
	
	public boolean existsUserByEmail(String email) {
		return ((Integer)sqlSession.selectOne("User.existsUserByEmail", email) > 0) ? true : false;
	}
	
	public boolean existsUserByNickname(String nickname, String appType) {
		ParamMap map =  new ParamMap() ;
		map.put("nickname", nickname) ;
		map.put("appType", appType) ;
		return ((Integer)sqlSession.selectOne("User.existsUserByNickname", map) > 0) ? true : false;
	}
	
	public boolean existsUesrByRecommendKey(String recommendKey) {
		return ((Integer)sqlSession.selectOne("User.existsUserByRecommendKey", recommendKey) > 0) ? true : false;
	}

	public User selectVirtualRandomUser() {
		return sqlSession.selectOne("User.selectVirtualRandomUser") ;
	}
	
	public int delete(User user) {
		int effected = sqlSession.delete("User.delete", user);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	public int update(User user) {

		if(StringUtils.isEmpty(user.getAppType())){
			user.setAppType("pplus");
		}

		if(!user.getAppType().equals("pplus") && !user.getMobile().startsWith(user.getAppType() + "##")){
			user.setMobile(user.getAppType() + "##" + user.getMobile());
		}

		user.setVerificationMedia(user.getVerification().getMedia());
		int effected = sqlSession.update("User.update", user);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateExternal(User user) {

		if(StringUtils.isEmpty(user.getAppType())){
			user.setAppType("pplus");
		}

		if(!user.getAppType().equals("pplus") && !user.getMobile().startsWith(user.getAppType() + "##")){
			user.setMobile(user.getAppType() + "##" + user.getMobile());
		}

		user.setVerificationMedia(user.getVerification().getMedia());
		int effected = sqlSession.update("User.updateExternal", user);


		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	public int updateUseStatus(User user) {
		int effected = sqlSession.update("User.updateUseStatus", user);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Integer updateLottoDefaultTicketCount(Long userNo, Long ticketNum) throws ResultCodeException {
		try {
			ParamMap map =  new ParamMap() ;
			map.put("userNo", userNo) ;
			map.put("ticketNum", ticketNum) ;
			return sqlSession.update("User.updateLottoDefaultTicketCount", map);
		} catch(Exception e) {
			throw new UnknownException(e.getMessage()) ;
		}
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Integer updateLottoTicketCount(Long userNo, Long ticketNum) throws ResultCodeException {
		try {
			ParamMap map =  new ParamMap() ;
			map.put("userNo", userNo) ;
			map.put("ticketNum", ticketNum) ;
			return sqlSession.update("User.updateLottoTicketCount", map);
		} catch(Exception e) {
			throw new UnknownException(e.getMessage()) ;
		}
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Integer updateAllUserLottoDefaultTicket() throws ResultCodeException {
		try {
			return sqlSession.update("User.updateAllUserLottoDefaultTicket");
		} catch(Exception e) {
			throw new UnknownException(e.getMessage()) ;
		}
	}


	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateProfileImage(User user, Attachment attachment) {
		user.setProfileImage(attachment);
		//TODO. 기존 프로필 이미지 삭제 처리에 대해서 고민할 것
		int effected = sqlSession.update("User.updateProfileImage", user);
		if (effected > 0) {
			contactSvc.updateVersionByMobile(user);
		}
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	public List<UserApp> getUserApp(User user) {
		return sqlSession.selectList("User.getUserApp", user);
	}
	
	public boolean existsUserAuthedNumber(@Param("user") User user, @Param("mobile") String mobile) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("mobile", mobile) ;
		int exists = (Integer)sqlSession.selectOne("User.existsUserAuthedNumber", map) ; //MGK user, mobile);
		return exists > 0 ? true : false;
	}
	
	public Integer insertUserAuthedNumber(User user, String mobile) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("mobile", mobile) ;
		int effected = sqlSession.insert("User.insertUserAuthedNumber", map) ; //MGK user, mobile);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	public int deleteUserAuthedNumber(@Param("user") User user, @Param("mobile") String mobile) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("mobile", mobile) ;
		int effected = sqlSession.delete("User.deleteUserAuthedNumber", map) ; //MGK user, mobile);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	public int deleteInstalledAppByPushKey(String pushKey){
		ParamMap map = new ParamMap();
		map.put("pushKey", pushKey);
		int effected = sqlSession.delete("User.deleteInstalledAppByPushKey", map);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	public List<Contact> getUserAuthedNumberAll(User user) {
		return sqlSession.selectList("User.getUserAuthedNumberAll", user);
	}

	public Integer changePassword(User user) {
		int effected = sqlSession.update("User.changePassword", user);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateMobile(User user) throws ResultCodeException {
		User saved = getUserByMobile(user.getMobile());
		if (saved != null) {
			if (!user.getNo().equals(saved.getNo()))
				throw new DuplicateMobileException();
			else
				return Const.E_SUCCESS;
		}
		
		saved = getUser(user.getNo());
		
		int effected = sqlSession.update("User.updateMobile", user);
		if (effected > 0) {
			contactSvc.remappingFriendByUser(user, saved != null ? saved.getMobile() : null);
		}
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	public Integer updateEmail(User user) {
		int effected = sqlSession.update("User.updateEmail", user);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateNickname(User user) throws ResultCodeException {
		User saved = getUserByNickname(user.getNickname(), user.getAppType());
		if (saved != null) {
			if (!user.getNo().equals(saved.getNo()))
				throw new DuplicateNicknameException();
			else
				return Const.E_SUCCESS;
		}
		int effected = sqlSession.update("User.updateNickname", user);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateGender(User user) {
		int effected = sqlSession.update("User.updateGender", user);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateBirthday(User user) {
		int effected = sqlSession.update("User.updateBirthday", user);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteUser(User user) throws ResultCodeException {
		if (user == null)
			throw new InvalidArgumentException("user", "not found");
		
		User exists = null;
		if (!StringUtils.isEmpty(user.getLoginId()))
			exists = getUserByLoginId(user.getLoginId());
		else if (!StringUtils.isEmpty(user.getNickname()))
			exists = getUserByNickname(user.getNickname(), user.getAppType());
		else if (!StringUtils.isEmpty(user.getMobile()))
			exists = getUserByMobile(user.getMobile());
		else if (!StringUtils.isEmpty(user.getEmail()))
			exists = getUserByEmail(user.getEmail());
		
		if (exists == null)
			throw new NotMatchUserException();

		RedisUtil.getInstance().deleteOpsHash(REDIS_PREFIX + "loginId", exists.getLoginId());
		return sqlSession.delete("User.deleteUser", exists.getNo());
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateUseStatusAfterVerification(User user, Verification verification) throws ResultCodeException {
		Integer v = verificationSvc.confirm(verification);
		if (Const.E_SUCCESS.equals(v)) {
			int effected = sqlSession.update("User.updateUseStatus", user);
			if (effected == 0)
				throw new UnknownException();
			return Const.E_SUCCESS;
		}
		return v;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updatePushConfig(Device device) throws ResultCodeException {
		int effected = sqlSession.update("User.updatePushConfig", device);
		if (effected == 0)
			throw new NotFoundTargetException();
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateProperties(User user, Map<String, Object> properties) throws ResultCodeException {
		User tmp = user;
		if (tmp.getName() == null || StringUtils.isEmpty(tmp.getName()))
			tmp = getUser(tmp.getNo());
		
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			updateProperty(tmp, entry.getKey(), entry.getValue());
		}
		user.setProperties(tmp.getProperties());
		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updatePropertiesAll(User user, Map<String, Object> properties) throws ResultCodeException {
		user.setProperties(properties);
		sqlSession.update("User.updateProperties", user);
		return Const.E_SUCCESS;
	}

	private int updateProperty(User user, String key, Object value) {
		if (value == null) {
			if (user.getProperties() != null && 
					user.getProperties().containsKey(key)) {
				user.getProperties().remove(key);
			}
		} else {
			if (user.getProperties() == null)
				user.setProperties(new HashMap<String, Object>());
			
			user.getProperties().put(key, value);
		}
		return sqlSession.update("User.updateProperties", user);
	}
	
	public Integer updateCertificationLevel(User user) {
		int effected = sqlSession.update("User.updateCertificationLevel", user);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	
	public int getExistsNicknameUserCount(@Param("opt") SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("User.getExistsNicknameUserCount", map) ; //MGK opt);
	}
	
	public List<User> getExistsNicknameUserList(@Param("opt") SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("opt", opt) ;
		return sqlSession.selectList("User.getExistsNicknameUserList", map) ;
	}
	
	public List<User> getUserAllByStatus(SearchOpt opt, String appType) {
		ParamMap map = new ParamMap() ;
		map.put("opt", opt) ;
		map.put("appType", appType) ;
		return sqlSession.selectList("User.getUserAllByStatus", map) ;
	}

	public List<User> getUserAllMobileByStatus(SearchOpt opt, String appType) {
		ParamMap map = new ParamMap() ;
		map.put("opt", opt) ;
		map.put("appType", appType) ;
		return sqlSession.selectList("User.getUserAllMobileByStatus", map) ;
	}

	public void deleteContactLeaveUser(){
		List<User> userList = sqlSession.selectList("User.getUserAllLeave");
		for (User user : userList) {
			try {
				sqlSession.delete("Contact.deleteFriendByMobile", user.getMobile().replace("leave_", ""));
			}catch(Exception e) {
				logger.error("leaveAll() : " + AppUtil.excetionToString(e)) ;
			}
		}
	}

	public List<Long> getLeaveUserListExistPlus(){
		return sqlSession.selectList("User.getLeaveUserListExistPlus");
	}
	
	public List<User> leaveAll() {
		//Date baseDate = DateUtil.getDateAdd(DateUtil.getCurrentDate(), DateUtil.DATE, -15);
		List<User> userList = sqlSession.selectList("User.getUserAllForLeave");
		long currentMillis = System.currentTimeMillis();
		for (User user : userList) {
			String loginId = user.getLoginId();
			try {
				sqlSession.delete("Contact.deleteFriendByMobile", user.getMobile());
				sqlSession.delete("Buff.deleteBuffMember", user.getNo());

				user.setLoginId("leave_" + user.getLoginId()+currentMillis);
				user.setNickname("leave_" + user.getNickname()+currentMillis);
				user.setMobile("leave_" + user.getMobile()+currentMillis);
				user.setUseStatus("leave");
				sqlSession.update("User.leave", user);
				plusService.deleteByUser(user);
				RedisUtil.getInstance().deleteOpsHash(REDIS_PREFIX + "loginId", loginId);
				logger.info("leave user : " + user.getLoginId());
			} catch(Exception e) {
				logger.error("leaveAll() : " + AppUtil.excetionToString(e)) ;
			}

			try {
				sqlSession.delete("User.deleteLocationServiceLog", loginId);
			}catch (Exception e){
				logger.error("deleteLocationServiceLog : " + AppUtil.excetionToString(e)) ;

			}
		}
		return userList;
	}

	public List<String> getUserLoginIdList(SearchOpt opt) {
		List<String> arr= sqlSession.selectList("User.getUserLoginIdList", opt) ;

		return arr ;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Integer updateEventTicketCount(Long userNo) throws ResultCodeException {
		try {
			ParamMap map =  new ParamMap() ;
			map.put("userNo", userNo) ;
			return sqlSession.update("User.updateEventTicketCount", map);
		} catch(Exception e) {
			throw new UnknownException(e.getMessage()) ;
		}
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updatePayPassword(User user, String payPassword, Boolean encrypted) {

		if(encrypted != null && encrypted){
			payPassword = SecureUtil.decryptMobileNumber(payPassword);
		}

		ParamMap map = new ParamMap() ;
		map.put("no", user.getNo());
		map.put("payPassword", SecureUtil.encryptPassword(user.getLoginId(), payPassword));
		int effected = sqlSession.update("User.updatePayPassword", map) ;

		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	public int updatePayPasswordWithVerification(User user, String payPassword, Verification verification, Boolean encrypted)  throws ResultCodeException {
		Integer v = verificationSvc.confirm(verification);
		if (Const.E_SUCCESS.equals(v)) {

			if(encrypted != null && encrypted){
				payPassword = SecureUtil.decryptMobileNumber(payPassword);
			}

			ParamMap map = new ParamMap() ;
			map.put("no", user.getNo());
			map.put("payPassword", SecureUtil.encryptPassword(user.getLoginId(), payPassword));
			int effected = sqlSession.update("User.updatePayPassword", map) ;

			return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
		}
		return v;
	}

	public String checkLoginId(String loginId, String appType){
		if(StringUtils.isEmpty(appType)){
			appType = "pplus";
		}
		if(!appType.equals("pplus")){
			loginId = appType+"##"+loginId;
		}
		String exists = RedisUtil.getInstance().getOpsHash(REDIS_PREFIX + "loginId", loginId);
		if( exists != null && exists.equals("1") ){
			return "fail";
		} else{
			if(appType.equals("biz")){
				try {
					if(FTLinkPayApi.checkId(loginId.replace(appType+"##", ""))){
						return "success";
					}else{
						return "fail";
					}
				}catch (Exception e){
					logger.error(e.toString());
				}
			}else{
				return "success";
			}

		}

		return "fail";
	}


	public int checkPayPassword(User user, String payPassword, Boolean encrypted) {

		if(encrypted != null && encrypted){
			payPassword = SecureUtil.decryptMobileNumber(payPassword);
		}

		String encryptedPassword = SecureUtil.encryptPassword(user.getLoginId(), payPassword);
		ParamMap map = new ParamMap() ;
		map.put("no", user.getNo());
		String currentPayPassword = sqlSession.selectOne("User.getPayPassword", map);
		if(encryptedPassword.equals(currentPayPassword)){
			return Const.E_SUCCESS;
		}else {
			return Const.E_UNKNOWN;
		}
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateBuyPlusTerms(User user, Boolean terms){
		ParamMap map = new ParamMap() ;
		map.put("no", user.getNo());
		map.put("buyPlusTerms", terms);
		int effected = sqlSession.update("User.updateBuyPlusTerms", map) ;

		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updatePlusPush(User user, Boolean plusPush){
		ParamMap map = new ParamMap() ;
		map.put("no", user.getNo());
		map.put("plusPush", plusPush);
		int effected = sqlSession.update("User.updatePlusPush", map) ;

		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateActiveArea1(User user, String activeArea1Value, String activeArea1Name){
		ParamMap map = new ParamMap() ;
		map.put("no", user.getNo());
		map.put("activeArea1Value", activeArea1Value);
		map.put("activeArea1Name", activeArea1Name);
		int effected = sqlSession.update("User.updateActiveArea1", map) ;

		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateActiveArea2(User user, String activeArea2Value, String activeArea2Name){
		ParamMap map = new ParamMap() ;
		map.put("no", user.getNo());
		map.put("activeArea2Value", activeArea2Value);
		map.put("activeArea2Name", activeArea2Name);
		int effected = sqlSession.update("User.updateActiveArea2", map) ;

		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateQrImage(User user, String qrImage) {
		ParamMap map = new ParamMap() ;
		map.put("no", user.getNo());
		map.put("qrImage", qrImage);
		return sqlSession.update("User.updateQrImage", map) ;
	}
}
