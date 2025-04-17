package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.VirtualNumberService;
import kr.co.pplus.store.exception.ResultCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class VirtualNumberController extends RootController {

    private Logger logger = LoggerFactory.getLogger(VirtualNumberController.class);

    @Autowired
    VirtualNumberService virtualNumberService;

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/virtualNumber/getVirtualNumberManage")
    public Map<String,Object> getVirtualNumberManage(String virtualNumber) throws ResultCodeException {
        return result(200, "row", virtualNumberService.getVirtualNumberManage(virtualNumber));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/virtualNumber/getNbookVirtualNumberManageList")
    public Map<String,Object> getNbookVirtualNumberManageList() throws ResultCodeException {
        return result(200, "rows", virtualNumberService.getNbookVirtualNumberManageList());
    }
}
