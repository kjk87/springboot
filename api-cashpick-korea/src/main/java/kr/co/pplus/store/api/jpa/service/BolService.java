package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.BolHistory;
import kr.co.pplus.store.api.jpa.model.CashExchange;
import kr.co.pplus.store.api.jpa.model.Member;
import kr.co.pplus.store.api.jpa.model.MemberOnlyBol;
import kr.co.pplus.store.api.jpa.repository.BolHistoryRepository;
import kr.co.pplus.store.api.jpa.repository.CashExchangeRepository;
import kr.co.pplus.store.api.jpa.repository.MemberOnlyBolRepository;
import kr.co.pplus.store.api.jpa.repository.MemberRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidArgumentException;
import kr.co.pplus.store.exception.InvalidCashException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.CommonService;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class BolService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(BolService.class);

	@Autowired
	BolHistoryRepository bolHistoryRepository;


	@Autowired
	MemberRepository memberRepository;

	@Autowired
	MemberOnlyBolRepository memberOnlyBolRepository;

	@Autowired
	CashExchangeRepository cashExchangeRepository;

	@Autowired
	CommonService commonService;

	@Value("${STORE.EXCHANGE_BOL_LIMIT}")
	Long EXCHANGE_BOL_LIMIT = 10000L ;

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int increaseBol(Long memberSeqNo, BolHistory bolHistory) throws ResultCodeException {

		try{
			bolHistory.setRegDatetime(AppUtil.localDatetimeNowString());
			bolHistory = bolHistoryRepository.saveAndFlush(bolHistory);
			memberRepository.updateIncreaseBol(memberSeqNo, bolHistory.getAmount().doubleValue());

			return Const.E_SUCCESS;
		}catch (Exception e){
			logger.error(e.toString());
			throw new InvalidCashException();
		}
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void increaseBolList(List<BolHistory> bolHistoryList) {

		bolHistoryList = bolHistoryRepository.saveAll(bolHistoryList);

		List<MemberOnlyBol> memberOnlyBolList = new ArrayList<>();

		for (BolHistory bolHistory : bolHistoryList) {
			MemberOnlyBol memberOnlyBol = memberOnlyBolRepository.findBySeqNo(bolHistory.getMemberSeqNo());

			memberOnlyBol.setBol(memberOnlyBol.getBol() + bolHistory.getAmount());
			memberOnlyBolList.add(memberOnlyBol);
		}
		memberOnlyBolRepository.saveAll(memberOnlyBolList);

	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int decreaseBol(Long memberSeqNo, BolHistory bolHistory) throws ResultCodeException {

		try{
			bolHistory.setRegDatetime(AppUtil.localDatetimeNowString());
			bolHistory = bolHistoryRepository.saveAndFlush(bolHistory);
			memberRepository.updateDecreaseBol(memberSeqNo, bolHistory.getAmount().doubleValue());

			return Const.E_SUCCESS;
		}catch (Exception e){
			logger.error(e.toString());
			throw new InvalidCashException();
		}
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer adRewardBol(User user) throws ResultCodeException {

		Integer amount = 2;

		BolHistory bolHistory = new BolHistory();
		bolHistory.setAmount(amount.floatValue());
		bolHistory.setMemberSeqNo(user.getNo());
		bolHistory.setSubject("리워드 광고 적립");
		bolHistory.setPrimaryType("increase");
		bolHistory.setSecondaryType("adReward");
		bolHistory.setTargetType("member");
		bolHistory.setTargetSeqNo(user.getNo());
		bolHistory.setHistoryProp(new HashMap<String, Object>());
		bolHistory.getHistoryProp().put("적립 유형", "리워드 광고 적립");
		bolHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
		increaseBol(user.getNo(), bolHistory);
		memberRepository.updateAdCount(user.getNo());
		return amount;
	}

	public CashExchange exchangeBol(User user, CashExchange cashExchange) throws ResultCodeException  {

		Member member = memberRepository.findBySeqNo(user.getNo());

		if( cashExchange.getBol() < EXCHANGE_BOL_LIMIT || member.getBol() < cashExchange.getBol() ) {
			throw new InvalidArgumentException("amount", "exchange bol amount must be greater than " + EXCHANGE_BOL_LIMIT);
		}

		if( cashExchange.getBankName() == null || cashExchange.getBankAccountHolderName() == null ||
				cashExchange.getBankAccountId() == null ) {
			throw new InvalidArgumentException("bankInfo", "입금계좌 정보(은행명, 이름, 계좌번호)를 모두 입력하셔야 합니다.");
		}


		String dateStr = AppUtil.localDatetimeNowString() ;
		cashExchange.setSeqNo(null);
		cashExchange.setMemberSeqNo(member.getSeqNo());
		cashExchange.setCash(cashExchange.getBol()) ;
//		long refundCash = (long)(((float)cashExchange.getBol())*(100.0f-country.getProfitTaxRate())/100.0f);
		long refundCash = cashExchange.getBol() - 1000;
		cashExchange.setRefundCash(refundCash);
		cashExchange.setStatus(1);
		cashExchange.setRegDatetime(dateStr);
		cashExchange.setModDatetime(dateStr);
		cashExchange.setMemberType(member.getAppType());
		cashExchange = cashExchangeRepository.saveAndFlush(cashExchange) ;

		BolHistory bolHistory = new BolHistory() ;

		bolHistory.setHistoryProp(new HashMap<String, Object>());
		bolHistory.getHistoryProp().put("사용캐시", getMoneyType(cashExchange.getBol().toString())+"원");
		bolHistory.getHistoryProp().put("사용유형", "현금교환신청");
		bolHistory.getHistoryProp().put("현금교환금액", getMoneyType(cashExchange.getRefundCash().toString())+"원");
		String bankAccountDetail = "- 예금주 : " + cashExchange.getBankAccountHolderName() + "\n" ;
		bankAccountDetail += "- 은행명 : " + cashExchange.getBankName() + "\n" ;
		bankAccountDetail += "- 계좌번호 : " + cashExchange.getBankAccountId() ;
		bolHistory.getHistoryProp().put("계좌정보", bankAccountDetail);


		bolHistory.setAmount(cashExchange.getBol().floatValue());
		bolHistory.setMemberSeqNo(member.getSeqNo());
		bolHistory.setSubject("현금교환");
		bolHistory.setPrimaryType("decrease");
		bolHistory.setSecondaryType("exchange");
		bolHistory.setTargetType("cash_exchange");
		bolHistory.setTargetSeqNo(cashExchange.getSeqNo());


		decreaseBol(member.getSeqNo(), bolHistory);
		return cashExchange ;
	}

	public static String getMoneyType(String result){

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
}
