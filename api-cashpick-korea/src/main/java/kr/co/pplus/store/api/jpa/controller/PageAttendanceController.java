package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.PageAttendanceService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PageAttendanceController extends RootController {

    private Logger logger = LoggerFactory.getLogger(PageAttendanceController.class);

    @Autowired
    PageAttendanceService pageAttendanceService;

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageAttendance/attendanceWithStamp")
    public Map<String,Object> attendanceWithStamp(Session session, Long pageAttendanceSeqNo, Long pageSeqNo, String token) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", pageAttendanceService.attendanceWithStamp(session, pageAttendanceSeqNo, pageSeqNo, token));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageAttendance/attendance")
    public Map<String,Object> attendance(Session session, Long pageAttendanceSeqNo, Long memberSeqNo, Long pageSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", pageAttendanceService.attendanceFromPage(session, pageAttendanceSeqNo, memberSeqNo, pageSeqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageAttendance/saveAndGet")
    public Map<String,Object> saveAndGet(Session session, Long pageSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", pageAttendanceService.saveAndGet(session.getNo(), pageSeqNo));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageAttendance/getPageAttendanceLogList")
    public Map<String,Object> getPageAttendanceLogList(Session session, Long pageAttendanceSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", pageAttendanceService.getPageAttendanceLogList(pageAttendanceSeqNo));
    }

}
