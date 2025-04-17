package kr.co.pplus.store.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.AuthService;
import kr.co.pplus.store.mvc.service.MsgService;
import kr.co.pplus.store.mvc.service.QueueService;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Msg;
import kr.co.pplus.store.type.model.MsgForPush;
import kr.co.pplus.store.type.model.MsgForSms;
import kr.co.pplus.store.type.model.MsgOnly;
import kr.co.pplus.store.type.model.SavedMsg;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.util.DateUtil;

@RestController
public class MsgController extends RootController {
	@Autowired
	MsgService svc;
	
	@Autowired
	AuthService authSvc;
	
	@Autowired
	QueueService queueSvc;
	
	@Autowired
    MsgProducer producer;
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/insertSmsMsg/**")
	public Map<String,Object> insertSmsMsg(Session session, @RequestBody MsgForSms msg) throws ResultCodeException {
		//현재 캐쉬를 반영하기 위해서 db 기반으로 캐쉬를 업데이트 한다.
		session = authSvc.getReloadSession(session);
		
		msg.setAuthor(session);
		msg.setInput("user");
		return result(queueSvc.insertSmsMsg(msg, msg.getTargetList()), "row", msg);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/insertPushMsg/**")
	public Map<String,Object> insertPushMsg(Session session, @RequestBody MsgForPush msg) throws ResultCodeException {
		//현재 캐쉬를 반영하기 위해서 db 기반으로 캐쉬를 업데이트 한다.
		session = authSvc.getReloadSession(session);
		
		msg.setAuthor(session);
		msg.setInput("user");
		return result(queueSvc.insertPushMsg(msg, msg.getTargetList()), "row", msg);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/sendNow/**")
	public Map<String,Object> sendNow(Session session, MsgOnly msg) throws ResultCodeException {
		Integer ret = svc.sendNow(session, msg);
		if (Const.E_SUCCESS.equals(ret)) {
			msg.setReserveDate(DateUtil.getDateAdd(DateUtil.getCurrentDate(), DateUtil.MINUTE, -1));
			producer.push(msg);
		}
		return result(ret);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/cancelSend/**")
	public Map<String,Object> cancelSend(Session session, MsgOnly msg) throws ResultCodeException {
		return result(svc.cancelSend(session, msg));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/getMsgCount/**")
	public Map<String,Object> getMsgCount(Session session, MsgOnly msg) {
		return result(200, "row", svc.getMsgCountByTypeAndStatus(session, msg));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/getMsgList/**")
	public Map<String,Object> getMsgList(Session session, MsgOnly msg, SearchOpt opt) {
		return result(200, "rows", svc.getMsgListByTypeAndStatus(session, msg, opt));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/getPushTargetCount/**")
	public Map<String,Object> getPushTargetCount(Session session, MsgOnly msg, SearchOpt opt) {
		msg.setType("push");
		return result(200, "row", svc.getMsgReceiverCount(msg, opt));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/getPushTargetList/**")
	public Map<String,Object> getPushTargetList(Session session, MsgOnly msg, SearchOpt opt) {
		msg.setType("push");
		Msg saved = svc.getMsgWithReceiverList(msg, opt);
		return result(200, "rows", saved.getTargetList());
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/getSmsTargetCount/**")
	public Map<String,Object> getSmsTargetCount(Session session, MsgOnly msg, SearchOpt opt) {
		msg.setType("sms");
		return result(200, "row", svc.getMsgReceiverCount(msg, opt));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/getSmsTargetList/**")
	public Map<String,Object> getSmsTargetList(Session session, MsgOnly msg, SearchOpt opt) {
		msg.setType("sms");
		Msg saved = svc.getMsgWithReceiverList(msg, opt);
		return result(200, "rows", saved.getTargetList());
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/getReservedMsgAll/**")
	public Map<String,Object> getReservedMsgAll(Session session, MsgOnly msg) {
		msg.setAuthor(session);
		return result(200, "rows", svc.getReservedMsgAll(msg));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/insertSavedMsg/**")
	public Map<String,Object> insertSavedMsg(Session session, @RequestBody SavedMsg msg) {
		msg.setUser(session);
		return result(svc.insertSavedMsg(msg), "row", msg);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/deleteSavedMsg/**")
	public Map<String,Object> deleteSavedMsg(Session session, SavedMsg msg) {
		msg.setUser(session);
		return result(svc.deleteSavedMsg(msg));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/getSavedMsgCount/**")
	public Map<String,Object> getSavedMsgCount(Session session) {
		return result(200, "row", svc.getSavedMsgCount(session));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/getSavedMsgList/**")
	public Map<String,Object> getSavedMsgList(Session session, SearchOpt opt) {
		return result(200, "rows", svc.getSavedMsgList(session, opt));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/readComplete/**")
	public Map<String,Object> readComplete(Session session, String no) {
//		svc.readComplete(session, msg);
		return result(200);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/getMsgCountInBox/**")
	public Map<String,Object> getMsgCountInBox(Session session) {
		return result(200, "row", svc.getMsgCountInBox(session, session.getDevice().getInstalledApp().getType()));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/getMsgListInBox/**")
	public Map<String,Object> getMsgListInBox(Session session, SearchOpt opt) throws ResultCodeException {
		return result(200, "rows", svc.getMsgListInBox(session, opt, session.getDevice().getInstalledApp().getType()));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/msg/deleteMsgInBox/**")
	public Map<String,Object> deleteMsgInBox(Session session, MsgOnly msg) throws ResultCodeException {
		return result(svc.deleteMsgInBox(session, msg));
	}
}
