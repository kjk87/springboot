package kr.co.pplus.store.mvc.service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import kr.co.pplus.store.api.jpa.model.CashExchange;
import kr.co.pplus.store.api.jpa.repository.CashExchangeRepository;
import kr.co.pplus.store.api.jpa.repository.MemberRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.GoogleClientUtil;
import kr.co.pplus.store.util.HttpUtil;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
public class CashBolService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(CashBolService.class);
//	@Autowired
//	CashBolDao dao;
//
//	@Autowired
//	UserDao userDao;

    @Autowired
    MsgService msgSvc;

    @Autowired
    PageService pageSvc;

    @Autowired
    CommonService commonSvc;

    @Autowired
    QueueService queueSvc;

    @Autowired
    CashExchangeRepository cashExchangeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Value("${STORE.APPLE_RECEIPTVALIDATION_URL}")
    String APPLE_RECEIPTVALIDATION_URL = "https://buy.itunes.apple.com/verifyReceipt";

    @Value("${STORE.SECOND_APPLE_RECEIPTVALIDATION_URL}")
    String SECOND_APPLE_RECEIPTVALIDATION_URL = "https://buy.itunes.apple.com/verifyReceipt";

    @Value("${STORE.GOOGLE_ACCOUNT}")
    String GOOGLE_ACCOUNT = "aaa@gmail.com";

    @Value("${STORE.EXCHANGE_BOL_LIMIT}")
    Long EXCHANGE_BOL_LIMIT = 10000L;

    @Value("${STORE.GOOGLE_P12_PATH}")
    String GOOGLE_P12_PATH = "https://buy.itunes.apple.com/verifyReceipt";

    private Integer checkAppleReceiptValidation(String url, InAppBolHistory history) throws ResultCodeException, IOException, JsonGenerationException, JsonMappingException {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/json");

        Map<String, Object> convertArgs = new HashMap<String, Object>();
        if (history.getPaymentProperties() != null) {
            for (Map.Entry<String, Object> entry : history.getPaymentProperties().entrySet()) {
                if ("receiptData".equals(entry.getKey()))
                    convertArgs.put("receipt-data", entry.getValue());
                else
                    convertArgs.put(entry.getKey(), entry.getValue());
            }
        }
        Map res = (Map<String, Object>) HttpUtil.requestJsonObject(url, "UTF-8", 5000, 5000, header, convertArgs, Map.class);
        logger.debug(res.toString());
        if (res != null) {
            history.setPaymentResult(res);
            if (res.containsKey("status")) {
                int r = (Integer) res.get("status");
                return r;
            }
        }

        throw new PaymentValidationFailException("res", res);
    }

    public Integer checkGoogleReceiptValidation(InAppBolHistory history) throws ResultCodeException, IOException, GeneralSecurityException, GoogleJsonResponseException {
        Map<String, Object> res = GoogleClientUtil.confirmPurchase(
                GOOGLE_ACCOUNT, GOOGLE_P12_PATH
                , (String) history.getPaymentProperties().get("packageName")
                , (String) history.getPaymentProperties().get("productId")
                , (String) history.getPaymentProperties().get("purchaseToken"));

        return (Integer) res.get("consumptionState");
    }

//	public Integer giveCashBySession(Session session, CashHistory history) throws ResultCodeException {
//		Integer result = giveCash(session, history);
//		if (result.equals(Const.E_SUCCESS)) {
//			session.setTotalCash(session.getTotalCash() - history.getAmount());
//			super.reloadSession(session);
//		}
//		return result;
//	}

