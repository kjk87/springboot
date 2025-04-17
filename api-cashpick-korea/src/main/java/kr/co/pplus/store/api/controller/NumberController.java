package kr.co.pplus.store.api.controller;

import java.util.Map;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.NumberService;
import kr.co.pplus.store.type.model.Page;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.type.model.VirtualNumber;
import kr.co.pplus.store.util.StoreUtil;

@RestController
public class NumberController extends RootController {
	@Autowired
	NumberService svc;
	
	@Value("${STORE.FREE_NUMBER_PREFIX}")
	String FREE_NUMBER_PREFIX = "007";
	
	
	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/number/getPrefixNumber/**")
	public Map<String,Object> getPrefixNumber(Session session) {
		return result(200, "row", FREE_NUMBER_PREFIX);
	}
	
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/number/allocateVirtualNumberToPage/**")
	public Map<String,Object> allocateVirtualNumberToPage(Session session, Page page, VirtualNumber number) throws ResultCodeException {
		return result(svc.allocateVirtualNumberToPage(session, page, number, StoreUtil.getDefaultAllocateNumberDuration()));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/number/allocateActiveVirtualNumberToPage/**")
	public Map<String,Object> allocateActiveVirtualNumberToPage(Session session, Page page, VirtualNumber number) throws ResultCodeException {
		return result(svc.allocateActiveVirtualNumberToPage(session, page, number, StoreUtil.getDefaultAllocateNumberDuration()));
	}
}
