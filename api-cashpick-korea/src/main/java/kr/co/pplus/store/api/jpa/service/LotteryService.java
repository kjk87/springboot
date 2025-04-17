package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.NotFoundException;
import kr.co.pplus.store.exception.NotJoinTimeException;
import kr.co.pplus.store.exception.NotPermissionException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.StoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class LotteryService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(LotteryService.class);


    @Autowired
    private LotteryRepository lotteryRepository;

    @Autowired
    private LotteryJoinRepository lotteryJoinRepository;

    @Autowired
    private LotteryJoinUserRepository lotteryJoinUserRepository;

    @Autowired
    private LotteryJoinCountRepository lotteryJoinCountRepository;

    @Autowired
    private LotteryJoinUserCountRepository lotteryJoinUserCountRepository;

    @Autowired
    private LotteryOnlyRoundRepository lotteryOnlyRoundRepository;

    @Autowired
    private LotteryWinConditionRepository lotteryWinConditionRepository;

    @Autowired
    private LotteryWinnerRepository lotteryWinnerRepository;

    @Autowired
    private PointService pointService;

    @PersistenceContext
    private EntityManager entityManager;


    public Lottery getLottery() {
        String dateStr = AppUtil.localDatetimeNowString();

        return lotteryRepository.findFirstByStatusAndEventStartDatetimeLessThanEqualAndEventEndDatetimeGreaterThanEqualOrderBySeqNoDesc("active", dateStr, dateStr);
    }

    public Lottery getLotteryByLotteryRound(Integer lotteryRound) {
        String dateStr = AppUtil.localDatetimeNowString();

        if (lotteryRound < 1084) {
            return null;
        }

        return lotteryRepository.findFirstByLotteryRoundAndAnnounceDatetimeLessThanEqual(lotteryRound, dateStr);
    }

    public List<LotteryOnlyRound> getLotteryRoundList() {
        String dateStr = AppUtil.localDatetimeNowString();

        return lotteryOnlyRoundRepository.findTop20ByStatusAndAnnounceDatetimeLessThanEqualAndLotteryRoundGreaterThanEqualOrderBySeqNoDesc("complete", dateStr, 1084);
    }

    public int getJoinCount(Long memberSeqNo) {
        return lotteryJoinRepository.countByMemberSeqNo(memberSeqNo);
    }

    public int getJoinCount(Long memberSeqNo, Long lotterySeqNo) {
        return lotteryJoinUserRepository.countByLotterySeqNoAndMemberSeqNo(lotterySeqNo, memberSeqNo);
    }


    public List<LotteryJoinCount> getJoinCountGroupByJoinType(Long memberSeqNo) {
        return lotteryJoinCountRepository.countGroupByJoinType(memberSeqNo);
    }

    public List<LotteryJoinUserCount> getJoinCountGroupByJoinType(Long memberSeqNo, Long lotterySeqNo) {
        return lotteryJoinUserCountRepository.countGroupByJoinType(lotterySeqNo, memberSeqNo);
    }

    public Page<LotteryJoin> getMyLotteryJoinList(Long memberSeqNo, Pageable pageable) {
        return lotteryJoinRepository.findAllByMemberSeqNoOrderBySeqNoDesc(memberSeqNo, pageable);
    }

    public Page<LotteryJoinUser> getMyLotteryJoinList(Long memberSeqNo, Long lotterySeqNo, Pageable pageable) {
        return lotteryJoinUserRepository.findAllByLotterySeqNoAndMemberSeqNoOrderBySeqNoDesc(lotterySeqNo, memberSeqNo, pageable);
    }

    public LotteryWinCondition getLotteryWinConditionByLotterySeqNo(Long lotterySeqNo) {
        return lotteryWinConditionRepository.findFirstByLotterySeqNo(lotterySeqNo);
    }

    public Page<LotteryWinner> getMyLotteryWinList(Long memberSeqNo, Long lotterySeqNo, Pageable pageable) {
        return lotteryWinnerRepository.findAllByMemberSeqNoAndLotterySeqNoOrderByGradeAsc(memberSeqNo, lotterySeqNo, pageable);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void joinLottery(Long memberSeqNo, Long lotterySeqNo, Integer count, String joinType) throws ResultCodeException {

        Lottery lottery = lotteryRepository.findBySeqNo(lotterySeqNo);

        if (!lottery.getStatus().equals("active")) {
            throw new NotJoinTimeException();
        }

        try {
            Date endDate = DateUtil.getDate(DateUtil.DEFAULT_FORMAT, lottery.getEventEndDatetime());
            if (endDate.getTime() < System.currentTimeMillis()) {
                throw new NotJoinTimeException();
            }
        } catch (ParseException e) {
            logger.error(e.toString());
        }

        if (joinType.equals("advertise")) {
            count = 3;
        }

        joinNative(memberSeqNo, lotterySeqNo, count, joinType);

    }


    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void joinNative(Long memberSeqNo, Long lotterySeqNo, Integer count, String joinType) {

        List<Integer> range = IntStream.range(1, 46).boxed().collect(Collectors.toList());//1~45
        List<Integer> noList;

        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder queryBuilder2 = new StringBuilder();

        Random random = new Random();
        int no = random.nextInt(28) + 1;

        queryBuilder.append("insert into lottery_join");
        queryBuilder.append(no);

        queryBuilder.append("(seq_no, member_seq_no, lottery_seq_no, no1, no2, no3, no4, no5, no6, join_datetime) values ");
        queryBuilder2.append("insert into lottery_join_user (seq_no, member_seq_no, lottery_seq_no, no1, no2, no3, no4, no5, no6, join_type, reg_datetime) values ");

        for (int i = 0; i < count; i++) {

            String seqNo = StoreUtil.getLottoID("") + i;
            queryBuilder.append("(");
            queryBuilder.append(seqNo);
            queryBuilder.append(",");
            queryBuilder.append(memberSeqNo);
            queryBuilder.append(",");
            queryBuilder.append(lotterySeqNo);

            queryBuilder2.append("(");
            queryBuilder2.append(seqNo);
            queryBuilder2.append(",");
            queryBuilder2.append(memberSeqNo);
            queryBuilder2.append(",");
            queryBuilder2.append(lotterySeqNo);

            Collections.shuffle(range);
            noList = range.subList(0, 6);
            Collections.sort(noList);

            for (int j = 0; j < 6; j++) {
                queryBuilder.append(",");
                queryBuilder.append(noList.get(j));

                queryBuilder2.append(",");
                queryBuilder2.append(noList.get(j));
            }

            queryBuilder.append(", now())");

            queryBuilder2.append(",'");
            queryBuilder2.append(joinType);
            queryBuilder2.append("', now())");

            if (i < count - 1) {
                queryBuilder.append(",");

                queryBuilder2.append(",");
            }

        }


        entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();
        entityManager.createNativeQuery(queryBuilder2.toString()).executeUpdate();
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void lotteryTableNoTest(Long memberSeqNo, Long lotterySeqNo, Integer count, Integer no) {

        List<Integer> range = IntStream.range(1, 46).boxed().collect(Collectors.toList());//1~45
        List<Integer> noList;

        StringBuilder queryBuilder = new StringBuilder();


        queryBuilder.append("insert into lottery_join");
        queryBuilder.append(no);

        queryBuilder.append("(seq_no, member_seq_no, lottery_seq_no, no1, no2, no3, no4, no5, no6, join_datetime) values ");

        for (int i = 0; i < count; i++) {

            String seqNo = StoreUtil.getLottoID("") + i;
            queryBuilder.append("(");
            queryBuilder.append(seqNo);
            queryBuilder.append(",");
            queryBuilder.append(memberSeqNo);
            queryBuilder.append(",");
            queryBuilder.append(lotterySeqNo);

            Collections.shuffle(range);
            noList = range.subList(0, 6);
            Collections.sort(noList);

            for (int j = 0; j < 6; j++) {
                queryBuilder.append(",");
                queryBuilder.append(noList.get(j));

            }

            queryBuilder.append(", now())");


            if (i < count - 1) {
                queryBuilder.append(",");
            }
        }

        entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void receiveLotteryWinner(Long memberSeqNo, Long lotteryWinnerSeqNo) throws ResultCodeException {
        LotteryWinner lotteryWinner = lotteryWinnerRepository.findBySeqNo(lotteryWinnerSeqNo);

        String dateStr = AppUtil.localDatetimeNowString();

        if (lotteryWinner == null) {
            throw new NotFoundException();
        }

        if (!lotteryWinner.getMemberSeqNo().equals(memberSeqNo)) {
            throw new NotPermissionException();
        }

        if (!lotteryWinner.getStatus().equals("active")) {
            throw new NotPermissionException();
        }

        if (lotteryWinner.getGiftType().equals("point")) {

            Integer point = 0;
            if (lotteryWinner.getMoney() > 50000) {
                Float tax = lotteryWinner.getMoney() * 0.22f;
                point = lotteryWinner.getMoney() - tax.intValue();
            } else {
                point = lotteryWinner.getMoney();

            }

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(memberSeqNo);
            pointHistory.setType("charge");
            pointHistory.setPoint(point.floatValue());
            pointHistory.setSubject("로또 당첨금");
            pointHistory.setHistoryProp(new HashMap<>());
            pointHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
            pointService.updatePoint(memberSeqNo, pointHistory);

            lotteryWinner.setStatus("complete");
            lotteryWinner.setStatusDatetime(dateStr);
            lotteryWinnerRepository.save(lotteryWinner);

        } else if (lotteryWinner.getGiftType().equals("lotto")) {
            Lottery lottery = getLottery();
            if (lottery != null) {
                joinLottery(memberSeqNo, lottery.getSeqNo(), lotteryWinner.getMoney(), "lotto");

                lotteryWinner.setStatus("complete");
                lotteryWinner.setStatusDatetime(dateStr);
                lotteryWinnerRepository.save(lotteryWinner);
            }

        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void totalPointGive() throws ResultCodeException {
        List<LotteryWinner> lotteryWinnerList = lotteryWinnerRepository.findTop100ByGiftTypeAndStatus("point", "active");

        if (lotteryWinnerList != null && !lotteryWinnerList.isEmpty()) {
            LotteryWinner lotteryWinner = null;
            StringBuilder queryBuilder = new StringBuilder();
            StringBuilder updateQueryBuilder = new StringBuilder();
            queryBuilder.append("insert into point_history (member_seq_no, type, point, subject, history_prop, reg_datetime) values ");
            for (int i = 0; i < lotteryWinnerList.size(); i++) {
                lotteryWinner = lotteryWinnerList.get(i);
                Integer point = 0;
                if (lotteryWinner.getMoney() > 50000) {
                    Float tax = lotteryWinner.getMoney() * 0.22f;
                    point = lotteryWinner.getMoney() - tax.intValue();
                } else {
                    point = lotteryWinner.getMoney();

                }
                queryBuilder.append("(");
                queryBuilder.append(lotteryWinner.getMemberSeqNo());
                queryBuilder.append(",'charge',");
                queryBuilder.append(point);
                queryBuilder.append(",'로또 당첨금','{\"지급처\":\"캐시픽 운영팀\"}',now())");

                if (i < lotteryWinnerList.size() - 1) {
                    queryBuilder.append(",");
                }

                updateQueryBuilder.append("update member set point = ifnull(point, 0) + ");
                updateQueryBuilder.append(point);
                updateQueryBuilder.append(" where seq_no = ");
                updateQueryBuilder.append(lotteryWinner.getMemberSeqNo());
                updateQueryBuilder.append(";");

                updateQueryBuilder.append("update lottery_winner set status = 'complete', status_datetime = now() where seq_no = ");
                updateQueryBuilder.append(lotteryWinner.getSeqNo());
                updateQueryBuilder.append(";");

            }

            entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();
            entityManager.createNativeQuery(updateQueryBuilder.toString()).executeUpdate();
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void totalGive() throws ResultCodeException {
        List<LotteryWinner> lotteryWinnerList = lotteryWinnerRepository.findTop100ByGiftTypeAndStatus("lotto", "active");

        if (lotteryWinnerList != null && !lotteryWinnerList.isEmpty()) {
            Lottery lottery = getLottery();

            List<Integer> range = IntStream.range(1, 46).boxed().collect(Collectors.toList());//1~45
            List<Integer> noList;

            StringBuilder queryBuilder = new StringBuilder();
            StringBuilder queryBuilder2 = new StringBuilder();
            StringBuilder updateQueryBuilder = new StringBuilder();

            Random random = new Random();
            int no = random.nextInt(28) + 1;

            queryBuilder.append("insert into lottery_join");
            queryBuilder.append(no);
            queryBuilder.append("(seq_no, member_seq_no, lottery_seq_no, no1, no2, no3, no4, no5, no6, join_datetime) values ");

            queryBuilder2.append("insert into lottery_join_user (seq_no, member_seq_no, lottery_seq_no, no1, no2, no3, no4, no5, no6, join_type, reg_datetime) values ");

            int seqIndex = 0;
            for (int i = 0; i < lotteryWinnerList.size(); i++) {
                for (int j = 0; j < lotteryWinnerList.get(i).getMoney(); j++) {

                    String seqNo = StoreUtil.getLottoID("") + seqIndex;
                    seqIndex++;
                    queryBuilder.append("('");
                    queryBuilder.append(seqNo);
                    queryBuilder.append("',");
                    queryBuilder.append(lotteryWinnerList.get(i).getMemberSeqNo());
                    queryBuilder.append(",");
                    queryBuilder.append(lottery.getSeqNo());

                    queryBuilder2.append("('");
                    queryBuilder2.append(seqNo);
                    queryBuilder2.append("',");
                    queryBuilder2.append(lotteryWinnerList.get(i).getMemberSeqNo());
                    queryBuilder2.append(",");
                    queryBuilder2.append(lottery.getSeqNo());

                    Collections.shuffle(range);
                    noList = range.subList(0, 6);
                    Collections.sort(noList);

                    for (int k = 0; k < 6; k++) {
                        queryBuilder.append(",");
                        queryBuilder.append(noList.get(k));

                        queryBuilder2.append(",");
                        queryBuilder2.append(noList.get(k));

                    }

                    queryBuilder.append(", now())");

                    queryBuilder2.append(",'lotto', now())");

                    if (j < lotteryWinnerList.get(i).getMoney() - 1) {
                        queryBuilder.append(",");

                        queryBuilder2.append(",");
                    }
                }

                if (i < lotteryWinnerList.size() - 1) {
                    queryBuilder.append(",");

                    queryBuilder2.append(",");
                }

                updateQueryBuilder.append("update lottery_winner set status = 'complete', status_datetime = now() where seq_no = ");
                updateQueryBuilder.append(lotteryWinnerList.get(i).getSeqNo());
                updateQueryBuilder.append(";");
            }

            entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();
            entityManager.createNativeQuery(queryBuilder2.toString()).executeUpdate();
            entityManager.createNativeQuery(updateQueryBuilder.toString()).executeUpdate();
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void totalReceivePointByMemberSeqNo(Long memberSeqNo, Long lotterySeqNo) throws ResultCodeException {
        List<LotteryWinner> lotteryWinnerList = lotteryWinnerRepository.findTop50ByGiftTypeAndStatusAndMemberSeqNoAndLotterySeqNo("point", "active", memberSeqNo, lotterySeqNo);

        if (lotteryWinnerList != null && !lotteryWinnerList.isEmpty()) {
            LotteryWinner lotteryWinner = null;
            StringBuilder queryBuilder = new StringBuilder();
            StringBuilder updateQueryBuilder = new StringBuilder();
            queryBuilder.append("insert into point_history (member_seq_no, type, point, subject, history_prop, reg_datetime) values ");
            for (int i = 0; i < lotteryWinnerList.size(); i++) {
                lotteryWinner = lotteryWinnerList.get(i);
                Integer point = 0;
                if (lotteryWinner.getMoney() > 50000) {
                    Float tax = lotteryWinner.getMoney() * 0.22f;
                    point = lotteryWinner.getMoney() - tax.intValue();
                } else {
                    point = lotteryWinner.getMoney();

                }
                queryBuilder.append("(");
                queryBuilder.append(lotteryWinner.getMemberSeqNo());
                queryBuilder.append(",'charge',");
                queryBuilder.append(point);
                queryBuilder.append(",'로또 당첨금','{\"지급처\":\"캐시픽 운영팀\"}',now())");

                if (i < lotteryWinnerList.size() - 1) {
                    queryBuilder.append(",");
                }

                updateQueryBuilder.append("update member set point = ifnull(point, 0) + ");
                updateQueryBuilder.append(point);
                updateQueryBuilder.append(" where seq_no = ");
                updateQueryBuilder.append(lotteryWinner.getMemberSeqNo());
                updateQueryBuilder.append(";");

                updateQueryBuilder.append("update lottery_winner set status = 'complete', status_datetime = now() where seq_no = ");
                updateQueryBuilder.append(lotteryWinner.getSeqNo());
                updateQueryBuilder.append(";");

            }

            entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();
            entityManager.createNativeQuery(updateQueryBuilder.toString()).executeUpdate();
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void totalReceiveLottoByMemberSeqNo(Long memberSeqNo, Long lotterySeqNo) throws ResultCodeException {
        List<LotteryWinner> lotteryWinnerList = lotteryWinnerRepository.findTop50ByGiftTypeAndStatusAndMemberSeqNoAndLotterySeqNo("lotto", "active", memberSeqNo, lotterySeqNo);

        if (lotteryWinnerList != null && !lotteryWinnerList.isEmpty()) {
            Lottery lottery = getLottery();

            List<Integer> range = IntStream.range(1, 46).boxed().collect(Collectors.toList());//1~45
            List<Integer> noList;

            StringBuilder queryBuilder = new StringBuilder();
            StringBuilder queryBuilder2 = new StringBuilder();
            StringBuilder updateQueryBuilder = new StringBuilder();

            Random random = new Random();
            int no = random.nextInt(28) + 1;

            queryBuilder.append("insert into lottery_join");
            queryBuilder.append(no);
            queryBuilder.append("(seq_no, member_seq_no, lottery_seq_no, no1, no2, no3, no4, no5, no6, join_datetime) values ");

            queryBuilder2.append("insert into lottery_join_user (seq_no, member_seq_no, lottery_seq_no, no1, no2, no3, no4, no5, no6, join_type, reg_datetime) values ");

            int seqIndex = 0;
            for (int i = 0; i < lotteryWinnerList.size(); i++) {
                for (int j = 0; j < lotteryWinnerList.get(i).getMoney(); j++) {

                    String seqNo = StoreUtil.getLottoID("") + seqIndex;
                    seqIndex++;
                    queryBuilder.append("('");
                    queryBuilder.append(seqNo);
                    queryBuilder.append("',");
                    queryBuilder.append(lotteryWinnerList.get(i).getMemberSeqNo());
                    queryBuilder.append(",");
                    queryBuilder.append(lottery.getSeqNo());

                    queryBuilder2.append("('");
                    queryBuilder2.append(seqNo);
                    queryBuilder2.append("',");
                    queryBuilder2.append(lotteryWinnerList.get(i).getMemberSeqNo());
                    queryBuilder2.append(",");
                    queryBuilder2.append(lottery.getSeqNo());

                    Collections.shuffle(range);
                    noList = range.subList(0, 6);
                    Collections.sort(noList);

                    for (int k = 0; k < 6; k++) {
                        queryBuilder.append(",");
                        queryBuilder.append(noList.get(k));

                        queryBuilder2.append(",");
                        queryBuilder2.append(noList.get(k));
                    }

                    queryBuilder.append(", now())");

                    queryBuilder2.append(",'lotto', now())");

                    if (j < lotteryWinnerList.get(i).getMoney() - 1) {
                        queryBuilder.append(",");

                        queryBuilder2.append(",");
                    }
                }

                if (i < lotteryWinnerList.size() - 1) {
                    queryBuilder.append(",");

                    queryBuilder2.append(",");
                }

                updateQueryBuilder.append("update lottery_winner set status = 'complete', status_datetime = now() where seq_no = ");
                updateQueryBuilder.append(lotteryWinnerList.get(i).getSeqNo());
                updateQueryBuilder.append(";");
            }


            entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();
            entityManager.createNativeQuery(queryBuilder2.toString()).executeUpdate();
            entityManager.createNativeQuery(updateQueryBuilder.toString()).executeUpdate();
        }
    }

    public long getLottoTypeActiveWinCount(Long memberSeqNo, Long lotterySeqNo) throws ResultCodeException {
        return lotteryWinnerRepository.countByGiftTypeAndStatusAndMemberSeqNoAndLotterySeqNo("lotto", "active", memberSeqNo, lotterySeqNo);
    }

    public static void main(String[] argv) {
    }

}
