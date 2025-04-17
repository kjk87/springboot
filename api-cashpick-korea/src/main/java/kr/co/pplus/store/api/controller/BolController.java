package kr.co.pplus.store.api.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.pplus.store.mvc.service.AuthService;
import kr.co.pplus.store.mvc.service.CashBolService;
import kr.co.pplus.store.type.dto.BaseResponse;
import kr.co.pplus.store.type.model.BolHistory;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "BolController", description = "bol포인트 api")
public class BolController extends RootController {
	@Autowired
	CashBolService svc;
	
	@Autowired
	AuthService authSvc;


	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/bol/getHistoryCount/**")
	@ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	public Map<String,Object> getHistoryCount(Session session, SearchOpt opt) {
		return result(200, "row", svc.getBolHistoryCount(session, opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/bol/getHistoryList/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BolHistory.class))))
	public Map<String,Object> getHistoryList(Session session, SearchOpt opt) {
		return result(200, "rows", svc.getBolHistoryList(session, opt));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/bol/getHistoryWithTargetList/**")
	@ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BolHistory.class))))
	public Map<String,Object> getHistoryWithTargetList(Session session, SearchOpt opt) {
		return result(200, "rows", svc.getBolHistoryWithTargetList(session, opt));
	}
	
	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/bol/getHistoryWithTarget/**")
	@ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = BolHistory.class)))
	public Map<String,Object> getHistoryWithTarget(Session session, BolHistory history) {
		return result(200, "row", svc.getBolHistoryWithTarget(history));
	}

}
