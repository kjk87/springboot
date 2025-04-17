package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.LotteryService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LotteryController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(LotteryController.class);


    @Autowired
    LotteryService lotteryService;

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/getLottery")
    public Map<String, Object> getLottery() throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", lotteryService.getLottery());
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/getLotteryByLotteryRound")
    public Map<String, Object> getLotteryByLotteryRound(Integer lotteryRound) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", lotteryService.getLotteryByLotteryRound(lotteryRound));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/getLotteryRoundList")
    public Map<String, Object> getLotteryRoundList() throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", lotteryService.getLotteryRoundList());
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/getLotteryJoinCount")
    public Map<String, Object> getJoinCount(Session session, Long lotterySeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", lotteryService.getJoinCount(session.getNo(), lotterySeqNo));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/getJoinCountGroupByJoinType")
    public Map<String, Object> getJoinCountGroupByJoinType(Session session, Long lotterySeqNo) throws ResultCodeException {


        return result(Const.E_SUCCESS, "rows", lotteryService.getJoinCountGroupByJoinType(session.getNo(), lotterySeqNo));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/getMyLotteryJoinList")
    public Map<String, Object> getMyLotteryJoinList(Session session, Long lotterySeqNo, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", lotteryService.getMyLotteryJoinList(session.getNo(), lotterySeqNo, pageable));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/getLotteryWinConditionByLotterySeqNo")
    public Map<String, Object> getLotteryWinConditionByLotterySeqNo(Long lotterySeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", lotteryService.getLotteryWinConditionByLotterySeqNo(lotterySeqNo));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/getMyLotteryWinList")
    public Map<String, Object> getMyLotteryWinList(Session session, Long lotterySeqNo, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", lotteryService.getMyLotteryWinList(session.getNo(), lotterySeqNo, pageable));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/joinLottery")
    public Map<String, Object> joinLottery(Session session, Long lotterySeqNo, Integer count, String joinType) throws ResultCodeException {

        lotteryService.joinLottery(session.getNo(), lotterySeqNo, count, joinType);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/receiveLotteryWinner")
    public Map<String, Object> receiveLotteryWinner(Session session, Long lotteryWinnerSeqNo) throws ResultCodeException {

        lotteryService.receiveLotteryWinner(session.getNo(), lotteryWinnerSeqNo);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/totalReceiveLottoByMemberSeqNo")
    public Map<String, Object> totalReceiveLottoByMemberSeqNo(Session session, Long lotterySeqNo) throws ResultCodeException {

        lotteryService.totalReceiveLottoByMemberSeqNo(session.getNo(), lotterySeqNo);

        return result(Const.E_SUCCESS);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/lottery/getLottoTypeActiveWinCount")
    public Map<String, Object> getLottoTypeActiveWinCount(Session session, Long lotterySeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", lotteryService.getLottoTypeActiveWinCount(session.getNo(), lotterySeqNo));
    }

}
