package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.User;
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
import java.util.Date;
import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class TodayPickService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(TodayPickService.class);


    @Autowired
    private TodayPickRepository todayPickRepository;

    @Autowired
    private TodayPickWithQuestionRepository todayPickWithQuestionRepository;

    @Autowired
    private TodayPickQuestionRepository todayPickQuestionRepository;

    @Autowired
    private TodayPickReviewRepository todayPickReviewRepository;

    @Autowired
    private TodayPickReviewWithMemberRepository todayPickReviewWithMemberRepository;

    @Autowired
    TodayPickJoinRepository todayPickJoinRepository;

    @Autowired
    TodayPickJoinOnlyRepository todayPickJoinOnlyRepository;

    @Autowired
    TodayPickJoinItemRepository todayPickJoinItemRepository;

    @Autowired
    TodayPickExampleRepository todayPickExampleRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    LotteryService lotteryService;

    public List<TodayPick> getTodayPickList() {
        List<String> statusList = new ArrayList<>();
        statusList.add("active");
        statusList.add("complete");
        statusList.add("expire");

        String dateStr = AppUtil.localDatetimeNowString();

        return todayPickRepository.findAllByStatusInAndAosAndOpenStartDateLessThanEqualAndOpenEndDateGreaterThanEqualOrderByArrayAscSeqNoDesc(statusList, true, dateStr, dateStr);
    }


    public Page<TodayPick> getTodayPickList(Pageable pageable) {
        List<String> statusList = new ArrayList<>();
        statusList.add("active");
        statusList.add("complete");
        statusList.add("expire");

        String dateStr = AppUtil.localDatetimeNowString();

        return todayPickRepository.findAllByStatusInAndAosAndOpenStartDateLessThanEqualAndOpenEndDateGreaterThanEqualOrderByArrayAscSeqNoDesc(statusList, true, dateStr, dateStr, pageable);
    }


    public TodayPickWithQuestion getTodayPick(Long seqNo) {
        TodayPickWithQuestion todayPick = todayPickWithQuestionRepository.findBySeqNo(seqNo);
        if(!todayPick.getStatus().equals("complete")){
            for(TodayPickQuestion todayPickQuestion : todayPick.getQuestionList()){
                todayPickQuestion.setAnswer(null);
            }
        }

        return todayPick;
    }

    public TodayPick getTodayPickOnly(Long seqNo) {
        return todayPickRepository.findBySeqNo(seqNo);

    }

    public List<TodayPickQuestion> getTodayPickQuestionList(Long todayPickSeqNo) {
        return todayPickQuestionRepository.findAllByTodayPickSeqNoOrderByArrayAsc(todayPickSeqNo);
    }

    public TodayPickJoin getMyTodayPick(User user, Long todayPickSeqNo) {
        return todayPickJoinRepository.findFirstByMemberSeqNoAndTodayPickSeqNo(user.getNo(), todayPickSeqNo);
    }

    public Page<TodayPickJoin> getTodayPickWinnerList(Long todayPickSeqNo, Pageable pageable) {
        return todayPickJoinRepository.findAllByTodayPickSeqNoAndStatus(todayPickSeqNo, "win", pageable);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void todayPickWinConfirm(User user, Long todayPickJoinSeqNo) throws ResultCodeException {
        TodayPickJoinOnly todayPickJoin = todayPickJoinOnlyRepository.findBySeqNo(todayPickJoinSeqNo);

        if(!todayPickJoin.getMemberSeqNo().equals(user.getNo())){
            throw new NotPermissionException();
        }

        todayPickJoin.setIsConfirm(true);
        todayPickJoinOnlyRepository.save(todayPickJoin);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void todayPickJoin(User user, TodayPickJoinOnly todayPickJoinOnly) throws ResultCodeException {

        String dateStr = AppUtil.localDatetimeNowString();

        List<TodayPickJoinItem> itemList = todayPickJoinOnly.getJoinItemList();

        TodayPick todayPick = getTodayPickOnly(todayPickJoinOnly.getTodayPickSeqNo());

        try {
            Date endDate = DateUtil.getDate(DateUtil.DEFAULT_FORMAT,todayPick.getEventEndDate());
            if(endDate.getTime() < System.currentTimeMillis()){
                throw new NotJoinTimeException();
            }
        }catch (ParseException e){
            logger.error(e.toString());
        }

        if(getMyTodayPick(user, todayPickJoinOnly.getTodayPickSeqNo()) != null){
            throw new AlreadyExistsException();
        }

        todayPickJoinOnly.setMemberSeqNo(user.getNo());
        todayPickJoinOnly.setRegDatetime(dateStr);
        todayPickJoinOnly.setStatus("active");
        todayPickJoinOnly.setIsConfirm(false);

        if(!AppUtil.isEmpty(user.getRecommendationCode())){
            Member member = memberService.getMemberByRecommendKey(user.getRecommendationCode());
            if(member != null && member.getUseStatus().equals("normal") && member.getAppType().equals(Const.APP_TYPE_LUCKYBOL)){
                todayPickJoinOnly.setReferralMemberSeqNo(member.getSeqNo());
            }
        }

        todayPickJoinOnly = todayPickJoinOnlyRepository.save(todayPickJoinOnly);

        for(TodayPickJoinItem item : itemList){
            item.setJoinDatetime(dateStr);
            item.setMemberSeqNo(user.getNo());
            item.setStatus("active");
            item.setTodayPickJoinSeqNo(todayPickJoinOnly.getSeqNo());
            item.setTodayPickSeqNo(todayPickJoinOnly.getTodayPickSeqNo());
            todayPickJoinItemRepository.save(item);
            todayPickExampleRepository.updateJoinCount(item.getTodayPickExampleSeqNo());
        }

        try {
            Lottery lottery = lotteryService.getLottery();
            if(lottery != null){
                lotteryService.joinLottery(user.getNo(), lottery.getSeqNo(), 1, "pickJoin");
            }
        }catch (Exception e){
            logger.error(e.toString());
        }

    }

    public Page<TodayPickReviewWithMember> getTodayPickReviewList(Long todayPickSeqNo, Pageable pageable) {
        return todayPickReviewWithMemberRepository.findAllByTodayPickSeqNoAndStatusOrderBySeqNoDesc(todayPickSeqNo, "active", pageable);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public TodayPickReview insertTodayPickReview(Long memberSeqNo, Long todayPickSeqNo, String review) throws ResultCodeException {

        review = Filtering.filter(review);
        if (AppUtil.isEmpty(review)) {
            throw new InvalidArgumentException();
        }

        String dateStr = AppUtil.localDatetimeNowString();

        TodayPickReview todayPickReview = new TodayPickReview();
        todayPickReview.setMemberSeqNo(memberSeqNo);
        todayPickReview.setTodayPickSeqNo(todayPickSeqNo);
        todayPickReview.setReview(review);
        todayPickReview.setStatus("active");
        todayPickReview.setRegDatetime(dateStr);
        todayPickReview.setModDatetime(dateStr);

        todayPickReview = todayPickReviewRepository.save(todayPickReview);
        return todayPickReview;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public TodayPickReview updateTodayPickReview(Long memberSeqNo, Long todayPickReviewSeqNo, String review) throws ResultCodeException {

        TodayPickReview todayPickReview = todayPickReviewRepository.findBySeqNo(todayPickReviewSeqNo);


        if (!todayPickReview.getMemberSeqNo().equals(memberSeqNo)) {
            throw new NotPermissionException();
        }

        review = Filtering.filter(review);
        if (AppUtil.isEmpty(review)) {
            throw new InvalidArgumentException();
        }

        String dateStr = AppUtil.localDatetimeNowString();
        todayPickReview.setReview(review);
        todayPickReview.setModDatetime(dateStr);

        todayPickReview = todayPickReviewRepository.save(todayPickReview);
        return todayPickReview;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteTodayPickReview(Long memberSeqNo, Long todayPickReviewSeqNo) throws ResultCodeException {

        TodayPickReview todayPickReview = todayPickReviewRepository.findBySeqNo(todayPickReviewSeqNo);

        if (!todayPickReview.getMemberSeqNo().equals(memberSeqNo)) {
            throw new NotPermissionException();
        }

        todayPickReview.setStatus("inactive");

        String dateStr = AppUtil.localDatetimeNowString();
        todayPickReview.setModDatetime(dateStr);
        todayPickReviewRepository.save(todayPickReview);
    }
}
