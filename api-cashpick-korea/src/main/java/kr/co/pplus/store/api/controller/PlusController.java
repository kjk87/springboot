package kr.co.pplus.store.api.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.PlusService;
import kr.co.pplus.store.mvc.service.QueueService;
import kr.co.pplus.store.type.dto.MovePlusDto;
import kr.co.pplus.store.type.dto.MovePlusListDto;
import kr.co.pplus.store.type.dto.PlusGroupListDto;
import kr.co.pplus.store.type.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class PlusController extends RootController {
	
	@Autowired
	PlusService svc;
	
	@Autowired
	QueueService queueSvc;
	
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/insertGroup/**")
	public Map<String,Object> insertGroup(Session session, @RequestBody PlusGroup group) throws ResultCodeException {
		group.setDefaultGroup(false);
		return result(svc.insertGroup(session, group), "row", group);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/updateGroupName/**")
	public Map<String,Object> updateGroupName(Session session, PlusGroup group) throws ResultCodeException {
		return result(svc.updateGroupName(session, group), "row", group);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/updateGroupPriorityAll")
	public Map<String,Object> updateGroupPriorityAll(Session session, @RequestBody PlusGroupListDto groupList) {
		return result(svc.updateGroupPriorityAll(session, groupList.getGroupList()));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/deleteGroup/**")
	public Map<String,Object> deleteGroup(Session session, PlusGroup group) throws ResultCodeException {
		return result(svc.deleteGroup(session, group));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getGroupAll/**")
	public Map<String,Object> getGroupAll(Session session) {
		return result(200, "rows", svc.getGroupAll(session));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getGroup/**")
	public Map<String,Object> getGroup(PlusGroup group) {
		return result(200, "row", svc.getGroup(group));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/insert/**")
	public Map<String,Object> insert(Session session, @RequestBody Plus plus) throws ResultCodeException {
		return result(queueSvc.insertPlus(session, plus), "row", plus);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/delete/**")
	public Map<String,Object> delete(Session session, Plus plus) throws ResultCodeException {
		return result(svc.delete(session, plus));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/deleteByPage/**")
	public Map<String,Object> delete(Session session, Page page) throws ResultCodeException {
		return result(svc.deleteByPage(session, page));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/addToGroup/**")
	public Map<String,Object> addToGroup(Session session, PlusGroup group, Plus plus) throws ResultCodeException {
		return result(svc.addToGroup(group, plus));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/addListToGroup/**")
	public Map<String,Object> addListToGroup(Session session, @RequestBody MovePlusListDto data) throws ResultCodeException {
		return result(svc.addListToGroup(data.getGroup(), data.getPlusList()));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/removeFromGroup/**")
	public Map<String,Object> removeFromGroup(Session session, PlusGroup group, Plus plus) throws ResultCodeException {
		return result(svc.removeFromGroup(group, plus));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/removeListFromGroup/**")
	public Map<String,Object> removeListFromGroup(Session session, @RequestBody MovePlusListDto data) throws ResultCodeException {
		return result(svc.removeListFromGroup(data.getGroup(), data.getPlusList()));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/moveGroup/**")
	public Map<String,Object> moveGroup(Session session, MovePlusDto dto) throws ResultCodeException {
		return result(svc.moveGroup(dto.getPlus(), dto.getSrc(), dto.getDest()));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/moveGroupToGroup/**")
	public Map<String,Object> moveGroupToGroup(Session session, MovePlusDto dto) throws ResultCodeException {
		return result(svc.moveGroupToGroup(dto.getSrc(), dto.getDest()));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getOnlyPlus/**")
	public Map<String,Object> getOnlyPlus(Session session, Long pageSeqNo) {
		Plus plus = new Plus();
		plus.setNo(pageSeqNo);
		return result(200, "row", svc.getOnlyPlus(session, plus));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getCount/**")
	public Map<String,Object> getCount(Session session, PlusGroup group, SearchOpt opt) {
		return result(200, "row", svc.getCount(session, group, opt));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getList/**")
	public Map<String,Object> getList(Session session, PlusGroup group, SearchOpt opt) throws ResultCodeException {
		return result(200, "rows", svc.getList(session, group, opt));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getExcludeCount/**")
	public Map<String,Object> getExcludeCount(Session session, PlusGroup group, SearchOpt opt) {
		return result(200, "row", svc.getExcludeCount(session, group, opt));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getExcludeList/**")
	public Map<String,Object> getExcludeList(Session session, PlusGroup group, SearchOpt opt) throws ResultCodeException {
		return result(200, "rows", svc.getExcludeList(session, group, opt));
	}


//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/insertFan/**")
//	public Map<String,Object> insertFan(Session session, @RequestBody Plus plus) throws ResultCodeException {
//		return result(queueSvc.insertPlusFan(session, plus), "row", plus);
//	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/deleteFan/**")
	public Map<String,Object> deleteFan(Session session, Plus plus) throws ResultCodeException {
		return result(svc.deleteFan(session, plus));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/deleteByPageFan/**")
	public Map<String,Object> deleteByPageFan(Session session, Page page) throws ResultCodeException {
		return result(svc.deleteByPageFan(session, page));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getExistFan/**")
	public Map<String,Object> getExistFan(Session session, Page page) throws ResultCodeException {
		return result(svc.getExistFan(session, page));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/updateFan/**")
	public Map<String,Object> updateFan(Session session, Plus plus) throws ResultCodeException {
		return result(svc.updateByPageFan(session, plus));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/updatePushActivate/**")
	public Map<String,Object> updatePushActivate(Session session, Plus plus) throws ResultCodeException {
		return result(svc.updatePushActivate(plus));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/updatePlusGift/**")
	public Map<String,Object> updatePlusGift(Session session, Plus plus) throws ResultCodeException {
		return result(svc.updatePlusGift(plus));
	}
}
