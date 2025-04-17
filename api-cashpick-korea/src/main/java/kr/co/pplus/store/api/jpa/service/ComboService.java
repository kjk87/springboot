package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.Filtering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class ComboService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(ComboService.class);



	@Autowired
	private ComboEventRepository comboEventRepository;

	@Autowired
	private ComboRepository comboRepository;

	@Autowired
	private ComboWinRepository comboWinRepository;

	@Autowired
	private ComboJoinRepository comboJoinRepository;

	@Autowired
	private ComboReviewRepository comboReviewRepository;

	@Autowired
	private ComboReviewWithMemberRepository comboReviewWithMemberRepository;

	@Autowired
	ComboGiftRepository comboGiftRepository;


	public List<ComboEvent> getComboEventList(){
		List<String> statusList = new ArrayList<>();
		statusList.add("active");
		statusList.add("complete");
		statusList.add("expire");
		statusList.add("cancel");

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);

		String startDate = DateUtil.getDate(DateUtil.DEFAULT_FORMAT, calendar.getTime());
		return comboEventRepository.findAllByStatusInAndAosAndEventDatetimeGreaterThanEqualOrderByComboEventArrayDesc(statusList, true, startDate);
	}


	public ComboEvent getComboEvent(Long seqNo){
		return comboEventRepository.findBySeqNo(seqNo);
	}

	public Combo getCombo(Long memberSeqNo){
		return comboRepository.findFirstByMemberSeqNo(memberSeqNo);
	}

	public ComboGift getComboGift(){
		String monthUnique = DateUtil.getDate("yyyy-MM", new Date());
		return comboGiftRepository.findByMonthUnique(monthUnique);
	}

	public Page<ComboGift> getComboGiftList(Pageable pageable){
		return comboGiftRepository.findAllByOrderBySeqNoDesc(pageable);
	}

	public ComboWin getComboWin(Long memberSeqNo){

		String monthUnique = DateUtil.getDate("yyyy-MM", new Date());

		return comboWinRepository.findFirstByMemberSeqNoAndMonthUnique(memberSeqNo, monthUnique);
	}

	public ComboJoin getComboJoin(Long memberSeqNo, Long comboEventSeqNo){
		return comboJoinRepository.findFirstByMemberSeqNoAndComboEventSeqNo(memberSeqNo, comboEventSeqNo);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ComboJoin insertComboJoin(Long memberSeqNo, Long comboEventSeqNo, Long comboEventExampleSeqNo) throws ResultCodeException {


		if(getComboWin(memberSeqNo) != null){
			throw new AlreadyLimitException();
		}

		ComboEvent comboEvent = getComboEvent(comboEventSeqNo);
		if(comboEvent == null){
			throw new NotFoundException();
		}

		try {
			Date endDate = DateUtil.getDate(DateUtil.DEFAULT_FORMAT,comboEvent.getEventDatetime());
			if(endDate.getTime() < System.currentTimeMillis()){
				throw new NotJoinTimeException();
			}
		}catch (ParseException e){
			logger.error(e.toString());
		}

		if(getComboJoin(memberSeqNo, comboEventSeqNo) != null){
			throw new AlreadyExistsException();
		}

		ComboJoin comboJoin = new ComboJoin();
		comboJoin.setComboEventSeqNo(comboEventSeqNo);
		comboJoin.setMemberSeqNo(memberSeqNo);
		comboJoin.setStatus("active");
		comboJoin.setComboEventExampleSeqNo(comboEventExampleSeqNo);
		comboJoin.setJoinDatetime(AppUtil.localDatetimeNowString());
		comboJoin.setComboEventArray(comboEvent.getComboEventArray());
		comboJoin.setJoinUnique(memberSeqNo + "|" + comboEvent.getComboEventArray());

		comboJoin = comboJoinRepository.save(comboJoin);
		return comboJoin;

	}

	public Page<ComboReviewWithMember> getComboReviewList(Long comboEventSeqNo, Pageable pageable){
		return comboReviewWithMemberRepository.findAllByComboEventSeqNoAndStatusOrderBySeqNoAsc(comboEventSeqNo, "active", pageable);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ComboReview insertComboReview(Long memberSeqNo, Long comboEventSeqNo, Long comboJoinSeqNo, String review) throws ResultCodeException {

		review = Filtering.filter(review);
		if(AppUtil.isEmpty(review)){
			throw new InvalidArgumentException();
		}

		String dateStr = AppUtil.localDatetimeNowString();

		ComboReview comboReview = new ComboReview();
		comboReview.setMemberSeqNo(memberSeqNo);
		comboReview.setComboEventSeqNo(comboEventSeqNo);
		comboReview.setComboJoinSeqNo(comboJoinSeqNo);
		comboReview.setReview(review);
		comboReview.setStatus("active");
		comboReview.setRegDatetime(dateStr);
		comboReview.setModDatetime(dateStr);

		comboReview = comboReviewRepository.save(comboReview);
		return comboReview;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ComboReview updateComboReview(Long memberSeqNo, Long comboReviewSeqNo, String review) throws ResultCodeException {

		ComboReview comboReview = comboReviewRepository.findBySeqNo(comboReviewSeqNo);


		if(!comboReview.getMemberSeqNo().equals(memberSeqNo)){
			throw new NotPermissionException();
		}

		review = Filtering.filter(review);
		if(AppUtil.isEmpty(review)){
			throw new InvalidArgumentException();
		}

		String dateStr = AppUtil.localDatetimeNowString();
		comboReview.setReview(review);
		comboReview.setModDatetime(dateStr);

		comboReview = comboReviewRepository.save(comboReview);
		return comboReview;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void deleteComboReview(Long memberSeqNo, Long comboReviewSeqNo) throws ResultCodeException {

		ComboReview comboReview = comboReviewRepository.findBySeqNo(comboReviewSeqNo);

		if(!comboReview.getMemberSeqNo().equals(memberSeqNo)){
			throw new NotPermissionException();
		}

		comboReview.setStatus("inactive");

		String dateStr = AppUtil.localDatetimeNowString();
		comboReview.setModDatetime(dateStr);
		comboReviewRepository.save(comboReview);
	}
}
