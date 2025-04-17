//package kr.co.pplus.store.api.jpa.controller;
//
//import kr.co.pplus.store.api.controller.RootController;
//import kr.co.pplus.store.api.jpa.model.Goods;
//import kr.co.pplus.store.api.jpa.model.GoodsCategory;
//import kr.co.pplus.store.api.jpa.repository.GoodsCategoryRepository;
//import kr.co.pplus.store.api.util.AppUtil;
//import kr.co.pplus.store.exception.InvalidGoodsCategoryException;
//import kr.co.pplus.store.exception.InvalidGoodsException;
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.Session;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//public class GoodsCategoryController extends RootController {
//
//    private Logger logger = LoggerFactory.getLogger(GoodsCategoryController.class);
//
//    @Autowired
//    GoodsCategoryRepository goodsCategoryRepository ;
//
//    @Value("${STORE.DEFAULT.LANG}")
//    String defaultLang = "ko";
//
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/goodsCategory")
//    public Map<String,Object> addGoodsCategory(Session session, @RequestBody GoodsCategory goodsCategory) throws ResultCodeException {
//
//
//        try {
//            String dateStr = AppUtil.localDatetimeNowString() ;
//            goodsCategory.setSeqNo(null);
//            goodsCategory.setRegDatetime(dateStr);
//            goodsCategory.setModDatetime(dateStr);
//            if( goodsCategory.getLang() == null || goodsCategory.getLang().trim().length() == 0 ) {
//                goodsCategory.setLang(defaultLang) ;
//            }
//            goodsCategory = goodsCategoryRepository.saveAndFlush(goodsCategory);
//            return result(Const.E_SUCCESS, "row", goodsCategory);
//        }
//        catch(Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            throw new InvalidGoodsCategoryException("add.goodsCategory", e);
//        }
//
//
//    }
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/goodsCategory")
//    public Map<String,Object> updateGoodsCategory(Session session, @RequestBody GoodsCategory goodsCategory) throws ResultCodeException {
//        try {
//
//            if (goodsCategory.getSeqNo() == null) {
//                throw new InvalidGoodsException("/goodsCategory[PUT]", "goodsCategory.seq_no cannot be null");
//            }
//
//            GoodsCategory goodsCategoryTmp = goodsCategoryRepository.findBySeqNo(goodsCategory.getSeqNo());
//            if (goodsCategoryTmp == null) {
//                throw new InvalidGoodsException("/goodsCategory[PUT]", "goodsCategory.seq_no not found");
//            }
//
//            if( goodsCategory.getLang() == null || goodsCategory.getLang().trim().length() == 0 ) {
//                goodsCategory.setLang(goodsCategoryTmp.getLang()) ;
//            }
//
//            String dateStr = AppUtil.localDatetimeNowString() ;
//            goodsCategory.setRegDatetime(null);
//            goodsCategory.setModDatetime(dateStr);
//            goodsCategory = goodsCategoryRepository.saveAndFlush(goodsCategory);
//            return result(Const.E_SUCCESS, "row", goodsCategory);
//        }
//        catch(Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            throw new InvalidGoodsCategoryException("update.goodsCategory", e);
//        }
//    }
//
//    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/goodsCategory")
//    public Map<String,Object> deleteGoodsCategory(Session session, @RequestParam(value="seqNo", required=true) Long seqNo) throws ResultCodeException {
//        try {
//            GoodsCategory category = new GoodsCategory();
//            category.setSeqNo(seqNo);
//            goodsCategoryRepository.delete(category);
//        }
//        catch(Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            throw new InvalidGoodsCategoryException("delete.goodsCategory", e);
//        }
//        return result(Const.E_SUCCESS, "row", null);
//    }
//
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/goodsCategory")
//    public Map<String,Object> selectGoodsCategory(Session session, Pageable pageable,
//                                         @RequestParam(value="depth", required=false) Byte depth,
//                                         @RequestParam(value="seqNo", required=false) Long seqNo) throws ResultCodeException {
//
//        Page<GoodsCategory> page = null ;
//        try {
//
//
//
//            if( depth != null && seqNo == null ) {
//                page = goodsCategoryRepository.findAllByDepth(depth, pageable);
//            }
//            else if( seqNo != null ) {
//                GoodsCategory goodsCategory = goodsCategoryRepository.findBySeqNo(seqNo);
//                return result(Const.E_SUCCESS, "row", goodsCategory);
//            }
//            else {
//                page = goodsCategoryRepository.findAll(pageable);
//            }
//            return result(Const.E_SUCCESS, "row", page);
//        }
//        catch(Exception e){
//            logger.error(AppUtil.excetionToString(e)) ;
//            throw new InvalidGoodsCategoryException("select.goodsCategory", e);
//        }
//
//
//    }
//}
