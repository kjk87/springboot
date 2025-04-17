//package kr.co.pplus.store.api.controller;
//
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.mvc.service.AuthService;
//import kr.co.pplus.store.mvc.service.CashBolService;
//import kr.co.pplus.store.mvc.service.EventService;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.Session;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import java.util.Map;
//
//@RestController
//public class TaskController extends RootController {
//	@Autowired
//	CashBolService svc;
//
//	@Autowired
//	AuthService authSvc;
//
//	@Autowired
//	EventService eventSvc;
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/task/lot")
//	public Map<String,Object> lot(Session session, @RequestParam(value = "code", required = true) String code, Map<String, Object> map) throws ResultCodeException {
//
//		try {
//
////			if (session.getLoginId().equals("sysadmin")) {
////				eventSvc.lotExpiredEventAllWhen(code);
////			} else {
////				throw new Exception("/task/lot : parameter error");
////			}
//
////			eventSvc.lotExpiredEventAllWhen(code);
//
//			return result(Const.E_SUCCESS, "row", null);
//		} catch (Exception e) {
//			map.put("errorMessage", e.getMessage()) ;
//			throw new ResultCodeException(500, map);
//		}
//	}
//}
