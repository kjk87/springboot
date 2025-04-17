package kr.co.pplus.store.api.controller;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.CustomerService;
import kr.co.pplus.store.type.model.Customer;
import kr.co.pplus.store.type.model.CustomerGroup;
import kr.co.pplus.store.type.model.Page;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.Session;

@RestController
public class CustomerController extends RootController {
	
	@Autowired
	CustomerService svc;
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/insertGroup/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = CustomerGroup.class)))
	public Map<String,Object> insertGroup(Session session, @RequestBody CustomerGroup group) throws ResultCodeException {
		group.setDefaultGroup(false);
		return result(svc.insertGroup(group), "row", group);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/updateGroupPriorityAll")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updateGroupPriorityAll(Session session, @RequestBody CustomerGroupListDto groupList) {
		return result(svc.updateGroupPriorityAll(groupList.getGroupList()));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/updateGroupName/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = CustomerGroup.class)))
	public Map<String,Object> updateGroupName(Session session, CustomerGroup group) throws ResultCodeException {
		return result(svc.updateGroupName(group), "row", group);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/deleteGroup/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> deleteGroup(Session session, CustomerGroup group) throws ResultCodeException {
		return result(svc.deleteGroup(group));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/getGroupAll/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomerGroup.class))))
	public Map<String,Object> getGroupAll(Session session, Page page) {
		return result(Const.E_SUCCESS, "rows", svc.getCustomerGroupAll(page));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/getGroup/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = CustomerGroup.class)))
	public Map<String,Object> getGroup(Session session, CustomerGroup group) {
		return result(Const.E_SUCCESS, "row", svc.getCustomerGroup(group));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/insertCustomerList/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = CustomerGroup.class)))
	public Map<String,Object> insertCustomerList(Session session, @RequestBody CustomerListDto dto)  throws ResultCodeException {
		return result(svc.insertCustomerList(dto.getPage(), dto.getCustomerList()), "row", dto);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/existsCustomerByMobile/**")
	@ApiResponse(responseCode = "200", description = "row : boolean", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> existsCustomerByMobile(Session session, Customer customer) throws ResultCodeException {
		return result(Const.E_SUCCESS, "row", svc.existsCustomerByMobile(customer));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/getCustomerByMobile/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Customer.class)))
	public Map<String,Object> getCustomerByMobile(Session session, Customer customer) throws ResultCodeException {
		return result(Const.E_SUCCESS, "row", svc.getCustomerByMobile(customer));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/insertCustomer/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Customer.class)))
	public Map<String,Object> insertCustomer(Session session, @RequestBody Customer customer) throws ResultCodeException {
		return result(svc.insertCustomer(customer), "row", customer);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/updateCustomer/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Customer.class)))
	public Map<String,Object> updateCustomer(Session session, @RequestBody Customer customer) throws ResultCodeException {
		return result(svc.updateCustomer(customer), "row", customer);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/deleteCustomer/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> deleteCustomer(Session session, Customer customer) throws ResultCodeException {
		return result(svc.deleteCustomer(session, customer));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/addCustomerToGroup/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> addCustomerToGroup(Session session, CustomerGroup group, Customer customer) throws ResultCodeException {
		return result(svc.addCustomerToGroup(group, customer));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/addCustomerListToGroup/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> addCustomerListToGroup(Session session, @RequestBody MoveCustomerListDto data) throws ResultCodeException {
		return result(svc.addCustomerListToGroup(data.getGroup(), data.getCustomerList()));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/removeCustomerFromGroup/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> removeCustomerFromGroup(Session session, CustomerGroup group, Customer customer) throws ResultCodeException {
		return result(svc.removeCustomerFromGroup(group, customer));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/removeCustomerListFromGroup/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> removeCustomerListFromGroup(Session session, @RequestBody MoveCustomerListDto data) throws ResultCodeException {
		return result(svc.removeCustomerListFromGroup(data.getGroup(), data.getCustomerList()));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/moveCustomerGroup/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> moveCustomerGroup(Session session, MoveCustomerDto dto) throws ResultCodeException {
		return result(svc.moveCustomerGroup(dto.getCustomer(), dto.getSrc(), dto.getDest()));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/moveGroupToGroup/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> moveGroupToGroup(Session session, MoveCustomerDto dto) throws ResultCodeException {
		return result(svc.moveGroupToGroup(dto.getSrc(), dto.getDest()));
	}

	@GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/getCustomerCountByPageSeqNo/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getCustomerCount(Session session, Long pageSeqNo) {
		return result(Const.E_SUCCESS, "row", svc.getCustomerCountByPageSeqNo(pageSeqNo));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/getCustomerCount/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getCustomerCount(Session session, CustomerGroup group, SearchOpt opt) {
		return result(Const.E_SUCCESS, "row", svc.getCustomerCount(session, group, opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/getCustomerList/**")
	public Map<String,Object> getCustomerList(Session session, CustomerGroup group, SearchOpt opt) throws ResultCodeException {
		return result(Const.E_SUCCESS, "rows", svc.getCustomerList(session, group, opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/getUserCustomerCount/**")
	public Map<String,Object> getUserCustomerCount(Session session, CustomerGroup group, SearchOpt opt) {
		return result(Const.E_SUCCESS, "row", svc.getUserCustomerCount(session, group, opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/getUserCustomerList/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class))))
	public Map<String,Object> getUserCustomerList(Session session, CustomerGroup group, SearchOpt opt) throws ResultCodeException {
		return result(Const.E_SUCCESS, "rows", svc.getUserCustomerList(session, group, opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/getExcludeCustomerCount/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getExcludeCustomerCount(Session session, Page page, CustomerGroup group, SearchOpt opt) {
		return result(Const.E_SUCCESS, "row", svc.getExcludeCustomerCount(session, page, group, opt));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/customer/getExcludeCustomerList/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class))))
	public Map<String,Object> getExcludeCustomerList(Session session, Page page, CustomerGroup group, SearchOpt opt) throws ResultCodeException {
		return result(Const.E_SUCCESS, "rows", svc.getExcludeCustomerList(session, page, group, opt));
	}
}
