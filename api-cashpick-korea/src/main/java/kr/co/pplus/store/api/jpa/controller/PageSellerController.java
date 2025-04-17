package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.Count;
import kr.co.pplus.store.api.jpa.model.PageSeller;
import kr.co.pplus.store.api.jpa.repository.PageRepository;
import kr.co.pplus.store.api.jpa.repository.PageSellerRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidPageSellerException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.CashBolService;
import kr.co.pplus.store.mvc.service.PageService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.CashLog;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.type.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
public class PageSellerController extends RootController {

    private Logger logger = LoggerFactory.getLogger(PageSellerController.class);

    Float payRatio = 0.5f ;

    @Value("${STORE.BIZ_JOIN_CASH}")
    long bizJoinCash ;

    @Autowired
    PageSellerRepository pageSellerRepository ;

    @Autowired
    PageRepository pageRepository ;

    @Autowired
    CashBolService cashBolSvc ;

    @Autowired
    PageService pageSvc ;


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageSeller")
    //@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidPageSellerException.class)
    public Map<String,Object> addPageSeller(Session session, @RequestBody PageSeller pageSeller) throws ResultCodeException {


        try {
            String dateStr = AppUtil.localDatetimeNowString() ;
            if( pageSeller.getIsSeller() == null )
                pageSeller.setIsSeller(false);
            if( pageSeller.getIsTermsAccept() == null )
                pageSeller.setIsTermsAccept(false);
            if( pageSeller.getBizPayRatio() == null )
                pageSeller.setBizPayRatio(payRatio);
            //ToDo 일반 사용자용 api 별도 필요함.
            pageSeller.setStatus(1) ; // 상점 페이지 자동 승인 완료
            pageSeller.setRegDatetime(dateStr);
            pageSeller.setModDatetime(dateStr);
            pageSeller = pageSellerRepository.saveAndFlush(pageSeller);
            if( pageSeller == null )
                throw new InvalidPageSellerException("[POST]/pageSeller[POST]", "save Error") ;

            pageSvc.updateSeller(pageSeller.getPageSeqNo(), true);

            User user = new User() ;
            user.setNo(session.getNo()) ;
            CashLog cashLog = new CashLog();
            cashLog.setCash((int)bizJoinCash);
            cashLog.setNote("비즈 회원가입 무료 캐쉬 충전");
            cashBolSvc.increaseCash(user, cashLog);
            return result(Const.E_SUCCESS, "row", pageSeller);
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new InvalidPageSellerException("[POST]/pageSeller[POST]", e) ;
        }
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageSeller")
    //@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidPageSellerException.class)
    public Map<String,Object> updatePageSeller(Session session, @RequestBody PageSeller pageSeller) throws ResultCodeException {


        try {
            String dateStr = AppUtil.localDatetimeNowString() ;
            pageSeller.setModDatetime(dateStr);
            pageSeller = pageSellerRepository.saveAndFlush(pageSeller);
            if( pageSeller == null )
                throw new InvalidPageSellerException("[PUT]/pageSeller[PUT]", "update error") ;
            return result(Const.E_SUCCESS, "row", pageSeller);
        }
        catch(InvalidPageSellerException e) {
            logger.error(AppUtil.excetionToString(e)) ;
            throw e ;
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new InvalidPageSellerException("[PUT]/pageSeller[PUT]", e) ;
        }
    }

    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageSeller")
    //@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidPageSellerException.class)
    public Map<String,Object> deletePageSeller(Session session,
                                      @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
                                      @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo) throws ResultCodeException {
        try {
            if( pageSeqNo != null )
                pageSellerRepository.deleteByPageSeqNo(pageSeqNo);
            else if( memberSeqNo != null )
                pageSellerRepository.deleteAllByMemberSeqNo(memberSeqNo);
            return result(Const.E_SUCCESS, "row", null);
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new InvalidPageSellerException("/pageSeller[DELETE]", e) ;
        }
    }

    @GetMapping(value = baseUri+"/pageSeller")
    public Map<String,Object> selectPageSeller(Session session, Pageable pageable,
                                     @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
                                     @RequestParam(value = "pageSeqNo", required = false)Long pageSeqNo,
                                     @RequestParam(value = "isSeller", required = false)Boolean isSeller,
                                     @RequestParam(value = "status", required = false)Integer status,
                                     @RequestParam(value = "isTermsAccept", required = false)Boolean isTermsAccept) throws ResultCodeException {
        Page<PageSeller> page = null ;
        try {

            if (pageSeqNo != null) {
                PageSeller pageSeller = pageSellerRepository.findByPageSeqNo(memberSeqNo) ;
                return result(Const.E_SUCCESS, "row", pageSeller);
            }
            else if (memberSeqNo != null) {

                if( isSeller != null && isTermsAccept != null){
                    page = pageSellerRepository.findAllByMemberSeqNoAndIsSellerAndIsTermsAccept(memberSeqNo, isSeller, isTermsAccept, pageable) ;
                }
                else if( isSeller != null ){
                    page = pageSellerRepository.findAllByMemberSeqNoAndIsSeller(memberSeqNo, isSeller, pageable) ;
                }
                else if( isTermsAccept != null ){
                    page = pageSellerRepository.findAllByMemberSeqNoAndIsTermsAccept(memberSeqNo, isTermsAccept, pageable) ;
                }
                else {
                    page = pageSellerRepository.findAllByMemberSeqNo(memberSeqNo, pageable);
                    return result(Const.E_SUCCESS, "row", page);
                }
            }
            else {

                if( isSeller != null && isTermsAccept != null){
                    page = pageSellerRepository.findAllByIsSellerAndIsTermsAccept(isSeller, isTermsAccept, pageable) ;
                }
                else if( isSeller != null ){
                    page = pageSellerRepository.findAllByIsSeller(isSeller, pageable) ;
                }
                else if( isTermsAccept != null ){
                    page = pageSellerRepository.findAllByIsTermsAccept(isTermsAccept, pageable) ;
                }
                else {
                    page = pageSellerRepository.findAllBy(pageable);

                }
            }
            return result(Const.E_SUCCESS, "row", page);
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new InvalidPageSellerException("/pageSeller[GET]", e) ;
        }
    }

    @CrossOrigin
    @GetMapping(value = baseUri+"/pageSeller/detail")
    public Map<String,Object> selectPageSellerDetail(Session session, Pageable pageable,
                                           @RequestParam(value = "memberSeqNo", required = false)Long memberSeqNo,
                                           @RequestParam(value = "pageSeqNo", required = false)Long pageSeqNo,
                                           @RequestParam(value = "isSeller", required = false)Boolean isSeller,
                                           @RequestParam(value = "status", required = false)Integer status,
                                           @RequestParam(value = "searchType", required = false) String searchType,
                                           @RequestParam(value = "searchWord", required = false) String searchWord,
                                           @RequestParam(value = "searchStartDate", required = false) String searchStartDate,
                                           @RequestParam(value = "searchEndDate", required = false) String searchEndDate,
                                           @RequestParam(value = "approvalWait", required = false) Boolean approvalWait,
                                           @RequestParam(value = "approval", required = false) Boolean approval,
                                           @RequestParam(value = "reject", required = false) Boolean reject,
                                           @RequestParam(value = "secondRequest", required = false) Boolean secondRequest,
                                           @RequestParam(value = "stop", required = false) Boolean stop,
                                           @RequestParam(value = "isTermsAccept", required = false)Boolean isTermsAccept) throws ResultCodeException {
        Page<PageSeller> page = null ;
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date startDate = null;
            if (searchStartDate != null && !searchStartDate.trim().isEmpty())
                try {
                    startDate = sdf.parse(searchStartDate + " 00:00:00.000");
                } catch (Exception ee) {
                }
            Date endDate = null;
            if (searchEndDate != null && !searchEndDate.trim().isEmpty())
                try {
                    endDate = sdf.parse(searchEndDate + " 23:59:59.999");
                } catch (Exception ee) {
                }

            if (searchWord != null && !searchWord.trim().isEmpty())
                searchWord = '%' + searchWord + '%';
            else
                searchWord = null;

            page = pageSellerRepository.findAllBy(memberSeqNo, pageSeqNo, isSeller, status,
                    searchType, searchWord, startDate, endDate,
                    approvalWait, approval, reject, secondRequest, stop, isTermsAccept, pageable) ;
            return result(Const.E_SUCCESS, "row", page);
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new InvalidPageSellerException("/pageSeller[GET]", e) ;
        }
    }


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageSeller/count")
    public Map<String,Object> countPageSeller(Session session, Pageable pageable,
                                    @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
                                    @RequestParam(value = "pageSeqNo", required = false)Long pageSeqNo,
                                    @RequestParam(value = "isSeller", required = false)Boolean isSeller,
                                    @RequestParam(value = "status", required = false)Integer status,
                                    @RequestParam(value = "isTermsAccept", required = false)Boolean isTermsAccept) throws ResultCodeException {
        Integer count = null ;
        try {

            if (pageSeqNo != null) {
                count = pageSellerRepository.countByPageSeqNo(memberSeqNo) ;
            }
            else if (memberSeqNo != null) {
                if( isSeller != null && isTermsAccept != null){
                    count = pageSellerRepository.countAllByMemberSeqNoAndIsSellerAndIsTermsAccept(memberSeqNo, isSeller, isTermsAccept) ;
                }
                else if( isSeller != null ){
                    count = pageSellerRepository.countAllByMemberSeqNoAndIsSeller(memberSeqNo, isSeller) ;
                }
                else if( isTermsAccept != null ){
                    count = pageSellerRepository.countAllByMemberSeqNoAndIsTermsAccept(memberSeqNo, isTermsAccept) ;
                }
                else {
                    count = pageSellerRepository.countAllByMemberSeqNo(memberSeqNo);
                }
            }
            else {

                if( isSeller != null && isTermsAccept != null){
                    count = pageSellerRepository.countAllByIsSellerAndIsTermsAccept(isSeller, isTermsAccept) ;
                }
                else if( isSeller != null ){
                    count = pageSellerRepository.countAllByIsSeller(isSeller) ;
                }
                else if( isTermsAccept != null ){
                    count = pageSellerRepository.countAllByIsTermsAccept(isTermsAccept) ;
                }
                else {
                    count = pageSellerRepository.countAllBy();
                }
            }

            return result(Const.E_SUCCESS, "row", new Count(count));
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new InvalidPageSellerException("/pageSeller[GET]", e) ;
        }
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageSeller/terms")
    public ModelAndView countPageSeller(Session session) throws ResultCodeException {
        try {

            ModelAndView model = new ModelAndView() ;
            model.setViewName("seller/terms");
            return model ;
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new InvalidPageSellerException("/pageSeller[GET]", e) ;
        }
    }
}
