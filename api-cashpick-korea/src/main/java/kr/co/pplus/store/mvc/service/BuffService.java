package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(transactionManager = "transactionManager")
public class BuffService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(BuffService.class);

    @Autowired
    CashBolService cashBolService;

    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void divideList(List<BuffMsg> buffMsgList) throws ResultCodeException {

        List<PointHistory> pointHistoryList = new ArrayList<>();
        List<BolHistory> bolHistoryList = new ArrayList<>();
        List<BuffDividedBolLog> buffDividedBolLogList = new ArrayList<>();
        List<ParamMap> pointParamMapList = new ArrayList<>();
        List<ParamMap> bolParamMapList = new ArrayList<>();

        for (BuffMsg buffMsg : buffMsgList) {
            BuffMember me = null;

            List<BuffMember> buffMemberList = sqlSession.selectList("Buff.getBuffMemberList", buffMsg.getBuffSeqNo());
            Float amount = buffMsg.getAmount() / (buffMemberList.size() - 1);

            for (BuffMember buffMember : buffMemberList) {
                if (buffMember.getMemberSeqNo().equals(buffMsg.getDividerSeqNo())) {
                    me = buffMember;
                    continue;
                }

                switch (buffMsg.getMoneyType()) {
                    case "point":
                        PointHistory pointHistory = new PointHistory();
                        pointHistory.setMemberSeqNo(buffMember.getMemberSeqNo());
                        pointHistory.setType("charge");
                        pointHistory.setPoint(amount);
                        pointHistory.setSubject("BUFF 적립");
                        pointHistory.setHistoryProp(new HashMap<>());
                        pointHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
                        pointHistoryList.add(pointHistory);
                        break;

                    case "bol":
                        User user = new User();
                        user.setNo(buffMember.getMemberSeqNo());
                        BolHistory bolHistory = new BolHistory();
                        bolHistory.setAmount(amount);
                        bolHistory.setUser(user);
                        bolHistory.setSubject("BUFF 적립");
                        bolHistory.setPrimaryType("increase");
                        bolHistory.setSecondaryType("buff");
                        bolHistory.setTargetType("member");
                        bolHistory.setTarget(user);
                        bolHistory.setProperties(new HashMap<>());
                        bolHistory.getProperties().put("지급처", "캐시픽 운영팀");
                        bolHistoryList.add(bolHistory);
                        break;
                }
            }

            if (me != null) {
                ParamMap map = null;

                switch (buffMsg.getMoneyType()) {
                    case "point":

                        map = new ParamMap();
                        map.put("list", buffMemberList);
                        map.put("amount", amount);
                        map.put("buffMemberSeqNo", me.getSeqNo());
                        map.put("buffSeqNo", me.getBuffSeqNo());
                        map.put("totalAmount", buffMsg.getAmount());
                        pointParamMapList.add(map);

                        break;

                    case "bol":

                        map = new ParamMap();
                        map.put("list", buffMemberList);
                        map.put("amount", amount);
                        map.put("buffMemberSeqNo", me.getSeqNo());
                        map.put("buffSeqNo", me.getBuffSeqNo());
                        map.put("totalAmount", buffMsg.getAmount());
                        bolParamMapList.add(map);

                        break;
                }
            }

            BuffDividedBolLog buffDividedBolLog = new BuffDividedBolLog();
            buffDividedBolLog.setBuffSeqNo(buffMsg.getBuffSeqNo());
            buffDividedBolLog.setMemberSeqNo(buffMsg.getDividerSeqNo());
            buffDividedBolLog.setMoneyType(buffMsg.getMoneyType());
            buffDividedBolLog.setType(buffMsg.getType());
            buffDividedBolLog.setAmount(buffMsg.getAmount());
            buffDividedBolLog.setEventSeqNo(buffMsg.getEventSeqNo());
            buffDividedBolLog.setShoppingSeqNo(buffMsg.getShoppingSeqNo());
            buffDividedBolLogList.add(buffDividedBolLog);


        }

        ParamMap buffParam = new ParamMap();

        if (buffDividedBolLogList.size() > 0) {

            buffParam.put("buffDividedBolLogList", buffDividedBolLogList);
        }

        if (pointParamMapList.size() > 0) {
            buffParam.put("pointParamMapList", pointParamMapList);
        }

        if (bolParamMapList.size() > 0) {
            buffParam.put("bolParamMapList", bolParamMapList);
        }


        if (pointHistoryList.size() > 0) {
            buffParam.put("buffPointHistoryList", pointHistoryList);
//            cashBolService.increasePointList(pointHistoryList);
        }

        if (bolHistoryList.size() > 0) {
            buffParam.put("buffBolHistoryList", bolHistoryList);
//            cashBolService.increaseBolList(bolHistoryList);
        }

        sqlSession.insert("Buff.buffDivide", buffParam);

        if (buffMsgList.size() > 0) {

            try {
                insertBuffPostList(buffMsgList);
            } catch (Exception e) {
                logger.error("insertBuffPost : " + e.toString());
            }

//            for (BuffMsg buffMsg : buffMsgList) {
//                try {
//                    insertBuffPost(buffMsg);
//                } catch (Exception e) {
//                    logger.error("insertBuffPost : " + e.toString());
//                }
//            }
        }

    }

    public void insertBuffPostList(List<BuffMsg> buffMsgList) {
        List<BuffPost> buffPostList = new ArrayList<>();
        for (BuffMsg buffMsg : buffMsgList) {
            BuffPost buffPost = new BuffPost();
            buffPost.setBuffSeqNo(buffMsg.getBuffSeqNo());
            buffPost.setMemberSeqNo(buffMsg.getDividerSeqNo());
            buffPost.setDivideAmount(buffMsg.getAmount());
            buffPost.setDivideType(buffMsg.getMoneyType());
            buffPost.setHidden(false);
            buffPost.setDeleted(false);
            buffPost.setThumbnail(buffMsg.getImage());
            if (buffMsg.getType().equals("event")) {
                buffPost.setTitle("이벤트 당첨");

                Event event = new Event();
                event.setNo(buffMsg.getEventSeqNo());
                event = eventService.get(event);

                buffPost.setContent("#" + event.getTitle());
                buffPost.setType("eventBuff");
                buffPost.setWinPrice(buffMsg.getWinPrice());


            } else if (buffMsg.getType().equals("eventGift")) {
                buffPost.setTitle("이벤트 당첨");
                buffPost.setContent(buffMsg.getTitle());
                buffPost.setType("eventGift");
            } else if (buffMsg.getType().equals("shopping")) {

                buffPost.setTitle("쇼핑적립");
                buffPost.setContent("#" + buffMsg.getTitle());
                buffPost.setType("productBuff");
                buffPost.setProductPriceSeqNo(buffMsg.getShoppingSeqNo());
                User user = userService.getUser(buffMsg.getDividerSeqNo());
                buffPost.setHidden(!(user.getBuffPostPublic() == null || user.getBuffPostPublic()));
            }

            buffPostList.add(buffPost);
        }
        if (buffPostList.size() > 0) {
            sqlSession.insert("Buff.insertBuffPostList", buffPostList);


        }
    }

    public void insertBuffPost(BuffMsg buffMsg) {
        BuffPost buffPost = new BuffPost();
        buffPost.setBuffSeqNo(buffMsg.getBuffSeqNo());
        buffPost.setMemberSeqNo(buffMsg.getDividerSeqNo());
        buffPost.setDivideAmount(buffMsg.getAmount());
        buffPost.setDivideType(buffMsg.getMoneyType());
        buffPost.setHidden(false);
        buffPost.setDeleted(false);
        buffPost.setThumbnail(buffMsg.getImage());
        if (buffMsg.getType().equals("event")) {
            buffPost.setTitle("이벤트 당첨");

            Event event = new Event();
            event.setNo(buffMsg.getEventSeqNo());
            event = eventService.get(event);

            buffPost.setContent("#" + event.getTitle());
            buffPost.setType("eventBuff");
            buffPost.setWinPrice(buffMsg.getWinPrice());


        } else if (buffMsg.getType().equals("eventGift")) {
            buffPost.setTitle("이벤트 당첨");
            buffPost.setContent(buffMsg.getTitle());
            buffPost.setType("eventGift");
        } else if (buffMsg.getType().equals("shopping")) {

            buffPost.setTitle("쇼핑적립");
            buffPost.setContent("#" + buffMsg.getTitle());
            buffPost.setType("productBuff");
            buffPost.setProductPriceSeqNo(buffMsg.getShoppingSeqNo());
            User user = userService.getUser(buffMsg.getDividerSeqNo());
            buffPost.setHidden(!(user.getBuffPostPublic() == null || user.getBuffPostPublic()));
        }

        sqlSession.insert("Buff.insertBuffPost", buffPost);

        if ((buffPost.getType().equals("productBuff") || buffPost.getType().equals("eventGift")) && !AppUtil.isEmpty(buffMsg.getImage())) {

            BuffPostImage buffPostImage = new BuffPostImage();
            buffPostImage.setArray(1);
            buffPostImage.setBuffPostSeqNo(buffPost.getSeqNo());
            buffPostImage.setImage(buffMsg.getImage());
            sqlSession.insert("Buff.insertBuffPostImage", buffPostImage);
        }
    }

    public void buffTest() throws ResultCodeException {
        List<BuffMsg> buffMsgList = new ArrayList<>();

        BuffMsg buffMsg = new BuffMsg();
        buffMsg.setBuffSeqNo(1L);
        buffMsg.setAmount(100f);
        buffMsg.setWinPrice(10000f);
        buffMsg.setDividerSeqNo(1021439L);
        buffMsg.setMoneyType("point");
        buffMsg.setType("event");
        buffMsg.setEventSeqNo(1015085L);
        buffMsg.setTitle("버프테스트 포스트");
        buffMsgList.add(buffMsg);

        buffMsg = new BuffMsg();
        buffMsg.setBuffSeqNo(1L);
        buffMsg.setAmount(100f);
        buffMsg.setWinPrice(10000f);
        buffMsg.setDividerSeqNo(1021439L);
        buffMsg.setMoneyType("point");
        buffMsg.setType("eventGift");
        buffMsg.setEventSeqNo(1015085L);
        buffMsg.setTitle("버프테스트 포스트");
        buffMsg.setImage("https://stg-prnumber.s3.ap-northeast-2.amazonaws.com/web/event/202212201545049_%EA%B2%BD%ED%92%88%EC%9D%B4%EB%AF%B8%EC%A7%80_4%EB%93%B1.jpg");
        buffMsgList.add(buffMsg);
        insertBuffPostList(buffMsgList);

    }


    public void deleteBuffMemberTest(Long seqNo) {
        sqlSession.delete("Buff.deleteBuffMember", seqNo);
    }

}
