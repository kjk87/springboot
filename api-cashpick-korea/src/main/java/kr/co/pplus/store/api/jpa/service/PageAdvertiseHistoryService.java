package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.AgentRepository;
import kr.co.pplus.store.api.jpa.repository.PageAdvertiseHistoryRepository;
import kr.co.pplus.store.api.jpa.repository.PageRepository;
import kr.co.pplus.store.api.jpa.repository.RecommendExpirationDateRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PageAdvertiseHistoryService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(PageAdvertiseHistoryService.class);

	@Autowired
	PageAdvertiseHistoryRepository pageAdvertiseHistoryRepository;

	@Autowired
	PageRepository pageRepository;

	@Autowired
	AgentRepository agentRepository;

	@Autowired
	MemberService memberService;

	@Autowired
	BolService bolService;

	@Autowired
	RecommendExpirationDateRepository recommendExpirationDateRepository;



	public Page<PageAdvertiseHistory> getPageAdvertiseHistoryListByPageSeqNo(Long pageSeqNo, String startDatetime, String endDatetime, Pageable pageable) throws ResultCodeException {

		return pageAdvertiseHistoryRepository.findAllByPageSeqNoAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, startDatetime, endDatetime, pageable);
	}

	public Integer getPageAdvertiseTotalPriceByPageSeqNo(Long pageSeqNo, String startDatetime, String endDatetime) throws ResultCodeException {

		return pageAdvertiseHistoryRepository.sumPrice(pageSeqNo, startDatetime, endDatetime);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public PageAdvertiseHistory save(PageAdvertiseHistory pageAdvertiseHistory, Long memberSeqNo) throws ResultCodeException{

		kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(pageAdvertiseHistory.getPageSeqNo());

		if(page.getAgent().getType()  == 1){//총판

			pageAdvertiseHistory.setIsExceed(isExceed(page.getAgent(), pageAdvertiseHistory.getPrice()));

			pageAdvertiseHistory.setWholesaleCode(page.getAgent().getCode());
		}else if(page.getAgent().getType() == 2){//대행사

			Agent wholesaleAgent = agentRepository.findByCode(page.getAgent().getParents());

			pageAdvertiseHistory.setIsExceed(isExceed(wholesaleAgent, pageAdvertiseHistory.getPrice()));
			pageAdvertiseHistory.setDistributeCode(page.getAgent().getCode());
			pageAdvertiseHistory.setWholesaleCode(page.getAgent().getParents());
		}else {
			String parentsCode = page.getAgent().getParents();//에이전트코드

			Agent agent = getDistAgentCode(parentsCode);
			Agent wholesaleAgent = agentRepository.findByCode(agent.getParents());

			pageAdvertiseHistory.setIsExceed(isExceed(wholesaleAgent, pageAdvertiseHistory.getPrice()));
			pageAdvertiseHistory.setDistributeCode(agent.getCode());
			pageAdvertiseHistory.setWholesaleCode(agent.getParents());
		}

		if(memberSeqNo != null){
			Member member = memberService.getMemberBySeqNo(memberSeqNo);
			if(!AppUtil.isEmpty(member.getRecommendationCode())){

				Member recommendMember = memberService.getMemberByRecommendKey(member.getRecommendationCode());
				RecommendExpirationDate recommendExpirationDate = recommendExpirationDateRepository.findByType(recommendMember.getAppType());

				if(recommendExpirationDate != null && !LocalDate.now().isAfter(recommendExpirationDate.getExpirationDate())){
					pageAdvertiseHistory.setRecommendAppType(recommendMember.getAppType());
					pageAdvertiseHistory.setRecommendSeqNo(recommendMember.getSeqNo());
					pageAdvertiseHistory.setRecommendProfit(Const.ADS_RECOMMEND_PROFIT);
					pageAdvertiseHistory.setRecommendUserSeqNo(memberSeqNo);

					BolHistory bolHistory = new BolHistory();
					bolHistory.setAmount(Const.ADS_RECOMMEND_PROFIT);
					bolHistory.setMemberSeqNo(recommendMember.getSeqNo());
					bolHistory.setPrimaryType("increase");
					bolHistory.setTargetType("member");
					bolHistory.setTargetSeqNo(recommendMember.getSeqNo());
					bolHistory.setSecondaryType("recommendProfit");
					bolHistory.setSubject("추천인 광고 수익");
					bolHistory.setHistoryProp(new HashMap<String, Object>());
					bolHistory.getHistoryProp().put("지급처", "오리마켓 운영팀");
					bolHistory.getHistoryProp().put("적립유형", "추천인 광고 수익");
					bolHistory.getHistoryProp().put("추천인", member.getNickname());

					bolService.increaseBol(recommendMember.getSeqNo(), bolHistory);
				}


			}
		}

		return pageAdvertiseHistoryRepository.saveAndFlush(pageAdvertiseHistory);
	}

	private Boolean isExceed(Agent agent, Integer price){
		logger.debug("expire time : "+agent.getExpirationDate().getTime());
		logger.debug("this time : "+System.currentTimeMillis());
		if(agent.getExpirationDate() != null && agent.getExpirationDate().getTime() < System.currentTimeMillis()){
			logger.debug("expired");
			return true;
		}

		if(agent.getTargetProfits() != null){
			Float sumAdvertiseFee = pageAdvertiseHistoryRepository.totalProfit(agent.getCode());
			Float advertiseFee = price*(agent.getAdvertiseFee()/100);

			logger.debug("fee : "+sumAdvertiseFee+advertiseFee);
			logger.debug("targetProfits : "+agent.getTargetProfits());

			if(sumAdvertiseFee+advertiseFee >= agent.getTargetProfits()){
				logger.debug("exceed");
				return true;
			}
		}
		return false;
	}

	public Agent getDistAgentCode(String parentsCode){

		Agent agent = agentRepository.findByCode(parentsCode);
		parentsCode = agent.getParents();

		Agent distAgent = null;
		if(agent.getType() == 2){
			distAgent = agent;
		}else{
			distAgent = agentRepository.findByCode(parentsCode);
		}

		return distAgent;
	}

}
