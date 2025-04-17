package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.MemberAddress;
import kr.co.pplus.store.api.jpa.service.MemberService;
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
public class MemberController extends RootController {

    private Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    MemberService memberService;

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/attendance")
    public Map<String, Object> attendance(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", memberService.attendance(session));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/getMemberAttendance")
    public Map<String, Object> getMemberAttendance(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", memberService.getMemberAttendance(session));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/attendance2")
    public Map<String, Object> attendance2(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", memberService.attendance2(session));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/attendance3")
    public Map<String, Object> attendance3(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", memberService.attendance3(session));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/updateProfile")
    public Map<String, Object> updateProfile(Session session, String nickname, String gender, String birthday, String job, String regionCode, String region1, String region2, String region3) throws ResultCodeException {
        memberService.updateProfile(session, nickname, gender, birthday, job, regionCode, region1, region2, region3);
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/adRewardReset")
    public Map<String, Object> adRewardReset() throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", memberService.adRewardReset());
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/address/save")
    public Map<String, Object> saveMemberAddress(Session session, @RequestBody MemberAddress memberAddress) throws ResultCodeException {
        memberAddress.setMemberSeqNo(session.getNo());
        return result(Const.E_SUCCESS, "row", memberService.saveAddress(memberAddress));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/address/get")
    public Map<String, Object> getMemberAddress(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", memberService.getMemberAddress(session.getNo()));
    }


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/get")
    public Map<String, Object> getMemberBySeqNo(Session session, Long memberSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", memberService.getMemberBySeqNo(memberSeqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/updatePlusPush")
    public Map<String, Object> updatePlusPush(Session session, Boolean plusPush) throws ResultCodeException {
        memberService.updatePlusPush(session, plusPush);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/updateBuffPostPublic")
    public Map<String, Object> updateBuffPostPublic(Session session, Boolean buffPostPublic) throws ResultCodeException {
        memberService.updateBuffPostPublic(session, buffPostPublic);
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/login")
    public Map<String, Object> login(String loginId, String password, String appType) throws ResultCodeException {
        return memberService.login(loginId, password, appType);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/getInviteRewardList")
    public Map<String, Object> getInviteRewardList(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", memberService.getInviteRewardList(session));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/getInviteGiftList")
    public Map<String, Object> getInviteGiftList(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", memberService.getInviteGiftList());
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/member/requestInviteReward")
    public Map<String, Object> requestInviteReward(Session session, Long seqNo, String gift, Boolean isCash) throws ResultCodeException {
        memberService.requestInviteReward(session, seqNo, gift, isCash);
        return result(Const.E_SUCCESS);
    }
}
