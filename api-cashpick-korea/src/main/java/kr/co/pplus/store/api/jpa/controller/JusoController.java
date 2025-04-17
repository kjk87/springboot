package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.Juso;
import kr.co.pplus.store.api.jpa.service.CategoryService;
import kr.co.pplus.store.api.jpa.service.JusoService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidCardException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class JusoController extends RootController {

    private Logger logger = LoggerFactory.getLogger(JusoController.class);

    @Autowired
    JusoService jusoService;

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/juso/doList")
    public Map<String, Object> getDoList() throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", jusoService.getProvinceList());
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/juso/list")
    public Map<String, Object> getJusoList(String type, String value) throws ResultCodeException {

        try {

            List<Juso> jusoList = new ArrayList<>();
            if(type.equals("gu")){
                jusoList = jusoService.getGuList(value);
            }else if(type.equals("dong")){
                jusoList = jusoService.getDongList(value);
            }

            return result(Const.E_SUCCESS, "rows", jusoList);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/juso/list", "ERROR");
        }
    }
}
