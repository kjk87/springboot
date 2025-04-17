package kr.co.pplus.store.api.jpa.controller;

import com.google.api.Http;
import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.Attachment;
import kr.co.pplus.store.api.jpa.repository.AttachmentRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidArgumentException;
import kr.co.pplus.store.exception.InvalidGoodsException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

@RestController
public class AttachmentController extends RootController {

    private Logger logger = LoggerFactory.getLogger(AttachmentController.class);

    @Autowired
    AttachmentRepository attachmentRepository ;

    @Value("${spring.profiles.active}")
    String activeSpringProfile = "local" ;

    @SkipSessionCheck
    @GetMapping(value = baseUri+"/attachment/image")
    public void getAttachmentImage(HttpServletResponse response, @RequestParam(value = "id", required = true) String id) throws ResultCodeException {

        try {
            Attachment attachment = attachmentRepository.findById(id) ;
            response.sendRedirect(attachment.getUrl()) ;

        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
        }
    }

}
