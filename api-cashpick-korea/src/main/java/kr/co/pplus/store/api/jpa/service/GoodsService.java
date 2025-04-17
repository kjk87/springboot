//package kr.co.pplus.store.api.jpa.service;
//
//import kr.co.pplus.store.api.jpa.model.*;
//import kr.co.pplus.store.api.jpa.repository.*;
//import kr.co.pplus.store.api.util.AppUtil;
//import kr.co.pplus.store.exception.AlreadyExistsException;
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.mvc.service.FanService;
//import kr.co.pplus.store.mvc.service.PageService;
//import kr.co.pplus.store.mvc.service.RootService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@Transactional(transactionManager = "jpaTransactionManager")
//public class GoodsService extends RootService {
//    private final static Logger logger = LoggerFactory.getLogger(GoodsService.class);
//
//
//    @Autowired
//    PageService pageSvc;
//
//    @Autowired
//    FanService fanSvc;
//
//    @Autowired
//    GoodsRepository goodsRepository;
//
//    @Autowired
//    GoodsImageRepository goodsImageRepository;
//
//    @Autowired
//    GoodsDetailRepository goodsDetailRepository;
//
//    @Autowired
//    private GoodsOptionRepository goodsOptionRepository;
//
//    @Autowired
//    private GoodsOptionItemRepository goodsOptionItemRepository;
//
//    @Autowired
//    private GoodsOptionDetailRepository goodsOptionDetailRepository;
//
//    @Autowired
//    private GoodsPriceRepository goodsPriceRepository;
//
//    @Autowired
//    private GoodsPriceOnlyRepository goodsPriceOnlyRepository;
//
//    public Page<GoodsDetail> getGoodsListByPageSeqNo(Long pageSeqNo, Boolean expired, Integer status, Integer type,
//                                                     Boolean isHotdeal, Boolean isPlus, Boolean isCoupon, Pageable pageable) throws ResultCodeException {
//        Page<GoodsDetail> page = goodsDetailRepository.findAllByWithByPageSeqNo(pageSeqNo, expired, status, type, isHotdeal, isPlus, isCoupon, pageable);
//
//        return page;
//    }
//
//    public GoodsOptionTotal getGoodsOptionTotal(Long goodsSeqNo) {
//        List<GoodsOption> goodsOptionList = goodsOptionRepository.findByGoodsSeqNoOrderBySeqNoAsc(goodsSeqNo);
//        List<GoodsOptionItem> goodsOptionItemList = goodsOptionItemRepository.findByGoodsSeqNoOrderBySeqNoAsc(goodsSeqNo);
//        List<GoodsOptionDetail> goodsOptionDetailList = goodsOptionDetailRepository.findByGoodsSeqNoOrderBySeqNoAsc(goodsSeqNo);
//
//        GoodsOptionTotal goodsOptionTotal = new GoodsOptionTotal();
//        goodsOptionTotal.setGoodsOptionList(goodsOptionList);
//        goodsOptionTotal.setGoodsOptionItemList(goodsOptionItemList);
//        goodsOptionTotal.setGoodsOptionDetailList(goodsOptionDetailList);
//
//        return goodsOptionTotal;
//    }
//
//    public GoodsOptionDetail getGoodsOptionDetailBySeqNo(Long seqNo) {
//        return goodsOptionDetailRepository.findBySeqNo(seqNo);
//    }
//
//    public GoodsOption getGoodsOptionBySeqNo(Long seqNo) {
//        return goodsOptionRepository.findBySeqNo(seqNo);
//    }
//
//    public void updateGoodsMinusSoldCount(Long seqNo, Integer amount) {
//        goodsRepository.updateMinusSoldCountBySeqNo(seqNo, amount);
//    }
//
//    public void updateGoodsOptionDetailPlusSoldCount(Long seqNo, Integer amount) {
//        goodsOptionDetailRepository.updatePlusSoldCountBySeqNo(seqNo, amount);
//    }
//
//    public void updateGoodsOptionDetailMinusSoldCount(Long seqNo, Integer amount) {
//        goodsOptionDetailRepository.updateMinusSoldCountBySeqNo(seqNo, amount);
//    }
//
//    public Goods getGoodsBySeqNo(Long seqNo){
//        return goodsRepository.findBySeqNo(seqNo);
//    }
//
//    public List<GoodsImage> getGoodsImageList(Long goodsSeqNo, String type) {
//
//        if (type == null) {
//            type = "detail";
//        }
//        return goodsImageRepository.findAllByGoodsSeqNoAndType(goodsSeqNo, type);
//    }
//
//    public Page<GoodsDetail> getGoodsListShipTypeAllByPageSeqNo(Long pageSeqNo, Long goodsCategorySeqNo, Boolean isHotdeal, Boolean isPlus, Pageable pageable) throws ResultCodeException {
//        Page<GoodsDetail> page = goodsDetailRepository.findAllShipTypeAllByPageSeqNo(pageSeqNo, goodsCategorySeqNo, isHotdeal, isPlus, pageable);
//
//        return page;
//    }
//
//    public Page<GoodsPrice> getGoodsPriceListShipTypeIsLuckyBol(Long first, Long second, Long third, Pageable pageable) throws ResultCodeException {
//        return goodsPriceRepository.findAllShipTypeWithIsLuckyball(first, second, third, pageable);
//    }
//
//    public Page<GoodsPrice> getGoodsPriceListShipTypeByPageSeqNo(Long pageSeqNo, Long first, Long second, Long third, Boolean isWholesale, Pageable pageable) throws ResultCodeException {
//        if(isWholesale){
//            return goodsPriceRepository.findAllShipTypeByPageSeqNoIsWholesale(pageSeqNo, first, second, third, pageable);
//        }else{
//            return goodsPriceRepository.findAllShipTypeByPageSeqNo(pageSeqNo, first, second, third, pageable);
//        }
//
//    }
//
//    public Page<GoodsPrice> getGoodsPriceListShipTypeByPageSeqNoOnlyNormal(Long pageSeqNo, Long first, Long second, Long third, Pageable pageable) throws ResultCodeException {
//        return goodsPriceRepository.findAllShipTypeByPageSeqNoOnlyNormal(pageSeqNo, first, second, third, pageable);
//    }
//
//    public Page<GoodsPrice> getGoodsPriceListShipTypeIsWholesale(Long pageSeqNo, Long first, Long second, Long third, Boolean isPopular, Pageable pageable) throws ResultCodeException {
//        if(isPopular){
//            return goodsPriceRepository.findAllShipTypeIsWholesaleOrderByPopular(pageSeqNo, first, second, third, pageable);
//        }
//        return goodsPriceRepository.findAllShipTypeIsWholesale(pageSeqNo, first, second, third, pageable);
//    }
//
//    public GoodsPrice getGoodsPriceBySeqNo(Long seqNo) {
//        return goodsPriceRepository.findBySeqNo(seqNo);
//    }
//
//    public GoodsPrice getGoodsPriceByGoodsSeqNoAndPageSeqNo(Long goodsSeqNo, Long pageSeqNo) {
//        return goodsPriceRepository.findByGoodsSeqNoAndPageSeqNo(goodsSeqNo, pageSeqNo);
//    }
//
//    public GoodsPriceOnly getGoodsPriceOnlyBySeqNo(Long seqNo) {
//        return goodsPriceOnlyRepository.findBySeqNo(seqNo);
//    }
//
//    public GoodsPriceOnly getGoodsPriceOnlyByGoodsSeqNoAndIsWholesale(Long goodsSeqNo, Boolean isWholesale) {
//        return goodsPriceOnlyRepository.findByGoodsSeqNoAndIsWholesale(goodsSeqNo, isWholesale);
//    }
//
//    public List<GoodsPriceOnly> getGoodsPriceOnlyByGoodsSeqNo(Long goodsSeqNo) {
//        return goodsPriceOnlyRepository.findByGoodsSeqNo(goodsSeqNo);
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public void updateGoodsPriceStatusByGoodsSeqNo(Long goodsSeqNo, Integer status) {
//        goodsPriceOnlyRepository.updateGoodsPriceStatusByGoodsSeqNo(status, goodsSeqNo);
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public void updateGoodsPriceStatusByGoodsSeqNoAndSoldOut(Long goodsSeqNo, Integer status) {
//        goodsPriceOnlyRepository.updateGoodsPriceStatusByGoodsSeqNoAndSoldOut(status, goodsSeqNo);
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public GoodsPriceOnly saveGoodsPriceOnly(GoodsPriceOnly goodsPriceOnly) {
//
//        if (goodsPriceOnly.getOriginPrice() != null && goodsPriceOnly.getPrice() != null) {
//            Float discountRatio = 100 - (goodsPriceOnly.getPrice() / goodsPriceOnly.getOriginPrice() * 100);
//            goodsPriceOnly.setDiscountRatio(discountRatio);
//        }
//
//        return goodsPriceOnlyRepository.saveAndFlush(goodsPriceOnly);
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public GoodsPriceOnly insertGoodsPriceOnly(GoodsPriceOnly goodsPriceOnly) throws ResultCodeException{
//
//        GoodsPrice goodsPrice = getGoodsPriceByGoodsSeqNoAndPageSeqNo(goodsPriceOnly.getGoodsSeqNo(), goodsPriceOnly.getPageSeqNo());
//
//        if(goodsPrice != null){
//            throw new AlreadyExistsException("insertGoodsPriceOnly", "이미 존재하는 상품 입니다.");
//        }
//
//        goodsPriceOnly.setSeqNo(null);
//        String dateStr = AppUtil.localDatetimeNowString();
//        goodsPriceOnly.setRegDatetime(dateStr);
//        goodsPriceOnly.setIsLuckyball(false);
//        goodsPriceOnly.setIsWholesale(false);
//        return saveGoodsPriceOnly(goodsPriceOnly);
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public void updateGoodsPriceStatusBySeqNo(Long goodsPriceSeqNo, Integer status) throws ResultCodeException{
//        goodsPriceOnlyRepository.updateGoodsPriceStatusBySeqNo(status, goodsPriceSeqNo);
//    }
//}
