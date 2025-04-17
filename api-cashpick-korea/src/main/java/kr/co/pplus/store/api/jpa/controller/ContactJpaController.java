package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.ContactJpaService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ContactJpaController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(ContactJpaController.class);

    @Autowired
    ContactJpaService contactService ;


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/contact/getContactListWithMember")
    public Map<String, Object> getContactListWithMember(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", contactService.getContactListWithMember(session.getNo(), pageable));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/contact/getFriendList")
    public Map<String, Object> getFriendList(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", contactService.getFriendList(session.getNo(), pageable));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/contact/getContactMemberCount")
    public Map<String, Object> getContactMemberCount(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", contactService.getContactMemberCount(session.getNo()));

    }

}
