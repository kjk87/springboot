package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.StoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "transactionManager")
public class CustomerService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(CustomerService.class);

	@Autowired
	UserService userSvc;

	@Autowired
	PageService pageSvc;
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertGroup(CustomerGroup group) throws ResultCodeException {
		int exists = sqlSession.selectOne("Customer.existsGroupByName", group);
		if (exists > 0)
			throw new AlreadyExistsException();
		
		int effected = sqlSession.insert("Customer.insertGroup", group);
		if (effected == 0)
			throw new UnknownException();
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateGroupPriorityAll(List<CustomerGroup> groupList) {
		for (CustomerGroup group : groupList) {
			sqlSession.update("Customer.updateGroupPriority", group);
		}
		return  Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateGroupName(CustomerGroup group) throws ResultCodeException {
		int exists = sqlSession.selectOne("Customer.existsGroupByName", group);
		if (exists > 0)
			throw new AlreadyExistsException();

		int effected = sqlSession.update("Customer.updateGroupName", group);
		if (effected == 0)
			throw new UnknownException();
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteGroup(CustomerGroup group) throws ResultCodeException {
		sqlSession.delete("Customer.deleteGroupMapping", group);
		sqlSession.delete("Customer.deleteGroup", group);
		return Const.E_SUCCESS;
	}
	
	public List<CustomerGroup> getCustomerGroupAll(Page page) {
		return sqlSession.selectList("Customer.getGroupAll", page.getNo());
	}

	public CustomerGroup getCustomerGroup(CustomerGroup group) {
		return sqlSession.selectOne("Customer.getGroup", group.getNo());
	}

	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Integer insertCustomerList(Page page, List<Customer> customerList)  throws ResultCodeException {
		if (page.getNo() == null)
			throw new InvalidArgumentException("pageNo", "need");
		for (Customer customer : customerList) {
			customer.setPage(page);
			customer.setMobile(StoreUtil.getValidatePhoneNumber(customer.getMobile()));
			int exists = sqlSession.selectOne("Customer.existsCustomerByMobile", customer);
			int addCount = 0;
			if (exists == 0) {
				customer.setTarget(userSvc.getUserByMobile(customer.getMobile()));
				int effected = sqlSession.insert("Customer.insertCustomer", customer);
				if (effected == 0)
					throw new UnknownException();
				
				CustomerGroup def = getDefaultGroup(customer.getPage());
				addCustomerToGroup(def, customer);
				addCount++;
			} else {
				//update
				Customer saved = sqlSession.selectOne("Customer.getCustomerByMobile", customer);
				saved.setMobile(customer.getMobile());
				saved.setTarget(userSvc.getUserByMobile(saved.getMobile()));
				saved.setName(customer.getName());
				saved.setStatus("active");
				saved.setProperties(customer.getProperties());
				
				int effected = sqlSession.update("Customer.updateCustomer", saved);
				if (effected == 0)
					throw new UnknownException();
			}
			
			if (addCount > 0)
				pageSvc.increaseCustomerCount(page, addCount);
		}
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Integer insertCustomer(Customer customer) throws ResultCodeException {
		if (customer.getPage() == null || customer.getPage().getNo() == null || customer.getMobile() == null)
			throw new InvalidArgumentException();
		customer.setMobile(StoreUtil.getValidatePhoneNumber(customer.getMobile()));
		int exists = sqlSession.selectOne("Customer.existsCustomerByMobile", customer);
		if (exists > 0) {
			Customer saved = sqlSession.selectOne("Customer.getCustomerByMobile", customer);
			if (saved.getStatus().equals("active"))
				throw new AlreadyExistsException();
			else {
				customer.setTarget(userSvc.getUserByMobile(customer.getMobile()));
				customer.setStatus("active");
				sqlSession.update("Customer.updateCustomer", customer);
			}
		} else {
		
			customer.setTarget(userSvc.getUserByMobile(customer.getMobile()));
			
			int effected = sqlSession.insert("Customer.insertCustomer", customer);
			if (effected == 0)
				throw new UnknownException();
			
			CustomerGroup def = getDefaultGroup(customer.getPage());
			addCustomerToGroup(def, customer);
			pageSvc.increaseCustomerCount(customer.getPage(), 1);
		}
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateCustomer(Customer customer) throws ResultCodeException {
		if (customer.getNo() == null || customer.getName() == null || customer.getMobile() == null)
			throw new InvalidArgumentException();
		
		customer.setMobile(StoreUtil.getValidatePhoneNumber(customer.getMobile()));
		
		Customer saved = sqlSession.selectOne("Customer.getCustomer", customer);
		if (saved == null)
			throw new NotFoundTargetException("customer", "not found");
		
		
		if (!customer.getMobile().equals(saved.getMobile())) {
			customer.setTarget(userSvc.getUserByMobile(customer.getMobile()));
		} else {
			customer.setTarget(saved.getTarget());
		}
		
		customer.setStatus(saved.getStatus());

		sqlSession.update("Customer.updateCustomer", customer);
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateCustomerStatus(Customer customer) {
		int effected = sqlSession.update("Customer.updateCustomerStatus", customer);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}	
	
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateMarketingConfig(Customer customer) {
		int effected = sqlSession.update("Customer.updateMarketingConfig", customer);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteCustomer(User user, Customer customer) throws ResultCodeException {
		if (customer.getPage() == null || customer.getPage().getNo() == null)
			throw new InvalidArgumentException();
		
		Page saved = pageSvc.getPageWithUser(customer.getPage());
		if (!saved.getUser().getNo().equals(user.getNo()))
			throw new NotPermissionException();

		sqlSession.delete("Customer.deleteCustomerMapping", customer);
		sqlSession.delete("Customer.deleteCustomer", customer);
		pageSvc.decreaseCustomerCount(customer.getPage(), 1);
		return Const.E_SUCCESS;
	}
	
	public boolean existsCustomerByMobile(Customer customer) throws ResultCodeException {
		if (customer.getPage() == null || customer.getPage().getNo() == null || customer.getMobile() == null)
			throw new InvalidArgumentException();
		customer.setMobile(StoreUtil.getValidatePhoneNumber(customer.getMobile()));
		return (Long)sqlSession.selectOne("Customer.existsCustomerByMobile", customer) > 0 ? true : false;
	}

	public Customer getCustomerByMobile(Customer customer) throws ResultCodeException {
		if (customer.getPage() == null || customer.getPage().getNo() == null || customer.getMobile() == null)
			throw new InvalidArgumentException();
		customer.setMobile(StoreUtil.getValidatePhoneNumber(customer.getMobile()));
		return sqlSession.selectOne("Customer.getCustomerByMobile", customer);
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer addCustomerListToGroup(CustomerGroup group, List<Customer> customerList) throws ResultCodeException {
		for (Customer customer : customerList) {
			ParamMap map = new ParamMap() ;
			map.put("group", group) ;
			map.put("customer", customer) ;
			int exists = sqlSession.selectOne("Customer.existsCustomerInGroup", map) ;
			if (exists > 0)
				throw new AlreadyExistsException();
			
			int effected = sqlSession.insert("Customer.addCustomerToGroup", map) ;
			if (effected == 0)
				throw new UnknownException();
		}
		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer removeCustomerListFromGroup(CustomerGroup group, List<Customer> customerList) throws ResultCodeException {
		for (Customer customer : customerList) {
			ParamMap map = new ParamMap() ;
			map.put("group", group) ;
			map.put("customer", customer) ;
			int exists = sqlSession.selectOne("Customer.existsCustomerInGroup", map) ;
			if (exists == 0)
				throw new NotFoundTargetException();

			int effected = sqlSession.delete("Customer.removeCustomerFromGroup", map) ;
			if (effected == 0)
				throw new UnknownException();
		}
		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer addCustomerToGroup(CustomerGroup group, Customer customer) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("group", group) ;
		map.put("customer", customer) ;
		int exists = sqlSession.selectOne("Customer.existsCustomerInGroup", map) ;
		if (exists > 0)
			throw new AlreadyExistsException();
		
		int effected = sqlSession.insert("Customer.addCustomerToGroup", map) ;
		if (effected == 0)
			throw new UnknownException();
			
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer removeCustomerFromGroup(CustomerGroup group, Customer customer) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("group", group) ;
		map.put("customer", customer) ;
		int exists = sqlSession.selectOne("Customer.existsCustomerInGroup", map) ;
		if (exists == 0)
			throw new NotFoundTargetException();

		int effected = sqlSession.delete("Customer.removeCustomerFromGroup", map) ;
		if (effected == 0)
			throw new UnknownException();
		
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer moveGroupToGroup(CustomerGroup src, CustomerGroup dest) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("src", src) ;
		map.put("dest", dest) ;
		sqlSession.insert("Customer.moveGroupToGroup", map) ;
		sqlSession.delete("Customer.deleteGroupMapping", src);
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer moveCustomerGroup(Customer customer, CustomerGroup src, CustomerGroup dest) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("src", src) ;
		map.put("customer", customer) ;
		int exists = sqlSession.selectOne("Customer.existsCustomerInGroup",  map) ;
		if (exists == 0)
			throw new NotFoundTargetException();

		int effected = sqlSession.delete("Customer.removeCustomerFromGroup", map) ;
		if (effected == 0)
			throw new UnknownException();

		map.clear() ;
		map.put("dest", dest) ;
		map.put("customer", customer) ;
		exists = sqlSession.selectOne("Customer.existsCustomerInGroup", map) ;
		if (exists > 0)
			throw new AlreadyExistsException();

		effected = sqlSession.selectOne("Customer.addCustomerToGroup", map) ;
		if (effected == 0)
			throw new UnknownException();
			
		
		return Const.E_SUCCESS;
		
	}
	
	private CustomerGroup getDefaultGroup(Page page) {
		int exists = sqlSession.selectOne("Customer.existsDefaultGroup", page.getNo());
		if (exists > 0)
			return sqlSession.selectOne("Customer.getDefaultGroup", page.getNo());
		
		CustomerGroup group = new CustomerGroup();
		group.setDefaultGroup(true);
		group.setName("ALL");
		group.setPriority(100);
		group.setPage(page);
		sqlSession.insert("Customer.insertGroup", group);
		return group;
	}

	public int getCustomerCountByPageSeqNo(Long pageSeqNo){
		ParamMap map = new ParamMap() ;
		map.put("pageSeqNo", pageSeqNo) ;
		return sqlSession.selectOne("Customer.getCustomerCountByPageSeqNo", map) ;
	}

	public List<Customer> getCustomerListAllByPageSeqNo(Long pageSeqNo){
		ParamMap map = new ParamMap() ;
		map.put("pageSeqNo", pageSeqNo) ;
		return sqlSession.selectOne("Customer.getCustomerListAllByPageSeqNo", map) ;
	}

	public int getCustomerCount(User user, CustomerGroup group, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Customer.getCustomerCount", map) ;
	}
	
	public List<Customer> getCustomerList(User user, CustomerGroup group, SearchOpt opt) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Customer.getCustomerList", map) ;
	}

	public int getUserCustomerCount(User user, CustomerGroup group, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Customer.getUserCustomerCount", map) ;
	}
	
	public List<Customer> getUserCustomerList(User user, CustomerGroup group, SearchOpt opt) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Customer.getUserCustomerList", map);
	}

	public int getExcludeCustomerCount(User user, Page page, CustomerGroup group, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("page", page) ;
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Customer.getExcludeCustomerCount", map) ;
	}
	
	public List<Customer> getExcludeCustomerList(User user, Page page, CustomerGroup group, SearchOpt opt) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("page", page) ;
		map.put("group", group) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Customer.getExcludeCustomerList", map) ;
	}
	
	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateTargetsByUser(User user) {
		return sqlSession.update("Customer.updateTargetsByUser", user);
	}
}
