package kr.co.pplus.store.api.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.jpa.model.CashBuy;
import kr.co.pplus.store.api.jpa.service.CashService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.CashBolService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CashController extends RootController {

	@Autowired
	CashBolService svc;

	@Autowired
	CashService cashService ;

//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/cash/giveCash/**")
//	public Map<String,Object> giveCash(Session session, @RequestBody CashHistory history) throws ResultCodeException {
//		return result(svc.giveCashBySession(session, history), "row", history);
//	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/cash/getHistoryTotalAmount/**")
	public Map<String,Object> getHistoryTotalAmount(Session session, SearchOpt opt) {
		return result(200, "row", svc.getCashHistoryTotalAmount(session, opt));
	}

	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/cash/getHistoryCount/**")
	public Map<String,Object> getHistoryCount(Session session, SearchOpt opt) {
		opt.setNo(session.getNo());
		return result(200, "row", svc.getCashHistoryCount(session, opt));
	}


	@PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/cash/insert")
	public Map<String, Object> insertCashBuy(Session session, @RequestBody CashBuy cashBuy) throws ResultCodeException {

		return result(cashService.insertCashBuy(session, cashBuy));

	}

	@GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/cash/logList")
	public Map<String, Object> getCashLogList(Session session, Pageable pageable, Long pageSeqNo, String type) throws ResultCodeException {

		return result(200, "row", cashService.getCashLogList(pageable, pageSeqNo, type));

	}

	@GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/cash/getCashHistoryList")
	public Map<String, Object> getCashHistoryList(Session session, Pageable pageable) throws ResultCodeException {

		return result(Const.E_SUCCESS, "row", cashService.getCashHistoryList(pageable, session.getNo()));

	}

	@GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/cash/getCashHistory")
	public Map<String, Object> getCashHistory(Session session, Long seqNo) throws ResultCodeException {

		return result(Const.E_SUCCESS, "row", cashService.getCashHistory(seqNo));

	}

	@SkipSessionCheck
	@GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/cash/getCashExchangeRateList")
	public Map<String, Object> getCashExchangeRateList(Session session) throws ResultCodeException {

		return result(Const.E_SUCCESS, "rows", cashService.getCashExchangeRateList());

	}

}
