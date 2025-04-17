package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.PushMsgService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidProductException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PushMsgController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(PushMsgController.class);

    @Autowired
    PushMsgService pushMsgService;


    @GetMapping(value = baseUri + "/push/msg/listByPageSeqNo")
    public Map<String, Object> getPushMsgListByPageSeqNo(Session session, Pageable pageable, @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo) throws ResultCodeException {
        try {


            return result(Const.E_SUCCESS, "row", pushMsgService.getPushMsgListByPageSeqNo(pageSeqNo, pageable));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/push/msg/listByPageSeqNo", "getPushMsgListByPageSeqNo ERROR");
        }

    }

}
