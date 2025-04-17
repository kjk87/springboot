package kr.co.pplus.store.api.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.ContactService;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.dto.BaseResponse;
import kr.co.pplus.store.type.dto.ContactListDto;
import kr.co.pplus.store.type.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ContactController extends RootController {
	
	@Autowired
	ContactService svc;
	
	@Autowired
    MsgProducer producer;
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/updateList/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> updateList(Session session, @RequestBody ContactListDto contacts) {
		Integer ret = svc.updateContactList(session, contacts.getContactList(), contacts.getDeleteAll());
		/*if (ret.equals(Const.E_SUCCESS)) {
			ActionForAsync act = new ActionForAsync();
			act.setAction("remappingFriendByContactAll");
			act.setTarget(session);
			producer.push(act);
		}*/
		return result(ret);
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/deleteList/**")
	@ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> deleteList(Session session, @RequestBody ContactListDto contacts) throws ResultCodeException {
		Integer ret = svc.deleteContactList(session, contacts.getContactList());
		return result(ret);
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getContactAll/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Contact.class))))
	public Map<String,Object> getContactAll(Session session) {
		return result(200, "rows", svc.getContactAllByUser(session));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getFriendCount/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getFriendCount(Session session) {
		return result(200, "row", svc.getFriendCount(session));
	}
	
//	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getFriendList/**")
//	public Map<String,Object> getFriendList(Session session, SearchOpt opt) {
//		 return result(200, "rows", svc.getFriendList(session, opt));
//	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getAllFriendCount/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getAllFriendCount(Session session) {
		return result(200, "row", svc.getAllFriendCount(session));
	}


	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getAllFriendList/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Contact.class))))
	public Map<String,Object> getAllFriendList(Session session, SearchOpt opt) {
		return result(200, "rows", svc.getAllFriendList(session, opt));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getUserFriendCount/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getUserFriendCount(Session session) {
		return result(200, "row", svc.getUserFriendCount(session));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getUserFriendList/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Contact.class))))
	public Map<String,Object> getUserFriendList(Session session, SearchOpt opt) {
		return result(200, "rows", svc.getUserFriendList(session, opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getExistsNicknameFriendCount/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getExistsNicknameFriendCount(Session session, SearchOpt opt) {
		return result(200, "row", svc.getExistsNicknameFriendCount(session, opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getExistsNicknameFriendList/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Contact.class))))
	public Map<String,Object> getExistsNicknameFriendList(Session session, SearchOpt opt) {
		 return result(200, "rows", svc.getExistsNicknameFriendList(session, opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getFriendPageCount/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getFriendPageCount(Session session, User user, SearchOpt opt) {
		return result(200, "row", svc.getFriendPageCount(user, opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getFriendPageList/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Page.class))))
	public Map<String,Object> getFriendPageList(Session session, User user, SearchOpt opt) {
		 return result(200, "rows", svc.getFriendPageList(user, opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/contact/getSameFriendAll/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class))))
	public Map<String,Object> getSameFriendAll(Session session, User user) {
		 return result(200, "rows", svc.getSameFriendAll(session, user));
	}	
}
