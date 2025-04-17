package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.NotificationBoxService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class NotificationBoxController extends RootController {

    private Logger logger = LoggerFactory.getLogger(NotificationBoxController.class);

    @Autowired
    NotificationBoxService notificationBoxService;

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/notificationBox/getNotificationBoxList")
    public Map<String,Object> getNotificationBoxList(Session session, Pageable pageable) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", notificationBoxService.getNotificationBoxList(session.getNo(), pageable));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/notificationBox/delete")
    public Map<String,Object> delete(Session session, Long seqNo) throws ResultCodeException {
        notificationBoxService.delete(session.getNo(), seqNo);
        return result(Const.E_SUCCESS);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/notificationBox/getBuffWithdrawNotification")
    public Map<String,Object> getBuffWithdrawNotification(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", notificationBoxService.getBuffWithdrawNotification(session.getNo()));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/notificationBox/read")
    public Map<String,Object> read(Session session, Long notificationBoxSeqNo) throws ResultCodeException {
        notificationBoxService.read(notificationBoxSeqNo);
        return result(Const.E_SUCCESS);
    }
}
