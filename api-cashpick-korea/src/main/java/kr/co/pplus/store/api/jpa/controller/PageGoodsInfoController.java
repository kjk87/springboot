//package kr.co.pplus.store.api.jpa.controller;
//
//import kr.co.pplus.store.StoreApplication;
//import kr.co.pplus.store.api.annotation.SkipSessionCheck;
//import kr.co.pplus.store.api.controller.RootController;
//import kr.co.pplus.store.api.jpa.model.*;
//import kr.co.pplus.store.api.jpa.repository.*;
//import kr.co.pplus.store.api.util.AppUtil;
//import kr.co.pplus.store.exception.*;
//import kr.co.pplus.store.mvc.service.PageService;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.Session;
//import kr.co.pplus.store.util.RedisUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.transaction.annotation.Isolation;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//public class PageGoodsInfoController extends RootController {
//
//    private Logger logger = LoggerFactory.getLogger(PageGoodsInfoController.class);
//
//    @Autowired
//    PageGoodsInfoRepository pageGoodsInfoRepository ;
//
//    @Autowired
//    PageService pageService;
//
//    @Value("${STORE.REDIS_PREFIX}")
//    String REDIS_PREFIX = "pplus-";
//
//
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/goodsInfo")
//    public Map<String,Object> addPageGoodsInfo(Session session, @RequestBody PageGoodsInfo pageGoodsInfo) throws ResultCodeException {
//
//        return result(Const.E_SUCCESS, "row", pageService.addPageGoodsNoticeInfo(pageGoodsInfo));
//
////        try {
////
////            String dateStr = AppUtil.localDatetimeNowString() ;
////
////            pageGoodsInfo.setSeqNo(null);
////            pageGoodsInfo.setRegDatetime(dateStr);
////            pageGoodsInfoRepository.saveAndFlush(pageGoodsInfo) ;
////
////
////
////        }
////        catch(Exception e){
////            logger.error(AppUtil.excetionToString(e)) ;
////            throw new InvalidPageGoodsInfoException("[POST]" + this.getClass().getCanonicalName(), e);
////        }
//    }
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/goodsInfo")
//    public Map<String,Object> updatePageGoodsInfo(Session session, @RequestBody PageGoodsInfo pageGoodsInfo) throws ResultCodeException {
//
//        return result(Const.E_SUCCESS, "row", pageService.updatePageGoodsNoticeInfo(pageGoodsInfo));
////        try {
////
////            String dateStr = AppUtil.localDatetimeNowString() ;
////
////
////            pageGoodsInfo.setRegDatetime(dateStr);
////            pageGoodsInfoRepository.saveAndFlush(pageGoodsInfo) ;
////
////
////            return result(Const.E_SUCCESS, "row", pageGoodsInfo);
////        }
////        catch(Exception e){
////            logger.error(AppUtil.excetionToString(e)) ;
////            throw new InvalidPageGoodsInfoException("[PUT]" + this.getClass().getCanonicalName(), e);
////        }
//    }
//
//
//
//    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/goodsInfo")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidPageGoodsCategoryException.class)
//    public Map<String,Object> deletePageGoodsInfo(Session session,
//                                        @RequestParam(value="pageSeqNo", required=true) Long pageSeqNo,
//                                        @RequestParam(value="goodsSeqNo", required=true) Long goodsSeqNo) throws ResultCodeException {
//        try {
//
//            pageGoodsInfoRepository.deleteByPageSeqNoAndGoodsSeqNo(pageSeqNo, goodsSeqNo);
//        }
//        catch(Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            throw new InvalidPageGoodsInfoException("[DELETE]" + this.getClass().getCanonicalName(), e);
//        }
//        return result(Const.E_SUCCESS, "row", null);
//    }
//
//    @SkipSessionCheck
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/goodsInfo")
//    public Map<String,Object> selectPageGoodsInfo(Session session,
//                                         @RequestParam(value="pageSeqNo", required=true) Long pageSeqNo,
//                                         @RequestParam(value="goodsSeqNo", required=true) Long goodsSeqNo) throws ResultCodeException {
//
//        try {
//
//            PageGoodsInfo  info = pageGoodsInfoRepository.findByPageSeqNoAndGoodsSeqNo(pageSeqNo, goodsSeqNo);
//
//            return result(Const.E_SUCCESS, "row", info);
//        }
//        catch(Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            throw new InvalidPageGoodsInfoException("[GET]" + this.getClass().getCanonicalName(), e);
//        }
//
//    }
//
//    @SkipSessionCheck
//    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/goodsInfo/category")
//    public Map<String,Object> getPageHashtagCategoryList(@RequestParam(value = "category", required = false) String category) {
//
//        if( category == null ) {
//            List<String> list = null ;
//            list = RedisUtil.getInstance().hScanAllKeys(REDIS_PREFIX + "goodsInfoCategory");
//            return result(200, "rows", list);
//        } else {
//            List<GoodsInfo> list = new ArrayList<GoodsInfo>() ;
//            String line = RedisUtil.getInstance().hGet(REDIS_PREFIX + "goodsInfoCategory", category);
//            String fields[] = line.split(",") ;
//            for(String field : fields) {
//
//                GoodsInfo info = new GoodsInfo() ;
//                info.setKey(field) ;
//                info.setValue(null);
//                if( StoreApplication.requiredGoodsInfoMap.get(field) != null ) {
//                    info.setRequired(true);
//                } else {
//                    info.setRequired(false);
//                }
//                list.add(info) ;
//            }
//            return result(200, "rows", list);
//        }
//
//    }
//
//}
