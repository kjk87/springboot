package kr.co.pplus.store.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.FanService;
import kr.co.pplus.store.type.dto.FanGroupListDto;
import kr.co.pplus.store.type.dto.MoveCustomerDto;
import kr.co.pplus.store.type.dto.MoveFanDto;
import kr.co.pplus.store.type.dto.MoveFanListDto;
import kr.co.pplus.store.type.model.CustomerGroup;
import kr.co.pplus.store.type.model.Fan;
import kr.co.pplus.store.type.model.FanGroup;
import kr.co.pplus.store.type.model.Page;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.Session;

@RestController
public class FanController extends RootController {
	@Autowired
	FanService svc;
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/insertGroup/**")
	public Map<String,Object> insertGroup(Session session, @RequestBody FanGroup group) throws ResultCodeException {
		group.setDefaultGroup(false);
		return result(svc.insertGroup(group), "row", group);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/updateGroupName/**")
	public Map<String,Object> updateGroupName(Session session, FanGroup group) throws ResultCodeException {
		return result(svc.updateGroupName(group), "row", group);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/updateGroupPriorityAll/**")
	public Map<String,Object> updateGroupPriority(Session session, @RequestBody FanGroupListDto groupList) {
		return result(svc.updateGroupPriorityAll(groupList.getGroupList()));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/deleteGroup/**")
	public Map<String,Object> deleteGroup(Session session, FanGroup group) throws ResultCodeException {
		return result(svc.deleteGroup(group), "row", group);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/getGroupAll/**")
	public Map<String,Object> getGroupAll(Session session, Page page) {
		return result(200, "rows", svc.getGroupAll(page));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/getGroup/**")
	public Map<String,Object> getGroup(Session session, FanGroup group) {
		return result(200, "rows", svc.getGroup(group));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/addToGroup/**")
	public Map<String,Object> addToGroup(Session session, FanGroup group, Fan fan) throws ResultCodeException {
		return result(svc.addToGroup(group, fan));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/addListToGroup/**")
	public Map<String,Object> addListToGroup(Session session, @RequestBody MoveFanListDto data) throws ResultCodeException {
		return result(svc.addListToGroup(data.getGroup(), data.getFanList()));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/removeFromGroup/**")
	public Map<String,Object> removeFromGroup(Session session, FanGroup group, Fan fan) throws ResultCodeException {
		return result(svc.removeFromGroup(group, fan));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/removeListFromGroup/**")
	public Map<String,Object> removeListFromGroup(Session session, @RequestBody MoveFanListDto data) throws ResultCodeException {
		return result(svc.removeListFromGroup(data.getGroup(), data.getFanList()));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/moveGroup/**")
	public Map<String,Object> moveGroup(Session session, MoveFanDto dto) throws ResultCodeException {
		return result(svc.moveGroup(dto.getFan(), dto.getSrc(), dto.getDest()));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/moveGroupToGroup/**")
	public Map<String,Object> moveGroupToGroup(Session session, MoveFanDto dto) throws ResultCodeException {
		return result(svc.moveGroupToGroup(dto.getSrc(), dto.getDest()));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/getCount/**")
	public Map<String,Object> getCount(Session session, FanGroup group, SearchOpt opt) {
		return result(200, "row", svc.getCount(session, group, opt));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/getList/**")
	public Map<String,Object> getList(Session session, FanGroup group, SearchOpt opt) throws ResultCodeException {
		return result(200, "rows", svc.getList(session, group, opt));
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/getExcludeCount/**")
	public Map<String,Object> getExcludeCount(Session session, Page page, FanGroup group, SearchOpt opt) {
		return result(200, "row", svc.getExcludeCount(session, page, group, opt));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/fan/getExcludeList/**")
	public Map<String,Object> getExcludeList(Session session, Page page, FanGroup group, SearchOpt opt) throws ResultCodeException {
		return result(200, "rows", svc.getExcludeList(session, page, group, opt));
	}
}
