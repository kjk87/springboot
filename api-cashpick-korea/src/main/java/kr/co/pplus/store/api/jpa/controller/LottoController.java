package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.Lotto;
import kr.co.pplus.store.api.jpa.repository.LottoRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.SqlException;
import kr.co.pplus.store.mvc.service.EventService;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class LottoController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(LottoController.class);

    @Autowired
    LottoRepository lottoRepository ;

    @Autowired
    EventService eventSvc ;

    @Autowired
    UserService userSvc ;

    @Value("${https://stage.prnumber.com/files/web/}")
    String storeUrl ;


    @CrossOrigin
    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/lotto")
    public Map<String,Object> selectLotto() throws ResultCodeException {

        try {
            Lotto lotto = lottoRepository.findBySeqNo(1L) ;
            return result(Const.E_SUCCESS, "row", lotto);
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new SqlException("[GET]/event/lotto ERROR", e);
        }
    }

}
