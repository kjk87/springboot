package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.QrCodeService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class QrodeController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(QrodeController.class);

    @Autowired
    QrCodeService qrCodeService ;


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/qr/getQrCodeByPagSeqNo")
    public Map<String, Object> getQrCodeByPagSeqNo(Session session, Long pageSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", qrCodeService.getQrCodeByPagSeqNo(pageSeqNo));

    }

}
