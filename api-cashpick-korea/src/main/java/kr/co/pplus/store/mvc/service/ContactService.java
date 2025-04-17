package kr.co.pplus.store.mvc.service;

import java.util.List;

import kr.co.pplus.store.type.dto.ParamMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import kr.co.pplus.store.exception.InvalidArgumentException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Contact;
import kr.co.pplus.store.type.model.Page;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.User;

@Service
@Transactional(transactionManager = "transactionManager")
public class ContactService extends RootService {
	private static final Logger logger = LoggerFactory.getLogger(ContactService.class);
	
//	@Autowired
//	ContactDao dao;
//
//	@Autowired
//	AuthDao authDao;
	
	@Autowired
	UserService userSvc;
	

	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateContactList(User user, List<Contact> contactList, Boolean deleteAll) {
		if (deleteAll != null && deleteAll == true)
			deleteContactAllByUser(user);
		
		for (Contact contact : contactList) {

			saveContact(user, contact, true, true);
		}
		updateVersionByUser(user);
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteContactList(User user, List<Contact> contactList) throws ResultCodeException {
		
		if (contactList == null || contactList.size() == 0)
			throw new InvalidArgumentException("contactList", "empty");

		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("contactList", contactList) ;
		int effected = sqlSession.delete("Contact.deleteList", map)  ;
		if (effected > 0)
			updateVersionByUser(user);
		return Const.E_SUCCESS;
	}
	
	public int updateVersionByUser(User user) {
		return sqlSession.update("Contact.updateVersionByUser", user);
	}


	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void saveContact(User user, Contact contact, Boolean checkExists, Boolean bindFriend) {

		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("mobile", contact.getMobile()) ;

		if (checkExists == true) {

			int exist = sqlSession.selectOne("Contact.exists", map);

			if (exist > 0) {
				return;
			}
		}
		
		int effected = sqlSession.insert("Contact.insert", map) ;
		
		if (effected > 0 && bindFriend == true) {
			contact.setUser(user);
			remappingFriendByContact(contact);
		}
		
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateVersionByMobile(User user) {
		if (user.getNo() == null)
			return 0;
		
		User fill = null;
		if (StringUtils.isEmpty(user.getMobile()))
			fill = sqlSession.selectOne("Auth.getUserByNo", user);
		else
			fill = user;
		
		if (StringUtils.isEmpty(fill.getMobile())) {
			logger.warn("Not found mobileNumber - user no=" + fill.getNo());
			return 0;
		}
		logger.debug("updateVersion - user no=" + fill.getNo() + ", mobile=" + fill.getMobile());
		return sqlSession.update("Contact.updateVersionByMobile", fill.getMobile());
	}
	
	/**
	 * 사용자의 휴대폰 번호가 변경된 경우에 친구 관계를 조정한다.
	 * 입력되는 사용자 정보에 사용자 번호와 휴대폰 번호가 있어야 한다.
	 * @param user
	 */
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void remappingFriendByUser(User user) {
		User saved = userSvc.getUser(user.getNo());
		remappingFriendByUser(user, saved.getMobile());
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void remappingFriendByUser(User user, String prevMobile) {
		if (!user.getMobile().equals(prevMobile)) {
			if (!StringUtils.isEmpty(prevMobile)) {
				sqlSession.delete("Contact.deleteFriendByMobile", prevMobile);
				sqlSession.update("Contact.updateVersionByMobile", prevMobile);
			}

			sqlSession.update("Contact.updateFriendByUser", user);
			sqlSession.insert("Contact.insertFriendByUser", user);
			updateVersionByMobile(user);
		}
	}
	
	/**
	 * 호출 전에 contact는 생성 되어 있어야 하며, contact.mobile, contact.user.no가 유효해야 한다.
	 * @param contact
	 */
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void remappingFriendByContact(Contact contact) {
		Contact saved = sqlSession.selectOne("Contact.getFriendByMobile", contact);
		if (saved == null) {
			User user = userSvc.getUserByMobile(contact.getMobile());
			if (user == null || user.getNo().equals(contact.getUser().getNo()))
				return;
			
			contact.setFriend(user);
			sqlSession.insert("Contact.insertFriendByContact",contact);
		} else {
			User user = userSvc.getUserByMobile(contact.getMobile());
			if (user == null || user.getNo().equals(contact.getUser().getNo())) {
				sqlSession.delete("Contact.deleteFriendByContact",contact);
			} else if (!user.getNo().equals(saved.getFriend().getNo())) {
				contact.setFriend(user);
				sqlSession.update("Contact.updateFriendByContact", contact);
			}
		}
		sqlSession.update("Contact.updateVersionByMobile", contact.getMobile());
		return;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void deleteContactAllByUser(User user) {
		sqlSession.delete("Contact.deleteFriendAllByUser",user);
		sqlSession.delete("Contact.deleteContactAllByUser", user);
	}
	
	public List<Contact> getContactAllByUser(User user) {
		return sqlSession.selectList("Contact.getContactAllByUser", user);
	}
	
	public int getFriendCount(User user) {
		return sqlSession.selectOne("Contact.getFriendCount", user);
	}
	
	public List<Contact> getFriendList(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Contact.getFriendList", map) ;
	}

	public int getAllFriendCount(User user) {
		return sqlSession.selectOne("Contact.getAllFriendCount", user);
	}

	public List<Contact> getAllFriendList(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Contact.getAllFriendList", map) ;
	}

	public int getUserFriendCount(User user) {
		return sqlSession.selectOne("Contact.getUserFriendCount", user);
	}

	public List<Contact> getUserFriendList(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Contact.getUserFriendList", map) ;
	}
	
	public int getExistsNicknameFriendCount(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Contact.getExistsNicknameFriendCount", map)  ;
	}
	
	public List<Contact> getExistsNicknameFriendList(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Contact.getExistsNicknameFriendList", map) ;
	}

	public int getFriendPageCount(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Contact.getFriendPageCount", map) ;
	}
	public List<Page> getFriendPageList(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Contact.getFriendPageList", map)  ;
	}
	
	public List<User> getSameFriendAll(User user, User other) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("other", other) ;
		return sqlSession.selectList("Contact.getSameFriendAll", map) ;
	}
	
	public List<User> getReverseFriendAll(User user) {
		return sqlSession.selectList("Contact.getReverseFriendAll", user);
	}
	
}
