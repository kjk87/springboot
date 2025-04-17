package kr.co.pplus.store.mvc.service;

import java.util.List;

import kr.co.pplus.store.type.dto.ParamMap;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import kr.co.pplus.store.exception.AlreadyExistsException;
import kr.co.pplus.store.exception.NotFoundTargetException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.UnknownException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Fan;
import kr.co.pplus.store.type.model.FanGroup;
import kr.co.pplus.store.type.model.Page;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.type.model.UserDevice;

@Service
@Transactional(transactionManager = "transactionManager")
public class FanService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(FanService.class);
	
//	@Autowired
//	FanDao dao;
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertGroup(FanGroup group) throws ResultCodeException {
		int exists = sqlSession.selectOne("Fan.existsGroupByName", group);
		if (exists > 0)
			throw new AlreadyExistsException();
		
		int effected = sqlSession.insert("Fan.insertGroup", group);
		if (effected == 0)
			throw new UnknownException();
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateGroupPriorityAll(List<FanGroup> groupList) {
		for (FanGroup group : groupList) {
			sqlSession.update("Fan.updateGroupPriority", group);
		}
		return  Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateGroupName(FanGroup group) throws ResultCodeException {
		int exists = sqlSession.selectOne("Fan.existsGroupByName", group);
		if (exists > 0)
			throw new AlreadyExistsException();
		
		int effected = sqlSession.update("Fan.updateGroupName", group);
		if (effected == 0)
			throw new UnknownException();
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteGroup(FanGroup group) throws ResultCodeException {
		sqlSession.delete("Fan.deleteGroupMapping", group);
		sqlSession.delete("Fan.deleteGroup", group);
		return Const.E_SUCCESS;
	}
	
	public List<FanGroup> getGroupAll(Page page) {
		return sqlSession.selectList("Fan.getGroupAll", page.getNo());
	}

	public FanGroup getGroup(FanGroup group) {
		return sqlSession.selectOne("Fan.getGroup", group.getNo());
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer addListToGroup(FanGroup group, List<Fan> fanList) throws ResultCodeException {
		for (Fan fan : fanList) {
			ParamMap map = new ParamMap() ;
			map.put("group", group) ;
			map.put("fan", fan) ;
			int exists = sqlSession.selectOne("Fan.existsInGroup", map) ;
			if (exists > 0)
				throw new AlreadyExistsException();
			
			int effected = sqlSession.insert("Fan.addToGroup", map);
			if (effected == 0)
				throw new UnknownException();
		}
		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer removeListFromGroup(FanGroup group, List<Fan> fanList) throws ResultCodeException {
		for (Fan fan : fanList) {
			ParamMap map = new ParamMap() ;
			map.put("group", group) ;
			map.put("fan", fan) ;
			int exists = sqlSession.selectOne("Fan.existsInGroup", map) ;
			if (exists == 0)
				throw new NotFoundTargetException();

			int effected = sqlSession.delete("Fan.removeFromGroup", map) ;
			if (effected == 0)
				throw new UnknownException();
		}
		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer addToGroup(FanGroup group, Fan fan) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("group", group) ;
		map.put("fan", fan) ;
		int exists = sqlSession.selectOne("Fan.existsInGroup", map) ;
		if (exists > 0)
			throw new AlreadyExistsException();
		
		int effected = sqlSession.insert("Fan.addToGroup", map) ;
		if (effected == 0)
			throw new UnknownException();
			
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer removeFromGroup(FanGroup group, Fan fan) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("group", group) ;
		map.put("fan", fan) ;
		int exists = sqlSession.selectOne("Fan.existsInGroup", map) ;
		if (exists == 0)
			throw new NotFoundTargetException();

		int effected = sqlSession.delete("Fan.removeFromGroup", map) ;
		if (effected == 0)
			throw new UnknownException();
		
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer moveGroupToGroup(FanGroup src, FanGroup dest) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("src", src) ;
		map.put("dest", dest) ;
		sqlSession.insert("Fan.moveGroupToGroup", map) ;
		sqlSession.delete("Fan.deleteGroupMapping", src);
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer moveGroup(Fan fan, FanGroup src, FanGroup dest) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("src", src) ;
		map.put("dest", dest) ;
		int exists = sqlSession.selectOne("Fan.existsInGroup", map) ;
		if (exists == 0)
			throw new NotFoundTargetException();

		int effected = sqlSession.delete("Fan.removeFromGroup", map) ;
		if (effected == 0)
			throw new UnknownException();

		map.clear() ;
		map.put("dest", dest) ;
		map.put("fan", fan) ;
		exists = sqlSession.selectOne("Fan.existsInGroup", map) ;
		if (exists > 0)
			throw new AlreadyExistsException();
		
		effected = sqlSession.insert("Fan.addToGroup", map) ;
		if (effected == 0)
			throw new UnknownException();
			
		
		return Const.E_SUCCESS;
		
	}
	
	public FanGroup getDefaultGroup(Page page) {
		int exists = sqlSession.selectOne("Fan.existsDefaultGroup", page.getNo());
		if (exists > 0)
			return sqlSession.selectOne("Fan.getDefaultGroup", page.getNo());
		
		FanGroup group = new FanGroup();
		group.setDefaultGroup(true);
		group.setName("ALL");
		group.setPriority(100);
		group.setPage(page);
		sqlSession.insert("Fan.insertGroup", group);
		return group;
	}
	
	public int getCount(User user, FanGroup group, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Fan.getCount", map) ;
	}
	
	public List<Fan> getList(User user, FanGroup group, SearchOpt opt) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Fan.getList", map) ;
	}

	public int getExcludeCount(User user, Page page, FanGroup group, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("page", page) ;
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Fan.getExcludeCount", map) ;
	}
	
	public List<Fan> getExcludeList(User user, Page page, FanGroup group, SearchOpt opt) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("page", page) ;
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Fan.getExcludeList", map) ;
	}
	
	public List<UserDevice> getFanDeviceAll(Page page) {
		return sqlSession.selectList("Fan.getFanDeviceAll", page);
	}
	
	public List<User> getFanAll(Page page) {
		return sqlSession.selectList("Fan.getAllByPage", page);
	}
		
	public boolean isBlockUser(User user, Page page) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("page", page) ;
		String block = sqlSession.selectOne("Fan.isBlockUser", map) ;
		return block != null && block.equalsIgnoreCase("Y") ? true : false;
	}

}
