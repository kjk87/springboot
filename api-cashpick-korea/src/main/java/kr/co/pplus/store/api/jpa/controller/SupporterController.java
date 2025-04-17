package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.Supporter;
import kr.co.pplus.store.api.jpa.service.SupporterService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SupporterController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(SupporterController.class);

    @Autowired
    SupporterService supporterService;


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/supporter/getSupporter")
    public Map<String, Object> getSupporter(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", supporterService.getSupporter(session.getNo()));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/supporter/saveSupporter")
    public Map<String, Object> saveSupporter(Session session, @RequestBody Supporter supporter) throws ResultCodeException {
        supporter.setMemberSeqNo(session.getNo());
        return result(Const.E_SUCCESS, "row", supporterService.saveSupporter(supporter));

    }
}
