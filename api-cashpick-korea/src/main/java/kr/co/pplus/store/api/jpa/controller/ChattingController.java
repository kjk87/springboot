//package kr.co.pplus.store.api.jpa.controller;
//
//import kr.co.pplus.store.api.controller.RootController;
//import kr.co.pplus.store.api.jpa.model.ChatData;
//import kr.co.pplus.store.api.jpa.service.ChattingService;
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.Session;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//public class ChattingController extends RootController {
//
//    private Logger logger = LoggerFactory.getLogger(ChattingController.class);
//
//    @Autowired
//    ChattingService chattingService;
//
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/chat/insert")
//    public Map<String,Object> insertChat(Session session, @RequestBody ChatData chatData) throws ResultCodeException {
//        return result(Const.E_SUCCESS, "row", chattingService.insertChat(chatData));
//    }
//
//}