//	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//	public Integer giveCash(User user, CashHistory history) throws ResultCodeException {
//		if (history.getTarget() == null)
//			throw new InvalidArgumentException("target", "not found");
//
//		if (user.getTotalCash() == null)
//			user = sqlSession.selectOne("User.getUser", user.getNo());
//
//		if (user.getTotalCash() < history.getAmount())
//			throw new NotEnoughCashException("totalCash", user.getTotalCash());
//
//		history.setUser(user);
//		history.setPrimaryType("decrease");
//		history.setSecondaryType("givecash");
//
//		if (StringUtils.isEmpty(history.getSubject()))
//			history.setSubject("캐쉬 선물");
//
//		if (history.getProperties() == null)
//			history.setProperties(new HashMap<String, Object>());
//
//		Integer effected1 = decreaseCash(user, history);
//		Integer effected2 = Const.E_UNKNOWN;
//		if (effected1.equals(Const.E_SUCCESS)) {
//			CashHistory pair = new CashHistory();
//			User other = new User();
//			other.setNo(history.getTarget().getNo());
//			pair.setUser(other);
//			pair.setTargetType("member");
//			pair.setAmount(history.getAmount());
//			pair.setPrimaryType("increase");
//			pair.setSecondaryType("recvCash");
//			pair.setSubject("캐쉬 선물 받음");
//
//			pair.setTarget(user);
//
//			effected2 = increaseCash(pair.getUser(), pair);
//
//			if (!effected2.equals(Const.E_SUCCESS))
//				throw new UnknownException();
//
//		} else {
//			throw new UnknownException();
//		}
//
//		return Const.E_SUCCESS;
//	}


    public CashExchange exchangeBol(Session session, CashExchange cashExchange) throws ResultCodeException {

        if (cashExchange.getBol() < EXCHANGE_BOL_LIMIT || session.getTotalBol() < cashExchange.getBol()) {
            throw new InvalidArgumentException("amount", "exchange bol amount must be greater than " + EXCHANGE_BOL_LIMIT);
        }

        if (cashExchange.getBankName() == null || cashExchange.getBankAccountHolderName() == null ||
                cashExchange.getBankAccountId() == null) {
            throw new InvalidArgumentException("bankInfo", "입금계좌 정보(은행명, 이름, 계좌번호)를 모두 입력하셔야 합니다.");
        }

        Country country = session.getCountry();


        String dateStr = AppUtil.localDatetimeNowString();
        cashExchange.setSeqNo(null);
        cashExchange.setMemberSeqNo(session.getNo());
        cashExchange.setCash(cashExchange.getBol());
//		long refundCash = (long)(((float)cashExchange.getBol())*(100.0f-country.getProfitTaxRate())/100.0f);
        long refundCash = cashExchange.getBol() - 1000;
        cashExchange.setRefundCash(refundCash);
        cashExchange.setStatus(1);
        cashExchange.setRegDatetime(dateStr);
        cashExchange.setModDatetime(dateStr);

        cashExchange = cashExchangeRepository.saveAndFlush(cashExchange);

        BolHistory bh = new BolHistory();
        bh.setProperties(new HashMap<String, Object>());
        bh.getProperties().put("사용캐시", getMoneyType(cashExchange.getBol().toString()) + "원");
        bh.getProperties().put("사용유형", "현금교환신청");
        bh.getProperties().put("현금교환금액", getMoneyType(cashExchange.getRefundCash().toString()) + "원");
        String bankAccountDetail = "- 예금주 : " + cashExchange.getBankAccountHolderName() + "\n";
        bankAccountDetail += "- 은행명 : " + cashExchange.getBankName() + "\n";
        bankAccountDetail += "- 계좌번호 : " + cashExchange.getBankAccountId();
        bh.getProperties().put("계좌정보", bankAccountDetail);
//		SimpleDateFormat sdf = new SimpleDateFormat("YYYY.MM.dd") ;
//		bh.getProperties().put("신청일자", sdf.format(new Date()));
        bh.setAmount(cashExchange.getBol().floatValue());
        bh.setUser(session);
        bh.setAmount(bh.getAmount());
        bh.setPrimaryType("decrease");
        bh.setSecondaryType("exchange");
        bh.setSubject("현금교환");
        bh.setTargetType("cash_exchange");
        bh.setTarget(new NoOnlyKey(cashExchange.getSeqNo()));
        bh.setIsLottoTicket(false);
        bh.setPage(null);

        decreaseBol(session, bh);
        return cashExchange;
    }

    public static String getMoneyType(String result) {

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');

        DecimalFormat df = new DecimalFormat("###,###");
        df.setDecimalFormatSymbols(dfs);

        try {

            double inputNum = Double.parseDouble(result);
            result = df.format(inputNum).toString();

        } catch (NumberFormatException e) {
            // TODO: handle exception
        }

        return result;
    }


    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer useBol(User user, BolHistory history) throws ResultCodeException {
        String secondaryType = history.getSecondaryType();

        if ("giftBols".equals(secondaryType))
            return useBols(user, history);

        if (history.getAmount() == null || history.getAmount() == 0)
            throw new InvalidArgumentException("amount", "empty");

        if (("giftBol".equals(secondaryType)
                || "reviewReward".equals(secondaryType)
                || "commentReward".equals(secondaryType))
                && history.getTarget() == null)
            throw new InvalidArgumentException("target", "not found");

        User me = user;
        User target = new User();
        target.setNo(history.getTarget().getNo());

        if (user.getTotalBol() == null)
            me = sqlSession.selectOne("User.getUser", user.getNo());

        if (me.getTotalBol() < history.getAmount())
            throw new NotEnoughBolException("totalBol", me.getTotalBol());

        history.setTargetType("member");
        if (StringUtils.isEmpty(history.getSubject()))
            history.setSubject("럭키볼 선물(1명)");

        Integer effected1 = decreaseBol(me, history);
        Integer effected2 = Const.E_UNKNOWN;
        if (effected1.equals(Const.E_SUCCESS)) {
            ParamMap map = new ParamMap();
            map.put("user", target);
            map.put("history", history);
            sqlSession.insert("CashBol.insertBolHistoryTarget", map);
			/*
			//즉시 상대에게 적립되지 않는다. 우선 선물함(bol_history_target)에 넣은 후에 대상자가 승인하면 그때 이력에 쌓이게 된다.
			BolHistory pair = new BolHistory();
			pair.setTargetType("member");
			pair.setAmount(history.getAmount());
			pair.setSecondaryType(getPairSecondaryType(secondaryType));
			pair.setSubject("BOL 적립");
			pair.setTarget(user);
			
			effected2 = increaseBol(target, pair);
			
			if (!effected2.equals(Const.E_SUCCESS))
				throw new UnknownException();
			*/
        } else {
            throw new UnknownException();
        }

        if ("reviewReward".equals(secondaryType)) {
            afterReviewReward(me, history);
        }

        return Const.E_SUCCESS;
    }

    private Integer useBols(User user, BolHistory history) throws ResultCodeException {
        if (history.getAmount() == null || history.getAmount() == 0)
            throw new InvalidArgumentException("amount", "empty");

        if (history.getTargetList() == null)
            throw new InvalidArgumentException("target list", "not found");

        User me = user;
        Float amount = history.getAmount();
        Float totalAmount = amount * history.getTargetList().size();

        if (user.getTotalBol() == null)
            me = sqlSession.selectOne("User.getUser", user.getNo());

        if (me.getTotalBol() < totalAmount)
            throw new NotEnoughCashException("totalBol", me.getTotalBol());

        history.setTargetType("member");
        if (StringUtils.isEmpty(history.getSecondaryType()))
            history.setSecondaryType("giftbols");

        if (StringUtils.isEmpty(history.getSubject()))
            history.setSubject("럭키볼 선물");

        history.setAmount(totalAmount);

        if (history.getProperties() == null)
            history.setProperties(new HashMap<String, Object>());

        history.getProperties().put("받는 대상", user.getDisplayName());
        history.getProperties().put("사용 유형", "럭시볼 선물");

        Integer effected1 = decreaseBol(me, history);
        Integer effected2 = Const.E_UNKNOWN;
        if (effected1.equals(Const.E_SUCCESS)) {
            history.setAmount(amount);
            for (User target : history.getTargetList()) {
                ParamMap map = new ParamMap();
                map.put("user", target);
                map.put("history", history);
                sqlSession.insert("CashBol.insertBolHistoryTarget", map);
				/*
				BolHistory pair = new BolHistory();
				pair.setTargetType("member");
				pair.setAmount(amount);
				pair.setSecondaryType("revcGift");
				pair.setSubject("BOL 선물 받음");
				pair.setTarget(user);
				
				effected2 = increaseBol(target, pair);
				
				if (!effected2.equals(Const.E_SUCCESS))
					throw new UnknownException();
				*/
            }
            history.setAmount(totalAmount);
        } else {
            throw new UnknownException();
        }

        return Const.E_SUCCESS;
    }


    private Integer afterReviewReward(User user, BolHistory history) throws ResultCodeException {
        User target = sqlSession.selectOne("User.getUser", history.getTarget().getNo());
        Page page = pageSvc.getPageByUser(user);
        pageSvc.giveReviewBol(page, target);
        return Const.E_SUCCESS;
    }

    private String getPairSecondaryType(String secondaryType) throws ResultCodeException {
        if (secondaryType.equals("giftBol") || secondaryType.equals("giftBols"))
            return "recvGift";
        else if (secondaryType.equals("reviewReward"))
            return "review";
        else if (secondaryType.equals("commentReward"))
            return "comment";
        else
            throw new InvalidArgumentException("secondaryType", "invalid");
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer decreaseCash(User user, CashLog cashLog) throws ResultCodeException {
        if (user.getCash() < cashLog.getCash())
            throw new NotEnoughCashException();

        boolean cashEmpty = false;
        if (user.getCash().equals(cashLog.getCash().doubleValue()))
            cashEmpty = true;

        Page page = pageSvc.getPageByUser(user);
        cashLog.setPageSeqNo(page.getNo());
        cashLog.setMemberSeqNo(user.getNo());
        cashLog.setType("used");

        int effected = sqlSession.insert("CashBol.insertCashLog", cashLog);
        if (effected > 0) {
            ParamMap map = new ParamMap();
            map.put("amount", Long.valueOf(cashLog.getCash()));
            map.put("no", user.getNo());
            sqlSession.update("User.decreaseCash", map);

            if (cashEmpty == true) {
                MsgOnly msg = new MsgOnly();
                msg.setInput("system");
                msg.setStatus("ready");
                msg.setType("push");
                msg.setMoveType1("inner");
                msg.setMoveType2("cashHistory");
                msg.setMoveTarget(user);
                msg.setPushCase(Const.BIZ_PUSH_ZEROCASH);
                msg.setSubject(user.getDisplayName() + " 님. 캐쉬가 모두 소진 되었습니다.");
                msg.setContents(msg.getSubject());
                msg.setAppType(Const.APP_TYPE_BIZ);
                queueSvc.insertMsgBox(StoreUtil.getCommonAdmin(), msg, user, Const.APP_TYPE_BIZ);

            }
        }
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer increaseOnlyCash(User user, Long amount) throws ResultCodeException {
        ParamMap map = new ParamMap();
        map.put("amount", amount);
        map.put("no", user.getNo());
        return sqlSession.update("User.increaseCash", map);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer increaseCash(User user, CashLog cashLog) throws ResultCodeException {

        Page page = pageSvc.getPageByUser(user);
        cashLog.setPageSeqNo(page.getNo());
        cashLog.setMemberSeqNo(user.getNo());
        cashLog.setType("charge");

        int effected = sqlSession.insert("CashBol.insertCashLog", cashLog);
        System.out.println("effected : " + effected);
        if (effected > 0) {
            increaseOnlyCash(user, cashLog.getCash().longValue());
        } else {
            throw new UnknownException();
        }
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer decreaseBol(User user, BolHistory history) throws ResultCodeException {
//		if (user.getTotalBol() < history.getAmount())
//			throw new NotEnoughBolException();

        history.setUser(user);
        history.setPrimaryType("decrease");

        int effected = sqlSession.insert("CashBol.insertBolHistory", history);
        if (effected > 0) {
            sqlSession.update("User.decreaseBol", history);
        }
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer increaseBol(User user, BolHistory history) {
        history.setUser(user);
        history.setPrimaryType("increase");

        int effected = sqlSession.insert("CashBol.insertBolHistory", history);
        if (effected > 0) {
            sqlSession.update("User.increaseBol", history);

        }
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer increaseBolList(List<BolHistory> historyList) {

        ParamMap map = new ParamMap();
        map.put("list", historyList);

        int effected = sqlSession.insert("CashBol.insertBolHistoryList", map);
        if (effected > 0) {
            map.put("amount", historyList.get(0).getAmount());
            sqlSession.update("User.increaseBolList", map);

        }
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer increasePointHistory(PointHistory pointHistory) {


        int effected = sqlSession.insert("CashBol.insertPointHistory", pointHistory);
        if (effected > 0) {
            sqlSession.update("User.increasePoint", pointHistory);

        }
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer increasePointList(List<PointHistory> historyList) {

        ParamMap map = new ParamMap();
        map.put("list", historyList);

        int effected = sqlSession.insert("CashBol.insertPointHistoryList", map);
        if (effected > 0) {
            map.put("point", historyList.get(0).getPoint());
            sqlSession.update("User.increasePointList", map);

        }
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public int getCashHistoryTotalAmount(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectOne("CashBol.getCashHistoryTotalAmount", map);
    }

    public int getCashHistoryCount(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectOne("CashBol.getCashHistoryCount", map);
    }


    public int getBolHistoryTotalAmount(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectOne("CashBol.getBolHistoryTotalAmount", map);
    }

    public List<BolHistory> getBolHistoryList(User user, SearchOpt opt) {
        if (opt.getAlign() == null || "new".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        } else if ("old".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("ASC");
        } else if ("amount".equals(opt.getAlign())) {
            opt.setOrderColumn("amount");
            opt.setOrderAsc("DESC");
        } else {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        }
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectList("CashBol.getBolHistoryList", map);
    }

    public int getBolHistoryCount(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectOne("CashBol.getBolHistoryCount", map);
    }

    public List<BolHistory> getBolHistoryWithTargetList(User user, SearchOpt opt) {
        if (opt.getAlign() == null || "new".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        } else if ("old".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("ASC");
        } else {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        }

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectList("CashBol.getBolHistoryWithTargetList", map);
    }

    public BolHistory getBolHistoryWithTarget(BolHistory history) {

        return sqlSession.selectOne("CashBol.getBolHistoryWithTarget", history.getNo());
    }

    public int getBolHistoryTargetCount(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectOne("CashBol.getBolHistoryTargetCount", map);
    }

    public List<BolHistoryTarget> getBolHistoryTargetList(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectList("CashBol.getBolHistoryTargetList", map);
    }

    public BolHistory getBolHistoryByTargetAndMemberSeqNo(Long memberSeqNo, Long targetSeqNo, String targetType) {

        ParamMap map = new ParamMap();
        map.put("memberSeqNo", memberSeqNo);
        map.put("targetSeqNo", targetSeqNo);
        map.put("targetType", targetType);
        return sqlSession.selectOne("CashBol.getBolHistoryByTargetAndMemberSeqNo", map);
    }
}
