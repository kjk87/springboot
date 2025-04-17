package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.NotPermissionException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.SecureUtil;
import kr.co.pplus.store.util.StoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class MemberService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberAttendanceRepository memberAttendanceRepository;

    @Autowired
    MemberAddressRepository memberAddressRepository;

    @Autowired
    InviteRewardRepository inviteRewardRepository;

    @Autowired
    InviteGiftRepository inviteGiftRepository;

    @Autowired
    BolService bolService;

    @Autowired
    PointService pointService;

    @Autowired
    PageJpaService pageJpaService;

    @Autowired
    LotteryService lotteryService;

    public Member getMemberBySeqNo(Long memberSeqNo) {
        return memberRepository.findBySeqNo(memberSeqNo);
    }

    public Map<String, Object> login(String loginId, String password, String appType) {

        if (AppUtil.isEmpty(appType)) {
            appType = "pplus";
        }

        if (!appType.equals("pplus") && !loginId.startsWith(appType + "##")) {
            loginId = appType + "##" + loginId;
        }

        String encryptedPwd = SecureUtil.encryptPassword(loginId.replace(appType + "##", ""), password);
        password = encryptedPwd;

        Member member = memberRepository.findByLoginIdAndPassword(loginId, password);

        if (member == null) {
            member = memberRepository.findByLoginId(loginId);
            if (member != null) {

                if (member.getLoginFailCount() == null) {
                    member.setLoginFailCount(0);
                }

                memberRepository.updateLoginFailCount(member.getSeqNo(), member.getLoginFailCount() + 1);


                member.setSeqNo(null);
                member.setPassword(null);
                Map<String, Object> err = new HashMap<String, Object>();
                err.put("resultCode", Const.E_NOTMATCHEDPWD);
                err.put("row", member);
                return err;
            }
        }

        if (member == null) {
            Map<String, Object> err = new HashMap<String, Object>();
            err.put("resultCode", Const.E_NOTFOUND);
            return err;
        }

        memberRepository.updateLoginFailCount(member.getSeqNo(), 0);
        member.setLoginFailCount(0);

        member.setPassword(null);


        if (appType.equals("biz")) {
            member.setPage(pageJpaService.getPageByMemberSeqNo(member.getSeqNo()));
        }

        //로그인 후 처리..각종 통계 등등등
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("resultCode", Const.E_SUCCESS);
        ret.put("row", member);
        return ret;
    }

    public Member getMemberByRecommendKey(String recommendKey) {
        return memberRepository.findByRecommendUniqueKey(recommendKey);
    }

    public MemberAddress getMemberAddress(Long memberSeqNo) {
        return memberAddressRepository.findByMemberSeqNo(memberSeqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public MemberAddress saveAddress(MemberAddress memberAddress) {

        memberAddress = memberAddressRepository.saveAndFlush(memberAddress);

        return memberAddress;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int adRewardReset() throws ResultCodeException {
        memberRepository.updateResetAdCount();
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public MemberAttendance attendance(User user) throws ResultCodeException {

        Integer point = StoreUtil.getRandomNumber(10) + 1;

        MemberAttendance memberAttendance = memberAttendanceRepository.findByMemberSeqNo(user.getNo());

        if (memberAttendance == null) {
            memberAttendance = new MemberAttendance();
            memberAttendance.setMemberSeqNo(user.getNo());
            memberAttendance.setAttendanceCount(1);
            memberAttendance.setAttendanceDatetime(AppUtil.localDatetimeNowString());
            memberAttendance = memberAttendanceRepository.saveAndFlush(memberAttendance);
            memberAttendance.setIsAttendance(true);
            memberAttendance.setAttendancePoint(point);
            attendancePoint(user, point.floatValue());

        } else {
            memberAttendance.setIsAttendance(false);
            try {
                Date attendanceDate = DateUtil.getDate(DateUtil.DEFAULT_FORMAT, memberAttendance.getAttendanceDatetime());
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                if (calendar.getTime().getTime() > attendanceDate.getTime()) {

                    memberAttendance.setAttendanceCount(memberAttendance.getAttendanceCount() + 1);

                    memberAttendance.setAttendanceDatetime(AppUtil.localDatetimeNowString());
                    memberAttendance = memberAttendanceRepository.saveAndFlush(memberAttendance);
                    memberAttendance.setIsAttendance(true);

                    if (memberAttendance.getAttendanceCount() == 100) {
                        attendancePoint(user, 1000f);
                        point = 1000;
                    } else if (memberAttendance.getAttendanceCount() == 10) {
                        attendancePoint(user, 300f);
                        point = 300;
                    } else if (memberAttendance.getAttendanceCount() == 30) {
                        attendancePoint(user, 500f);
                        point = 500;
                    } else {
                        attendancePoint(user, point.floatValue());
                    }

                    memberAttendance.setAttendancePoint(point);
                }

            } catch (Exception e) {
                logger.error("attendance error : " + e.toString());
            }

        }
        return memberAttendance;
    }

    public MemberAttendance getMemberAttendance(User user) throws ResultCodeException {
        MemberAttendance memberAttendance = memberAttendanceRepository.findByMemberSeqNo(user.getNo());
        if(memberAttendance != null){
            memberAttendance.setIsAttendance(false);

            try {
                Date attendanceDate = DateUtil.getDate(DateUtil.DEFAULT_FORMAT, memberAttendance.getAttendanceDatetime());
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                if (calendar.getTime().getTime() > attendanceDate.getTime()) {
                    memberAttendance.setIsAttendance(true);
                }

            } catch (Exception e) {
                logger.error(e.toString());
            }


            return memberAttendance;
        }
        return null;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public MemberAttendance attendance2(User user) throws ResultCodeException {


        MemberAttendance memberAttendance = memberAttendanceRepository.findByMemberSeqNo(user.getNo());
        if (memberAttendance == null) {
            memberAttendance = new MemberAttendance();
            memberAttendance.setMemberSeqNo(user.getNo());
            memberAttendance.setAttendanceCount(1);
            memberAttendance.setAttendanceDatetime(AppUtil.localDatetimeNowString());
            memberAttendance = memberAttendanceRepository.saveAndFlush(memberAttendance);
            memberAttendance.setIsAttendance(true);
            attendancePoint(user, 100f);
//			attendanceBol(user, 1f);
        } else {
            memberAttendance.setIsAttendance(false);
            try {
                Date attendanceDate = DateUtil.getDate(DateUtil.DEFAULT_FORMAT, memberAttendance.getAttendanceDatetime());
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                if (calendar.getTime().getTime() > attendanceDate.getTime()) {

                    if (memberAttendance.getAttendanceCount() >= 100) {
                        memberAttendance.setAttendanceCount(1);
                    } else {
                        memberAttendance.setAttendanceCount(memberAttendance.getAttendanceCount() + 1);
                    }

                    memberAttendance.setAttendanceDatetime(AppUtil.localDatetimeNowString());
                    memberAttendance = memberAttendanceRepository.saveAndFlush(memberAttendance);
                    memberAttendance.setIsAttendance(true);

                    if (memberAttendance.getAttendanceCount() >= 100) {
                        attendancePoint(user, 5000f);
//						attendanceBol(user, 50f);
                    } else if (memberAttendance.getAttendanceCount() == 30) {
                        attendancePoint(user, 1000f);
//						attendanceBol(user, 1f);
                    } else {
                        attendancePoint(user, 100f);
//						attendanceBol(user, 1f);
                    }
                }

            } catch (Exception e) {
                logger.error("attendance error : " + e.toString());
            }

        }
        return memberAttendance;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public MemberAttendance attendance3(User user) throws ResultCodeException {


        MemberAttendance memberAttendance = memberAttendanceRepository.findByMemberSeqNo(user.getNo());
        if (memberAttendance == null) {
            memberAttendance = new MemberAttendance();
            memberAttendance.setMemberSeqNo(user.getNo());
            memberAttendance.setAttendanceCount(1);
            memberAttendance.setAttendanceDatetime(AppUtil.localDatetimeNowString());
            memberAttendance = memberAttendanceRepository.saveAndFlush(memberAttendance);
            memberAttendance.setIsAttendance(true);
            attendancePoint(user, 1f);
//			attendanceBol(user, 1f);
        } else {
            memberAttendance.setIsAttendance(false);
            try {
                Date attendanceDate = DateUtil.getDate(DateUtil.DEFAULT_FORMAT, memberAttendance.getAttendanceDatetime());
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                if (calendar.getTime().getTime() > attendanceDate.getTime()) {

                    memberAttendance.setAttendanceCount(memberAttendance.getAttendanceCount() + 1);

                    memberAttendance.setAttendanceDatetime(AppUtil.localDatetimeNowString());
                    memberAttendance = memberAttendanceRepository.saveAndFlush(memberAttendance);
                    memberAttendance.setIsAttendance(true);

                    if (memberAttendance.getAttendanceCount() == 100) {
                        attendancePoint(user, 1000f);
                    } else if (memberAttendance.getAttendanceCount() == 10) {
                        attendanceBol(user, 3f);
                    } else if (memberAttendance.getAttendanceCount() == 30) {
                        attendancePoint(user, 500f);
                    } else if (memberAttendance.getAttendanceCount() > 100) {
                        attendancePoint(user, 10f);
                    } else {
                        attendancePoint(user, 1f);
                    }
                }

            } catch (Exception e) {
                logger.error("attendance error : " + e.toString());
            }

        }
        return memberAttendance;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateProfile(User user, String nickname, String gender, String birthday, String job, String regionCode, String region1, String region2, String region3) throws ResultCodeException {

        memberRepository.updateProfile(user.getNo(), nickname, gender, birthday, job, regionCode, region1, region2, region3, true);

//		if(user.getReceivedProfileReward() != null && user.getReceivedProfileReward()){
//			memberRepository.updateProfile(user.getNo(), nickname, gender, birthday, job, regionCode, region1, region2, region3, true);
//		}else{
//			if(!AppUtil.isEmpty(gender) && !AppUtil.isEmpty(birthday) && !AppUtil.isEmpty(job) && !AppUtil.isEmpty(regionCode) && !AppUtil.isEmpty(region1)){
//				memberRepository.updateProfile(user.getNo(), nickname, gender, birthday, job, regionCode, region1, region2, region3, true);
//				profilePoint(user, 100f);
//			}else{
//				memberRepository.updateProfile(user.getNo(), nickname, gender, birthday, job, regionCode, region1, region2, region3, false);
//			}
//		}

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void attendanceBol(User user, Float amount) throws ResultCodeException {
        BolHistory bolHistory = new BolHistory();
        bolHistory.setAmount(amount);
        bolHistory.setMemberSeqNo(user.getNo());
        bolHistory.setSubject("출석체크");
        bolHistory.setPrimaryType("increase");
        bolHistory.setSecondaryType("attendance");
        bolHistory.setTargetType("member");
        bolHistory.setTargetSeqNo(user.getNo());
        bolHistory.setHistoryProp(new HashMap<String, Object>());
        bolHistory.getHistoryProp().put("적립 유형", "출석적립");
        bolHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
        bolService.increaseBol(user.getNo(), bolHistory);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void attendancePoint(User user, Float amount) throws ResultCodeException {
        PointHistory pointHistory = new PointHistory();
        pointHistory.setMemberSeqNo(user.getNo());
        pointHistory.setType("charge");
        pointHistory.setPoint(amount);
        pointHistory.setSubject("출석적립");
        pointHistory.setHistoryProp(new HashMap<String, Object>());
        pointHistory.getHistoryProp().put("적립 유형", "출석적립");
        pointHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
        pointService.updatePoint(user.getNo(), pointHistory);


        try {
            Lottery lottery = lotteryService.getLottery();
            if(lottery != null){
                lotteryService.joinLottery(user.getNo(), lottery.getSeqNo(), 1, "attendance");
            }
        }catch (Exception e){
            logger.error(e.toString());
        }


    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void profilePoint(User user, Float amount) {
        PointHistory pointHistory = new PointHistory();
        pointHistory.setMemberSeqNo(user.getNo());
        pointHistory.setType("charge");
        pointHistory.setPoint(amount);
        pointHistory.setSubject("프로필 설정 완료");
        pointService.updatePoint(user.getNo(), pointHistory);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updatePlusPush(User user, Boolean plusPush) {
        memberRepository.updatePlusPush(user.getNo(), plusPush);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateBuffPostPublic(User user, Boolean buffPostPublic) {
        memberRepository.updateBuffPostPublic(user.getNo(), buffPostPublic);
    }

    public List<InviteReward> getInviteRewardList(User user) {
        return inviteRewardRepository.findAllByMemberSeqNoOrderBySeqNoDesc(user.getNo());
    }

    public List<InviteGift> getInviteGiftList() {
        return inviteGiftRepository.findAll();
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void requestInviteReward(User user, Long seqNo, String gift, Boolean isCash) throws ResultCodeException {

        InviteReward inviteReward = inviteRewardRepository.findBySeqNo(seqNo);

        if (!user.getNo().equals(inviteReward.getMemberSeqNo())) {
            throw new NotPermissionException();
        }

        String dateStr = AppUtil.localDatetimeNowString();

        if(isCash){
            inviteReward.setGift(gift);
            inviteReward.setReqDatetime(dateStr);
            inviteReward.setCompleteDatetime(dateStr);
            inviteReward.setStatus("complete");
            inviteRewardRepository.save(inviteReward);

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(user.getNo());
            pointHistory.setType("charge");
            pointHistory.setPoint(5000f);
            pointHistory.setSubject("초대하기 10명 달성");
            pointHistory.setHistoryProp(new HashMap<String, Object>());
            pointHistory.getHistoryProp().put("적립 유형", "초대하기 10명 달성");
            pointHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
            pointService.updatePoint(user.getNo(), pointHistory);

        }else{
            inviteReward.setGift(gift);
            inviteReward.setReqDatetime(dateStr);
            inviteReward.setStatus("request");
            inviteRewardRepository.save(inviteReward);
        }


    }

}
