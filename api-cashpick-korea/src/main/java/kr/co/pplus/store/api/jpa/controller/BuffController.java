package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.Buff;
import kr.co.pplus.store.api.jpa.model.BuffParam;
import kr.co.pplus.store.api.jpa.model.BuffPost;
import kr.co.pplus.store.api.jpa.service.BuffJpaService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BuffController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(BuffController.class);

    @Autowired
    BuffJpaService buffService;

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/getBuff")
    public Map<String, Object> getBuff(Session session, Long buffSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", buffService.getBuff(buffSeqNo));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/getBuffMember")
    public Map<String, Object> getBuffMember(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", buffService.getBuffMember(session.getNo()));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/getBuffPostList")
    public Map<String, Object> getBuffPostList(HttpServletRequest request, Session session, Long buffSeqNo, Pageable pageable) throws ResultCodeException {
        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);
        return result(Const.E_SUCCESS, "row", buffService.getBuffPostList(buffSeqNo, session.getNo(), pageable));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/getBuffPostLikeList")
    public Map<String, Object> getBuffPostLikeList(HttpServletRequest request, Session session, Long buffPostSeqNo, Pageable pageable) throws ResultCodeException {
        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);
        return result(Const.E_SUCCESS, "row", buffService.getBuffPostLikeList(buffPostSeqNo, session.getNo(), pageable));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/getBuffPostReplyList")
    public Map<String, Object> getBuffPostReplyList(HttpServletRequest request, Session session, Long buffPostSeqNo, Pageable pageable) throws ResultCodeException {
        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);
        return result(Const.E_SUCCESS, "row", buffService.getBuffPostReplyList(buffPostSeqNo, session.getNo(), pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/getBuffMemberList")
    public Map<String, Object> getBuffMemberList(HttpServletRequest request, Session session, Long buffSeqNo, Boolean includeMe, String search, Pageable pageable) throws ResultCodeException {
        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);

        if (includeMe == null) {
            includeMe = true;
        }
        return result(Const.E_SUCCESS, "row", buffService.getBuffMemberList(buffSeqNo, session.getNo(), includeMe, search, pageable));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/getBuffLogList")
    public Map<String, Object> getBuffLogList(Session session, Long buffSeqNo, String moneyType, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", buffService.getBuffLogList(buffSeqNo, moneyType, pageable));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/getRequestList")
    public Map<String, Object> getRequestList(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", buffService.getRequestList(session.getNo()));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/getRequestCount")
    public Map<String, Object> getRequestCount(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", buffService.getRequestCount(session.getNo()));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/insertBuffPost")
    public Map<String, Object> insertBuffPost(Session session, @RequestBody BuffPost buffPost) throws ResultCodeException {
        buffService.insertBuffPost(session.getNo(), buffPost);
        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/updateBuffPost")
    public Map<String, Object> updateBuffPost(Session session, @RequestBody BuffPost buffPost) throws ResultCodeException {
        buffService.updateBuffPost(session.getNo(), buffPost);
        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/deleteBuffPost")
    public Map<String, Object> deleteBuffPost(Session session, Long buffPostSeqNo) throws ResultCodeException {
        buffService.deleteBuffPost(session.getNo(), buffPostSeqNo);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/hiddenBuffPost")
    public Map<String, Object> hiddenBuffPost(Session session, Long buffPostSeqNo) throws ResultCodeException {
        buffService.hiddenBuffPost(session.getNo(), buffPostSeqNo);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/buffPostLike")
    public Map<String, Object> buffPostLike(Session session, Long buffPostSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", buffService.buffPostLike(session.getNo(), buffPostSeqNo));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/insertBuffPostReply")
    public Map<String, Object> insertBuffPostReply(Session session, Long buffPostSeqNo, String reply) throws ResultCodeException {

        buffService.insertBuffPostReply(session.getNo(), buffPostSeqNo, reply);
        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/modifyBuffPostReply")
    public Map<String, Object> modifyBuffPostReply(Session session, Long buffPostReplySeqNo, String reply) throws ResultCodeException {

        buffService.modifyBuffPostReply(session.getNo(), buffPostReplySeqNo, reply);
        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/deleteBuffPostReply")
    public Map<String, Object> deleteBuffPostReply(Session session, Long buffPostReplySeqNo) throws ResultCodeException {

        buffService.deleteBuffPostReply(session.getNo(), buffPostReplySeqNo);
        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/buffMake")
    public Map<String, Object> buffMake(Session session, @RequestBody Buff buff) throws ResultCodeException {
        buffService.buffMake(session.getNo(), buff);
        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/buffInvite")
    public Map<String, Object> buffInvite(Session session, @RequestBody BuffParam buffParam) throws ResultCodeException {
        buffService.buffInvite(session.getNo(), buffParam);
        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/changeBuffRequest")
    public Map<String, Object> changeBuffRequest(Session session, Long buffRequestSeqNo, String status) throws ResultCodeException {
        buffService.changeBuffRequest(session.getNo(), buffRequestSeqNo, status);
        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/changeBuffOwner")
    public Map<String, Object> changeBuffOwner(Session session, Long buffRequestSeqNo, Long ownerSeqNo) throws ResultCodeException {
        buffService.changeBuffOwner(session.getNo(), buffRequestSeqNo, ownerSeqNo);
        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/exitBuff")
    public Map<String, Object> exitBuff(Session session, Long buffRequestSeqNo) throws ResultCodeException {
        buffService.exitBuff(session.getNo(), buffRequestSeqNo);
        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/forcedExitBuff")
    public Map<String, Object> forcedExitBuff(Session session, @RequestBody BuffParam buffParam) throws ResultCodeException {
        buffService.forcedExitBuff(session.getNo(), buffParam);
        return result(Const.E_SUCCESS);

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buff/existBuffOwner")
    public Map<String, Object> existBuffOwner(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", buffService.existBuffOwner(session.getNo()));

    }

}
