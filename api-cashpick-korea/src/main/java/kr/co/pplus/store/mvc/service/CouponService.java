package kr.co.pplus.store.mvc.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.co.pplus.store.type.dto.ParamMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import kr.co.pplus.store.exception.InvalidArgumentException;
import kr.co.pplus.store.exception.NotFoundTargetException;
import kr.co.pplus.store.exception.NotPermissionException;
import kr.co.pplus.store.exception.NotPossibleDeleteException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.UnknownException;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Cooperation;
import kr.co.pplus.store.type.model.Coupon;
import kr.co.pplus.store.type.model.CouponTemplate;
import kr.co.pplus.store.type.model.MsgOnly;
import kr.co.pplus.store.type.model.Page;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.type.model.code.ActiveStatus;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.StoreUtil;

@Service
@Transactional(transactionManager = "transactionManager")
public class CouponService extends RootService {

//	@Autowired
//	CouponDao dao;
	
	@Autowired
	PageService pageSvc;
	
	@Autowired
	AttachmentService attachSvc;
	
	@Autowired
	MsgService msgSvc;
	
	
	@Autowired
	QueueService queueSvc;
	
	@Autowired
    MsgProducer producer;
	
	
	
	private final static String[] COUPON_CHARS = {
			"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"
			, "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"
			, "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
			, "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m"
			, "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
	};
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertCouponTemplate(User user, CouponTemplate template) throws ResultCodeException {
		if (template.getPublisherType() == null)
			template.setPublisherType("page");
		
		if (template.getPublisher() == null || template.getPublisher().getNo() == null)
			template.setPublisher(pageSvc.getPageByUser(user));

		if (template.getPublisherType().equals("page")) {
			Page myPage = pageSvc.getPageByUser(user);
			if (myPage == null )
				throw new NotFoundTargetException("page", "invalidate");
			
			if (!myPage.getNo().equals(template.getPublisher().getNo()))
				throw new NotPermissionException("page", "you are guest");
		}
		
		if (template.getStatus() == null)
			template.setStatus("active");
		
		int effected = sqlSession.insert("LuckyCoupon.insertCouponTemplate", template);
		
		if (template.getPublisherType().equals("page")) {
			ParamMap map = new ParamMap() ;
			map.put("page", template.getPublisher()) ;
			map.put("template", template) ;
			map.put("properties", null) ;
			sqlSession.insert("LuckyCoupon.bindCouponTemplateAndPage", map) ;
		}
		if (effected == 0)
			throw new UnknownException();
		
		/*if (template.getIcon() != null && template.getIcon().getNo() != null) {
			dao.allocateIcon(template);
		}*/
		
		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateCouponTemplate(User user, CouponTemplate template) throws ResultCodeException {
		if (template.getNo() == null)
			throw new InvalidArgumentException("template no", "required");
		
		CouponTemplate saved = sqlSession.selectOne("LuckyCoupon.getCouponTemplateWithIcon", template.getNo());
		
		if (saved == null)
			throw new NotFoundTargetException("couponTemplate", "not found");
		
		if (saved.getPublisherType().equals("page")) {
			saved = sqlSession.selectOne("LuckyCoupon.getPageCouponTemplateWithIcon", template.getNo());
			Page myPage = pageSvc.getPageByUser(user);
			
			if (myPage == null )
				throw new NotFoundTargetException("page", "invalidate");
			
			if (!myPage.getNo().equals(saved.getPublisher().getNo()))
				throw new NotPermissionException("page", "you are guest");
		}
		
		if (saved.getIcon() != null) {
			if (template.getIcon() != null) {
				if (!template.getIcon().getNo().equals(saved.getIcon().getNo())) {
					CouponTemplate dummy = new CouponTemplate();
					dummy.setNo(saved.getNo());
					sqlSession.update("LuckyCoupon.updateCouponTemplateIcon", dummy);
					attachSvc.delete(saved.getIcon());
				}
			} else {
				sqlSession.update("LuckyCoupon.updateCouponTemplateIcon", template);
				attachSvc.delete(saved.getIcon());
			}
		}
		
		int effected = sqlSession.update("LuckyCoupon.updateCouponTemplate", template);
		if (effected == 0)
			throw new UnknownException();
		
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteCouponTemplate(User user, CouponTemplate template) throws ResultCodeException {
		if (template.getNo() == null)
			throw new InvalidArgumentException("template no", "required");
		
		int exists = sqlSession.selectOne("LuckyCoupon.existsAdvertise", template);
		if (exists > 0)
			throw new NotPossibleDeleteException("advertise", "exists");
		
		CouponTemplate saved = sqlSession.selectOne("LuckyCoupon.getCouponTemplate", template.getNo());
		int effected = 0;
		if ("expire".equals(saved.getStatus())) {
			sqlSession.delete("LuckyCoupon.forceDeleteCouponTemplate", template);
		} else {
			if (saved.getDownloadCount() > 0 || saved.getGiftCount() > 0)
				throw new NotPermissionException("template", "activated.");

			sqlSession.delete("LuckyCoupon.deleteReadyCouponAllByTemplate", template);
			
			if (saved.getPublisherType().equals("page")) {
				sqlSession.delete("LuckyCoupon.deletePageCouponTemplate", template);
			} else {
				sqlSession.delete("LuckyCoupon.deleteCoopCouponTemplate", template);
			}
			
			effected = sqlSession.delete("LuckyCoupon.deleteCouponTemplate", template);
			if (effected == 0)
				throw new UnknownException();
		}
		
		if (saved.getIcon() != null) {
			attachSvc.delete(saved.getIcon());
		}
		return Const.E_SUCCESS;
	}
	
	public CouponTemplate getPageCouponTemplate(CouponTemplate template) {
		return sqlSession.selectOne("LuckyCoupon.getPageCouponTemplate", template.getNo());
	}
	
	public CouponTemplate getPageCouponTemplateWithIcon(CouponTemplate template) {
		return sqlSession.selectOne("LuckyCoupon.getPageCouponTemplateWithIcon", template.getNo());
	}
	
	public Integer getPageCouponTemplateCount(User user, Page page, CouponTemplate template, SearchOpt opt) {
		if (user == null) {
			template.setDisplay(true);
			template.setStatus("active");
		} else {
			Page myPage = pageSvc.getPageByUser(user);
			if (myPage == null || !myPage.getNo().equals(page.getNo())) {
				template.setDisplay(true);
				template.setStatus("active");
			}
		}

		ParamMap map = new ParamMap() ;
		map.put("page", page) ;
		map.put("template", template) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("LuckyCoupon.getPageCouponTemplateCount", map) ;
	}

	public List<CouponTemplate> getPageCouponTemplateList(User user, Page page, CouponTemplate template, SearchOpt opt) {
		if (user == null) {
			template.setDisplay(true);
			template.setStatus("active");
		} else {
			Page myPage = pageSvc.getPageByUser(user);
			if (myPage == null || !myPage.getNo().equals(page.getNo())) {
				template.setDisplay(true);
				template.setStatus("active");
			} 
		}
		
		if (template.getDisplay() != null && template.getDisplay() == true) {
			opt.setOrderColumn("start_datetime");
			opt.setOrderAsc("ASC");
		} else {
			opt.setOrderColumn("reg_datetime");
			opt.setOrderAsc("DESC");
		}

		ParamMap map = new ParamMap() ;
		map.put("page", page) ;
		map.put("template", template) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("LuckyCoupon.getPageCouponTemplateList", map) ;
	}
	
	public Integer getFranchiseCouponTemplateCount(User user, Cooperation franchise, SearchOpt opt) {
		CouponTemplate template = new CouponTemplate();
		template.setDisplay(true);
		ParamMap map = new ParamMap() ;
		map.put("franchise", franchise) ;
		map.put("template", template) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("LuckyCoupon.getFranchiseCouponTemplateCount", map) ;
	}

	public List<CouponTemplate> getFranchiseCouponTemplateList(User user, Cooperation franchise, SearchOpt opt) {
		CouponTemplate template = new CouponTemplate();
		template.setDisplay(true);
		opt.setOrderColumn("start_datetime");
		opt.setOrderAsc("ASC");
		ParamMap map = new ParamMap() ;
		map.put("franchise", franchise) ;
		map.put("template", template) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("LuckyCoupon.getFranchiseCouponTemplateList", map) ;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertCoupon(Coupon coupon) throws ResultCodeException {
		int effected = sqlSession.insert("LuckyCoupon.insertCoupon", coupon);
		if (effected == 0)
			throw new UnknownException();
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Coupon download(User user, CouponTemplate template) throws ResultCodeException {
		//Template 정보를 읽어와서 유효성 검사 수행
		// 쿠폰 번호 발급 (DB 중복 Check 필요)
		// 쿠폰 DB 저장.
		// 쿠폰 정보 반환.
		if (template.getNo() == null)
			throw new InvalidArgumentException("template no", "required");
		
		CouponTemplate saved = sqlSession.selectOne("LuckyCoupon.getCouponTemplate", template.getNo());
		if (saved == null)
			throw new NotFoundTargetException("template", "not found");
		
		Date now = DateUtil.getCurrentDate();
		if (saved.getDuration().getStartDate().getTime() > now.getTime() || saved.getDuration().getEndDate().getTime() < now.getTime())
			throw new NotPermissionException("expired", "true");
		
		
		if (!saved.getDisplay() || saved.getStatus().equals(ActiveStatus.deactive))
			throw new NotPermissionException("luckyCoupon", "not allow download");

		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("template", template) ;
		int count = sqlSession.selectOne("LuckyCoupon.getCouponCountByTemplate", map) ;
		
		if (count >= saved.getDownloadLimit())
			throw new NotPermissionException("luckyCoupon", "full download limited count.");
		
		Coupon coupon = new Coupon();
		coupon.setTemplate(saved);
		coupon.setCode(getNewCouponCode());
		coupon.setMethod("download");
		coupon.setReceiver(user);
		coupon.setStatus("ready");
		int effected = sqlSession.insert("LuckyCoupon.insertCoupon", coupon);
		if (effected == 0)
			throw new UnknownException("luckyCoupon", "insert fail");
		
		return coupon;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Coupon gift(User user, CouponTemplate template) throws ResultCodeException {
		if (template.getNo() == null)
			throw new InvalidArgumentException("template no", "required");
		
		CouponTemplate saved = sqlSession.selectOne("LuckyCoupon.getCouponTemplate", template.getNo());
		if (saved == null)
			throw new NotFoundTargetException("template", "not found");
		
		Date now = DateUtil.getCurrentDate();
		if (saved.getDuration().getStartDate().getTime() > now.getTime() || saved.getDuration().getEndDate().getTime() < now.getTime())
			throw new NotPermissionException("expired", "true");
		
		
		if (saved.getStatus().equals(ActiveStatus.deactive))
			throw new NotPermissionException("luckyCoupon", "deactive luckyCoupon");
		
		Coupon coupon = new Coupon();
		coupon.setTemplate(saved);
		coupon.setCode(getNewCouponCode());
		coupon.setMethod("gift");
		coupon.setReceiver(user);
		coupon.setStatus("active");
		int effected = sqlSession.insert("LuckyCoupon.insertCoupon", coupon);
		if (effected == 0)
			throw new UnknownException("luckyCoupon", "insert fail");

		sqlSession.update("LuckyCoupon.increaseTemplateGiftCount", saved);
		return coupon;
		
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer activate(User user, Coupon coupon) throws ResultCodeException {
		Coupon saved = sqlSession.selectOne("LuckyCoupon.getCouponByCode", coupon.getCode());
		if (saved == null)
			throw new NotFoundTargetException("luckyCoupon", "not found");
		
		if (!saved.getReceiver().getNo().equals(user.getNo()))
			throw new NotPermissionException("user", "not receiver");
		
		if (!"ready".equals(saved.getStatus()))
			throw new NotPermissionException("luckyCoupon", "already activated.");
		
		coupon.setStatus("active");
		int effected = sqlSession.update("LuckyCoupon.activateCoupon", coupon);
		
		if (effected == 0)
			throw new UnknownException();

		sqlSession.update("LuckyCoupon.increaseTemplateDownloadCount", saved.getTemplate());
		
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer use(User user, Coupon coupon) throws ResultCodeException {
		Coupon saved = sqlSession.selectOne("LuckyCoupon.getCouponByCode", coupon.getCode());
		
		if (saved == null)
			throw new NotFoundTargetException("luckyCoupon", "not found");
		
		if (!saved.getReceiver().getNo().equals(user.getNo()))
			throw new NotPermissionException("user", "not receiver");
		
		if (!"active".equals(saved.getStatus()))
			throw new NotPermissionException("luckyCoupon", "not active. (" + saved.getStatus() + ")");
		
		Date now = DateUtil.getCurrentDate();
		if (saved.getTemplate().getDuration().getEndDate().getTime() < now.getTime()) {
			coupon.setStatus("expired");
			sqlSession.update("LuckyCoupon.expireCouponByCode", coupon);
			return Const.E_EXPIREDEXCEPTION;
		}

		coupon.setTemplate(saved.getTemplate());

		coupon.setStatus("used");

		sqlSession.update("LuckyCoupon.useCoupon", coupon);
		sqlSession.update("LuckyCoupon.increaseTemplateUseCount", coupon.getTemplate());
		
		// 이상이 없으면 점주에게 Push를 전송해서 쿠폰의 사용 신청을 알리자.
		/*luckyCoupon.setReceiver(saved.getReceiver());
		luckyCoupon.setTemplate(saved.getTemplate());
		luckyCoupon.setStatus("requse");
		producer.push(luckyCoupon);*/
		
		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer requestUse(User user, Coupon coupon) throws ResultCodeException {
		Coupon saved = sqlSession.selectOne("LuckyCoupon.getCouponByCode", coupon.getCode());
		
		if (saved == null)
			throw new NotFoundTargetException("luckyCoupon", "not found");
		
		if (!saved.getReceiver().getNo().equals(user.getNo()))
			throw new NotPermissionException("user", "not receiver");
		
		if (!"active".equals(saved.getStatus()))
			throw new NotPermissionException("luckyCoupon", "not active. (" + saved.getStatus() + ")");
		
		Date now = DateUtil.getCurrentDate();
		if (saved.getTemplate().getDuration().getEndDate().getTime() < now.getTime()) {
			coupon.setStatus("expired");
			sqlSession.update("LuckyCoupon.expireCoupon", coupon);
			return Const.E_EXPIREDEXCEPTION;
		}


		sqlSession.update("LuckyCoupon.requestUseCoupon", coupon);
		
		// 이상이 없으면 점주에게 Push를 전송해서 쿠폰의 사용 신청을 알리자.
		coupon.setReceiver(saved.getReceiver());
		coupon.setTemplate(saved.getTemplate());
		coupon.setStatus("requse");
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer confirmUse(User user, Coupon coupon) throws ResultCodeException {
		Coupon saved = sqlSession.selectOne("LuckyCoupon.getCouponByCode", coupon.getCode());
		
		if (saved == null)
			throw new NotFoundTargetException("luckyCoupon", "not found");
		
		Page page = sqlSession.selectOne("LuckyCoupon.getPublisherAndPage", saved);
		
		if (!page.getUser().getNo().equals(user.getNo()))
			throw new NotPermissionException("user", "not luckyCoupon publisher");
		
		if (!"requse".equals(saved.getStatus()))
			throw new NotFoundTargetException("luckyCoupon", "not found request for use. (" + saved.getStatus() + ")");
		
		Date now = DateUtil.getCurrentDate();
		if (saved.getTemplate().getDuration().getEndDate().getTime() < now.getTime()) {
			coupon.setStatus("expired");
			sqlSession.update("LuckyCoupon.expireCoupon", coupon);
			return Const.E_EXPIREDEXCEPTION;
		}

		coupon.setTemplate(saved.getTemplate());

		coupon.setStatus("used");

		sqlSession.update("LuckyCoupon.useCoupon", coupon);
		sqlSession.update("LuckyCoupon.increaseTemplateUseCount", coupon.getTemplate());
		
		return Const.E_SUCCESS;
	}

	private String getNewCouponCode() {
		try {
			String prefix = DateUtil.getDateString("yyMMdd", DateUtil.getCurrentDate()); 
			String rnd = StoreUtil.getRandomString(COUPON_CHARS, 12);
			String suffix = "";
			String code = prefix + rnd + suffix;
			if ((Integer)sqlSession.selectOne("LuckyCoupon.existsCouponByCode", code) > 0)
				return getNewCouponCode();
			return code;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public int getPublishCouponCount(User user, CouponTemplate template, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("template", template) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("LuckyCoupon.getPublishCouponCount", map) ;
	}
	
	public List<Coupon> getPublishCouponList(User user, CouponTemplate template, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("template", template) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("LuckyCoupon.getPublishCouponList", map) ;
	}
	
	public int getCouponCount(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("LuckyCoupon.getCouponCount", map) ;
	}
	
	public List<Coupon> getCouponList(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("LuckyCoupon.getCouponList", map) ;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer setPlusCoupon(User user, CouponTemplate template) throws ResultCodeException {
		Page page = pageSvc.getPageByUser(user);
		if (page == null)
			throw new NotFoundTargetException("page", "not found");
		
		CouponTemplate plus = sqlSession.selectOne("LuckyCoupon.getPlusCouponTemplateWithIcon", page);
		if (plus != null) {
			sqlSession.update("LuckyCoupon.cancelPlusCouponTemplate", plus);
		}

		sqlSession.update("LuckyCoupon.setPlusCouponTemplate", template);
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void dailyExpireCouponAndTemplate() {
		List<CouponTemplate> templateList = sqlSession.selectList("LuckyCoupon.getCouponTemplateAllForExpire", 0);
		for (CouponTemplate template : templateList) {
			List<Coupon> couponList = sqlSession.selectList("LuckyCoupon.getCouponAllForExpire", template);
			for (Coupon coupon : couponList) {
				sqlSession.update("LuckyCoupon.expireCoupon", coupon);
			}
			sqlSession.update("LuckyCoupon.expireCouponTemplate", template);
		}
	}
	
	public void alertCouponExpire() throws ResultCodeException {
		User actor = new User();
		actor.setNo(2L);
		
		MsgOnly msg = new MsgOnly();
		msg.setInput("system");
		msg.setStatus("nosend");
		msg.setType("system");
		msg.setMoveType1("inner");
		msg.setMoveType2("msgbox");
		
		List<CouponTemplate> templateList = sqlSession.selectList("LuckyCoupon.getCouponTemplateAllForExpire", 1);
		for (CouponTemplate template : templateList) {
			msg.setSubject(template.getName() + " 쿠폰이 당일까지 사용 가능합니다.");
			msg.setContents(msg.getSubject());
			List<Coupon> couponList = sqlSession.selectList("LuckyCoupon.getCouponAllForExpire",template);
			for (Coupon coupon : couponList) {
				User user = coupon.getReceiver();
				msg.setMoveTarget(user);
				queueSvc.insertMsgBox(actor, msg, user, Const.APP_TYPE_USER);
			}
		}
	}
	
	public Coupon getCouponByCode(String code) {
		return sqlSession.selectOne("LuckyCoupon.getCouponByCode", code);
	}
	
	public Integer deleteCoupons(User user, List<Coupon> couponList) throws ResultCodeException {
		SearchOpt opt = new SearchOpt();
		opt.setFilter(new ArrayList<String>());
		for (Coupon coupon : couponList) {
			if (!coupon.getReceiver().getNo().equals(user.getNo()))
				throw new NotPermissionException("luckyCoupon", "not receiver");
			
			opt.getFilter().add(coupon.getCode());
		}
		
		if (opt.getFilter().size() > 0) {
			sqlSession.delete("LuckyCoupon.deleteCouponAdvertiseByCodes", opt);
			sqlSession.delete("LuckyCoupon.deleteCouponPageByCodes", opt);
			int effected = sqlSession.delete("LuckyCoupon.deleteCouponByCodes", opt);
			
			return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
		}
		return Const.E_NOTFOUND;
			
	}
	
}
