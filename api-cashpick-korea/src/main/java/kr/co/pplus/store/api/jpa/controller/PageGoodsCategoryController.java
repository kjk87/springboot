//package kr.co.pplus.store.api.jpa.controller;
//
//import kr.co.pplus.store.api.annotation.SkipSessionCheck;
//import kr.co.pplus.store.api.controller.RootController;
//import kr.co.pplus.store.api.jpa.model.GoodsCategory;
//import kr.co.pplus.store.api.jpa.model.PageGoodsCategory;
//import kr.co.pplus.store.api.jpa.model.PageGoodsCategoryDetail;
//import kr.co.pplus.store.api.jpa.repository.GoodsCategoryRepository;
//import kr.co.pplus.store.api.jpa.repository.GoodsRepository;
//import kr.co.pplus.store.api.jpa.repository.PageGoodsCategoryDetailRepository;
//import kr.co.pplus.store.api.jpa.repository.PageGoodsCategoryRepository;
//import kr.co.pplus.store.api.util.AppUtil;
//import kr.co.pplus.store.exception.*;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.Session;
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
//import java.util.List;
//import java.util.Map;
//
//@RestController
//public class PageGoodsCategoryController extends RootController {
//
//    private Logger logger = LoggerFactory.getLogger(PageGoodsCategoryController.class);
//
//    @Autowired
//    GoodsCategoryRepository goodsCategoryRepository ;
//
//    @Value("${STORE.DEFAULT.LANG}")
//    String defaultLang = "ko";
//
//    @Autowired
//    PageGoodsCategoryRepository pageGoodsCategoryRepository ;
//
//    @Autowired
//    PageGoodsCategoryDetailRepository pageGoodsCategoryDetailRepository ;
//
//    @Autowired
//    GoodsRepository goodsRepository ;
//
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageGoodsCategory")
//    public Map<String,Object> addPageGoodsCategory(Session session, @RequestBody PageGoodsCategoryDetail pageGoodsCategoryDetail) throws ResultCodeException {
//
//
//        try {
//
//            String dateStr = AppUtil.localDatetimeNowString() ;
//
//            GoodsCategory goodsCategory = pageGoodsCategoryDetail.getGoodsCategory() ;
//            goodsCategory.setSeqNo(null);
//            goodsCategory.setRegDatetime(dateStr);
//            goodsCategory.setModDatetime(dateStr);
//            if( goodsCategory.getLang() == null || goodsCategory.getLang().trim().length() == 0 ) {
//                goodsCategory.setLang(defaultLang) ;
//            }
//
//            try {
//                GoodsCategory tempGoodsCategory = goodsCategoryRepository
//                        .findByDepthAndNameAndLang(goodsCategory.getDepth(),
//                                goodsCategory.getName(),
//                                goodsCategory.getLang()) ;
//
//                if( tempGoodsCategory == null ) {
//                    goodsCategory = goodsCategoryRepository.saveAndFlush(goodsCategory) ;
////                    pageGoodsCategory.setGoodsCategory(goodsCategory) ;
//                } else {
//                    goodsCategory = tempGoodsCategory;
////                    pageGoodsCategory.setGoodsCategory(goodsCategory) ;
//                }
//
//            } catch(Exception e) {
//                new InvalidPageGoodsCategoryException("add.goodsCategory", "insert error");
//            }
//
//            pageGoodsCategoryDetail.setGoodsCategorySeqNo(goodsCategory.getSeqNo());
//
//            PageGoodsCategory pgc = new PageGoodsCategory() ;
//            pgc.setGoodsCategorySeqNo(pageGoodsCategoryDetail.getGoodsCategorySeqNo());
//            pgc.setPageSeqNo(pageGoodsCategoryDetail.getPageSeqNo());
//
//            pgc.setSeqNo(null);
//            pgc = pageGoodsCategoryRepository.saveAndFlush(pgc) ;
//
//            return result(Const.E_SUCCESS, "row", pgc);
//        }
//        catch(Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            throw new InvalidPageGoodsCategoryException("add.pageGoodsCategory", e);
//        }
//    }
//
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageGoodsCategory")
//    public Map<String,Object> updatePageGoodsCategory(Session session, @RequestBody PageGoodsCategoryDetail pageGoodsCategoryDetail) throws ResultCodeException {
//        try {
//
//            String dateStr = AppUtil.localDatetimeNowString() ;
//
//            GoodsCategory goodsCategory = pageGoodsCategoryDetail.getGoodsCategory() ;
//            Long beforeGoodsCategorySeqNo = goodsCategory.getSeqNo();
//            goodsCategory.setSeqNo(null);
//            goodsCategory.setRegDatetime(dateStr);
//            goodsCategory.setModDatetime(dateStr);
//            if( goodsCategory.getLang() == null || goodsCategory.getLang().trim().length() == 0 ) {
//                goodsCategory.setLang(defaultLang) ;
//            }
//
//            try {
//                GoodsCategory tempGoodsCategory = goodsCategoryRepository
//                        .findByDepthAndNameAndLang(goodsCategory.getDepth(),
//                                goodsCategory.getName(),
//                                goodsCategory.getLang()) ;
//
//                if( tempGoodsCategory == null ) {
//                    goodsCategory = goodsCategoryRepository.saveAndFlush(goodsCategory) ;
////                    pageGoodsCategory.setGoodsCategory(goodsCategory) ;
//                } else {
//                    goodsCategory = tempGoodsCategory;
////                    pageGoodsCategory.setGoodsCategory(goodsCategory) ;
//                }
//
//            } catch(Exception e) {
//                new InvalidPageGoodsCategoryException("add.goodsCategory", "insert error");
//            }
//
//            Long currentGoodsCategorySeqNo = goodsCategory.getSeqNo();
//
//            pageGoodsCategoryDetail.setGoodsCategorySeqNo(goodsCategory.getSeqNo());
//
//            PageGoodsCategory pgc = new PageGoodsCategory() ;
//            pgc.setSeqNo(pageGoodsCategoryDetail.getSeqNo());
//            pgc.setPageSeqNo(pageGoodsCategoryDetail.getPageSeqNo());
//            pgc.setGoodsCategorySeqNo(pageGoodsCategoryDetail.getGoodsCategorySeqNo());
//
//            pgc = pageGoodsCategoryRepository.saveAndFlush(pgc) ;
//            goodsRepository.updateCategorySeqNoByPageSeqNo(currentGoodsCategorySeqNo, beforeGoodsCategorySeqNo, pageGoodsCategoryDetail.getPageSeqNo());
//
//            return result(Const.E_SUCCESS, "row", pgc);
//        }
//        catch(Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            throw new InvalidGoodsException("update.goodsCategory", e);
//        }
//    }
//
//
//    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageGoodsCategory")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidPageGoodsCategoryException.class)
//    public Map<String,Object> deletePageGoodsCategory(Session session, @RequestParam(value="seqNo", required=true) Long seqNo) throws ResultCodeException {
//        try {
//
//            PageGoodsCategory pgc = pageGoodsCategoryRepository.findBySeqNo(seqNo) ;
//            if( pgc.getGoodsCategorySeqNo() == 1 ){
//                throw new RemoveBasicGoodsCategoryException("[DELETE]pageGoodsCategory", "Cannot remove default basic goods_category") ;
//            }
//
//            goodsRepository.resetCategory(pgc.getPageSeqNo(), pgc.getGoodsCategorySeqNo()) ;
//            pageGoodsCategoryRepository.deleteBySeqNo(seqNo);
//        }
//        catch(Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            if( e instanceof ResultCodeException ) {
//                throw e;
//            }
//            else {
//                throw new InvalidPageGoodsCategoryException("delete.pageGoodsCategory", e);
//            }
//        }
//        return result(Const.E_SUCCESS, "row", null);
//    }
//
//    @SkipSessionCheck
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/pageGoodsCategory")
//    public Map<String,Object> selectGoodsCategory(Session session, Pageable pageable,
//                                         @RequestParam(value="depth", required=false) Byte depth,
//                                         @RequestParam(value="pageSeqNo", required=true) Long pageSeqNo) throws ResultCodeException {
//
//        Page<PageGoodsCategory> page = null ;
//        try {
//
//            List<PageGoodsCategoryDetail> list = pageGoodsCategoryDetailRepository.findAllBy(pageSeqNo, depth);
//
//            return result(Const.E_SUCCESS, "rows", list);
//        }
//        catch(Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            throw new InvalidPageGoodsCategoryException("[GET]/pageGoodsCategory","select Error");
//        }
//
//    }
//}
