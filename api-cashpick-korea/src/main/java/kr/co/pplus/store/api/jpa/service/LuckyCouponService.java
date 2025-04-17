package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.LuckyCouponSend;
import kr.co.pplus.store.api.jpa.model.MemberLuckyCoupon;
import kr.co.pplus.store.api.jpa.repository.LuckyCouponRepository;
import kr.co.pplus.store.api.jpa.repository.LuckyCouponSendRepository;
import kr.co.pplus.store.api.jpa.repository.MemberLuckyCouponRepository;
import kr.co.pplus.store.api.jpa.repository.MemberRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class LuckyCouponService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(LuckyCouponService.class);

    @Autowired
    LuckyCouponRepository luckyCouponRepository;

    @Autowired
    LuckyCouponSendRepository luckyCouponSendRepository;

    @Autowired
    MemberLuckyCouponRepository memberLuckyCouponRepository;

    @Autowired
    MemberRepository memberRepository;

    public MemberLuckyCoupon getMemberLuckyCoupon(Long seqNo){
        return memberLuckyCouponRepository.findBySeqNo(seqNo);
    }

    public List<MemberLuckyCoupon> getMemberLuckyCouponList(Long memberSeqNo){
        List<Integer> statusList = new ArrayList<>();
        statusList.add(1);
        return memberLuckyCouponRepository.findAllByMemberSeqNoAndStatusInOrderByValidDatetimeAsc(memberSeqNo, statusList);
    }

    public int getMemberLuckyCouponCount(Long memberSeqNo){
        List<Integer> statusList = new ArrayList<>();
        statusList.add(1);
        return memberLuckyCouponRepository.countByMemberSeqNoAndStatusIn(memberSeqNo, statusList);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void useCoupon(Long seqNo){
        memberLuckyCouponRepository.updateUse(seqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cancelCoupon(Long seqNo){
        memberLuckyCouponRepository.updateCancel(seqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void expireCoupon(){
        String dateStr = AppUtil.localDatetimeTodayString();
        dateStr = dateStr + " 00:00:00";

        memberLuckyCouponRepository.updateExpired(dateStr);
        luckyCouponRepository.updateExpired(dateStr);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void sendCoupon() {
        String dateStr = AppUtil.localDatetimeTodayString();

        dateStr = dateStr + " 03:00:00";
        List<LuckyCouponSend> list = luckyCouponSendRepository.findAllByStatusAndSendDateTimeLessThanEqual("ready", dateStr);
        for (LuckyCouponSend luckyCouponSend : list) {
            if (luckyCouponSend.getTarget().equals("all")) {
                MemberLuckyCoupon memberLuckyCoupon;
                List<MemberLuckyCoupon> memberLuckyCouponList;
                boolean isLast = false;
                Long totalCount = 0L;
                int page = 0;
                while (!isLast) {
                    Pageable pageable = getPageable(page);
                    Page<BigInteger> seqNoPage = memberRepository.findAllLuckyball(pageable);
                    if(page == 0){
                        totalCount = seqNoPage.getTotalElements();
                    }
                    if (seqNoPage.isLast()) {
                        isLast = true;
                    }
                    memberLuckyCouponList = new ArrayList<>();
                    for (BigInteger seqNo: seqNoPage.getContent()){
                        memberLuckyCoupon = new MemberLuckyCoupon();
                        memberLuckyCoupon.setCouponSeqNo(luckyCouponSend.getCouponSeqNo());
                        memberLuckyCoupon.setCouponSendSeqNo(luckyCouponSend.getSeqNo());
                        memberLuckyCoupon.setCreateDateTime(dateStr);
                        memberLuckyCoupon.setUpdateDateTime(dateStr);
                        memberLuckyCoupon.setSendDateTime(dateStr);
                        memberLuckyCoupon.setValidDatetime(luckyCouponSend.getLuckyCoupon().getValidDatetime());
                        memberLuckyCoupon.setMemberSeqNo(seqNo.longValue());
                        memberLuckyCoupon.setStatus(1);
                        memberLuckyCouponList.add(memberLuckyCoupon);
                    }

                    memberLuckyCouponRepository.saveAll(memberLuckyCouponList);

                    page++;
                }

                luckyCouponSend.setCount(totalCount.intValue());
                luckyCouponSend.setUpdateDateTime(dateStr);
                luckyCouponSend.setStatus("complete");
                luckyCouponSendRepository.save(luckyCouponSend);


            } else if (luckyCouponSend.getTarget().equals("choice")) {
                memberLuckyCouponRepository.updateChoiceStatus(luckyCouponSend.getSeqNo(), dateStr);
                luckyCouponSend.setUpdateDateTime(dateStr);
                luckyCouponSend.setStatus("complete");
                luckyCouponSendRepository.save(luckyCouponSend);
            } else if (luckyCouponSend.getTarget().equals("target")) {
                String[] genders = luckyCouponSend.getTargetGender().split("/");

                List<String> genderList = new ArrayList<>();
                for (String gender : genders) {
                    switch (gender) {
                        case "M":
                            genderList.add("male");
                            break;
                        case "W":
                            genderList.add("female");
                            break;
                    }
                }

                String[] ages = luckyCouponSend.getTargetAge().split("/");
                boolean age10 = false, age20 = false, age30 = false, age40 = false, age50 = false, age60 = false;
                for (String age : ages) {
                    switch (age) {
                        case "10":
                            age10 = true;
                            break;
                        case "20":
                            age20 = true;
                            break;
                        case "30":
                            age30 = true;
                            break;
                        case "40":
                            age40 = true;
                            break;
                        case "50":
                            age50 = true;
                            break;
                        case "60":
                            age60 = true;
                            break;
                    }
                }

                MemberLuckyCoupon memberLuckyCoupon;
                List<MemberLuckyCoupon> memberLuckyCouponList;
                boolean isLast = false;
                Long totalCount = 0L;
                int page = 0;
                while (!isLast) {
                    Pageable pageable = getPageable(page);
                    Page<BigInteger> seqNoPage = memberRepository.findAllByTarget(genderList, age10, age20, age30, age40, age50, age60, pageable);
                    if(page == 0){
                        totalCount = seqNoPage.getTotalElements();
                    }


                    if (seqNoPage.isLast()) {
                        isLast = true;
                    }
                    memberLuckyCouponList = new ArrayList<>();
                    for (BigInteger seqNo: seqNoPage.getContent()){
                        memberLuckyCoupon = new MemberLuckyCoupon();
                        memberLuckyCoupon.setCouponSeqNo(luckyCouponSend.getCouponSeqNo());
                        memberLuckyCoupon.setCouponSendSeqNo(luckyCouponSend.getSeqNo());
                        memberLuckyCoupon.setCreateDateTime(dateStr);
                        memberLuckyCoupon.setUpdateDateTime(dateStr);
                        memberLuckyCoupon.setSendDateTime(dateStr);
                        memberLuckyCoupon.setValidDatetime(luckyCouponSend.getLuckyCoupon().getValidDatetime());
                        memberLuckyCoupon.setMemberSeqNo(seqNo.longValue());
                        memberLuckyCoupon.setStatus(1);
                        memberLuckyCouponList.add(memberLuckyCoupon);
                    }

                    memberLuckyCouponRepository.saveAll(memberLuckyCouponList);

                    page++;
                }

                luckyCouponSend.setCount(totalCount.intValue());
                luckyCouponSend.setUpdateDateTime(dateStr);
                luckyCouponSend.setStatus("complete");
                luckyCouponSendRepository.save(luckyCouponSend);

            }
        }
    }

    private Pageable getPageable(int page) {
        return new Pageable() {
            @Override
            public int getPageNumber() {
                return page;
            }

            @Override
            public int getPageSize() {
                return 10000;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return Sort.by(Sort.Direction.ASC, "seq_no");
            }

            @Override
            public Pageable next() {
                return this;
            }

            @Override
            public Pageable previousOrFirst() {
                return this;
            }

            @Override
            public Pageable first() {
                return this;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }
        };
    }

}
