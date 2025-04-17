package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.PageMenu;
import kr.co.pplus.store.api.jpa.service.PageMenuService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PageMenuController extends RootController {

    private Logger logger = LoggerFactory.getLogger(PageMenuController.class);

    @Autowired
    PageMenuService pageMenuService;

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageMenu/reg")
    public Map<String,Object> regPageMenu(Session session, @RequestBody PageMenu pageMenu) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageMenuService.regPageMenu(pageMenu));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageMenu/update")
    public Map<String,Object> updatePageMenu(Session session, @RequestBody PageMenu pageMenu) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageMenuService.updatePageMenu(pageMenu));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageMenu/getPageMenuListByPageSeqNo")
    public Map<String,Object> getPageMenuListByPageSeqNo(Session session, Long pageSeqNo, Pageable pageable) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageMenuService.getPageMenuListByPageSeqNo(pageSeqNo, pageable));
    }

    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageMenu/delete")
    public Map<String,Object> deletePageMenu(Session session, Long seqNo) throws ResultCodeException {
        return result(pageMenuService.deletePageMenu(seqNo));
    }
}
