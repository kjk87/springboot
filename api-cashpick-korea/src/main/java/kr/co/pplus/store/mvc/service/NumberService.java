package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.Duration;
import kr.co.pplus.store.type.model.Page;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.type.model.VirtualNumber;
import kr.co.pplus.store.util.StoreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "transactionManager")
public class NumberService extends RootService {

	@Autowired
	PageService pageSvc;
	
	@Autowired
	UserService userSvc;
	
	public List<VirtualNumber> getVirtualNumberAllByUser(User user) {
		return sqlSession.selectList("Number.getVirtualNumberAllByUser",user);
	}
	
	public boolean validateVirtualNumberLength(String virtualNumber) {
		if ("Y".equals(sqlSession.selectOne("Number.validateVirtualNumberLength",virtualNumber.length())))
			return true;
		return false;
	}
	
	public boolean existsVirtualNumberByPageAndType(Long pageNo, String numberType) {
		ParamMap map = new ParamMap() ;
		map.put("pageNo", pageNo) ;
		map.put("type", numberType) ;
		return (Integer)sqlSession.selectOne("Number.existsVirtualNumberByPageAndType", map) /* pageNo, numberType)*/ > 0 ? true : false;
	}
	
	public VirtualNumber getVirtualNumber(String virtualNumber) {
		return sqlSession.selectOne("Number.getVirtualNumber", virtualNumber);
	}


	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertVirtualNumber(VirtualNumber number) {
		number.setNumber(StoreUtil.getValidatePhoneNumber(number.getNumber()));
		int effected = sqlSession.insert("Number.insert", number);
		//dao.insertHistory(number);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateVirtualNumber(VirtualNumber number, boolean updateHistory) {
		number.setNumber(StoreUtil.getValidatePhoneNumber(number.getNumber()));
		
		if (updateHistory)
			sqlSession.update("Number.updateHistory", number);
		
		
		int effected = sqlSession.update("Number.update",number);
		sqlSession.insert("Number.insertHistory",number);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer allocateVirtualNumberToPage(User user, Page page, VirtualNumber number, Duration duration) throws ResultCodeException {

		if (!validateVirtualNumberLength(number.getNumber()))
			throw new ImpossibleNumberLength();
		
		number.setOpenBound("everybody");
		number.setType("normal");
		number.setActSrc("front");
		number.setActor(new User(user.getNo()));
		number.getActor().setLoginId(user.getLoginId());
		
		number.setNumber(StoreUtil.getValidatePhoneNumber(number.getNumber()));
		
		if (existsVirtualNumberByPageAndType(page.getNo(), "normal"))
			throw new AlreadyExistsException("virtualnumber", number.getNumber(), "exists", true);
		
		int result = Const.E_UNKNOWN;

		ParamMap map = new ParamMap() ;
		VirtualNumber saved = getVirtualNumber(number.getNumber());
		if (saved == null) {
			result = insertVirtualNumber(number);
			if (result ==Const.E_SUCCESS) {
				map.put("page", page) ;
				map.put("number", number) ;
				map.put("duration", duration) ;
				sqlSession.insert("Number.allocateVirtualNumberToPage", map) ;
			}
		} else {

			map.clear() ;
			map.put("pageNo", page.getNo()) ;
			map.put("virtualNumber", number.getNumber()) ;
			if ((Integer)sqlSession.selectOne("Number.existsVirtualNumberByPageAndNumber", map)  > 0)
				return Const.E_SUCCESS;
			
			Page allocatedPage = sqlSession.selectOne("Number.getAllocatedPageByNumber", number.getNumber());
			if (allocatedPage != null)
				throw new AlreadyExistsException("allocatedpage", allocatedPage.getName(), "allocated", true);
				
			if (saved.getReserved() == true)
				throw new ReservedNumberException("virtualnumber", number.getNumber(), "reserved", true);
			
			if (saved.getReserved() != null && number.getReserved() == null)
				number.setReserved(saved.getReserved());
			
			if (saved.getReservedDate() != null && number.getReservedDate() == null)
				number.setReservedDate(saved.getReservedDate());

			map.clear() ;
			map.put("page", page) ;
			map.put("number", number) ;
			map.put("duration", duration) ;
			sqlSession.insert("Number.allocateVirtualNumberToPage", map) ;
		}
			
		return result;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer allocateActiveVirtualNumberToPage(User user, Page page, VirtualNumber number, Duration duration) throws ResultCodeException {

		if (!validateVirtualNumberLength(number.getNumber()))
			throw new ImpossibleNumberLength();

		page.setUsePrnumber(true);
		number.setOpenBound("everybody");
		number.setType("normal");
		number.setActSrc("front");
		number.setActor(new User(user.getNo()));
		number.getActor().setLoginId(user.getLoginId());

		number.setNumber(StoreUtil.getValidatePhoneNumber(number.getNumber()));

		ParamMap map = new ParamMap() ;
		map.put("pageNo", page.getNo()) ;
		map.put("virtualNumber", number.getNumber()) ;
		if ((Integer)sqlSession.selectOne("Number.existsVirtualNumberByPageAndNumber", map) > 0) {

			sqlSession.update("Page.updateUsePrnumber", page) ;
			return Const.E_SUCCESS ;
		}

		int result = Const.E_UNKNOWN;

		map.clear() ;
		VirtualNumber saved = getVirtualNumber(number.getNumber());
		if (saved == null) {
			result = insertVirtualNumber(number);
			if (result ==Const.E_SUCCESS) {
				map.put("page", page) ;
				map.put("number", number) ;
				map.put("duration", duration) ;
				sqlSession.delete("Number.clearVirtualNumberFromPage", page.getNo()) ;
				sqlSession.insert("Number.allocateActiveVirtualNumberToPage", map) ;
				sqlSession.update("Page.updateUsePrnumber", page) ;
			} else {
				new UnknownException("allocatedpage", "insertVirtualNumber(number) ERROR", "allocated", true);
			}
		} else {


			Page allocatedPage = sqlSession.selectOne("Number.getAllocatedPageByNumber", number.getNumber());
			if (allocatedPage != null && allocatedPage.getNo() != page.getNo() )
				throw new AlreadyExistsException("allocatedpage", allocatedPage.getName(), "allocated", true);

			if (saved.getReserved() == true) {
				throw new ReservedNumberException("virtualnumber", number.getNumber(), "reserved", true);
			}
			else  {

				if (saved.getReserved() != null && number.getReserved() == null) {
					number.setReserved(saved.getReserved());
					number.setReservedDate(saved.getReservedDate());
					sqlSession.update("Number.update", number) ;

				}
			}

			map.clear() ;
			map.put("page", page) ;
			map.put("number", number) ;
			map.put("duration", duration) ;
			sqlSession.delete("Number.clearVirtualNumberFromPage", page.getNo()) ;
			sqlSession.insert("Number.allocateActiveVirtualNumberToPage", map);
			sqlSession.update("Page.updateUsePrnumber", page) ;
			return Const.E_SUCCESS ;
		}
		return result ;
	}
}



