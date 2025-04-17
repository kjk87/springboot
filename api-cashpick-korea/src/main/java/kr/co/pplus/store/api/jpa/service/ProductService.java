package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.AlreadyExistsException;
import kr.co.pplus.store.exception.InvalidGoodsLikeException;
import kr.co.pplus.store.exception.InvalidGoodsReviewException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.SetID;
import org.jivesoftware.smack.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class ProductService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductOptionItemRepository productOptionItemRepository;

    @Autowired
    private ProductOptionDetailRepository productOptionDetailRepository;

    @Autowired
    private ProductPriceRepository productPriceRepository;


    @Autowired
    private ProductPriceRefRepository productPriceRefRepository;

    @Autowired ProductPriceWithDistanceRepository productPriceWithDistanceRepository;

    @Autowired
    private ProductPriceOnlyRepository productPriceOnlyRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private ProductReviewImageRepository productReviewImageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    ProductLikeOnlyRepository productLikeOnlyRepository;

    @Autowired
    ProductLikeRepository productLikeRepository;

    @Autowired
    ProductInfoRepository productInfoRepository;

    @Autowired
    ProductAuthRepository productAuthRepository;

    @Autowired
    ProductNoticeRepository productNoticeRepository;

    @Autowired
    PageJpaService pageJpaService;

    public Product saveAndFlush(Product product) {
        return productRepository.saveAndFlush(product);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public ProductOptionTotal getProductOptionTotal(Long productSeqNo) {
        List<ProductOption> productOptionList = productOptionRepository.findByProductSeqNoOrderBySeqNoAsc(productSeqNo);
        List<ProductOptionItem> productOptionItemList = productOptionItemRepository.findByProductSeqNoOrderBySeqNoAsc(productSeqNo);
        List<ProductOptionDetail> productOptionDetailList = productOptionDetailRepository.findByProductSeqNoAndUsableAndStatusOrderBySeqNoAsc(productSeqNo, true, 1);

        ProductOptionTotal productOptionTotal = new ProductOptionTotal();
        productOptionTotal.setProductOptionList(productOptionList);
        productOptionTotal.setProductOptionItemList(productOptionItemList);
        productOptionTotal.setProductOptionDetailList(productOptionDetailList);

        return productOptionTotal;
    }

    public ProductOptionDetail getProductOptionDetailBySeqNo(Long seqNo) {
        return productOptionDetailRepository.findBySeqNo(seqNo);
    }

    public void saveProductOptionDetailRepository(ProductOptionDetail productOptionDetail) {
        productOptionDetailRepository.save(productOptionDetail);
    }

    public ProductOption getProductOptionBySeqNo(Long seqNo) {
        return productOptionRepository.findBySeqNo(seqNo);
    }

    public void updateGoodsMinusSoldCount(Long seqNo, Integer amount) {
        productRepository.updateMinusSoldCountBySeqNo(seqNo, amount);
    }

    public void updateSoldCountAndStatusBySeqNo(Long seqNo, Integer status, Integer amount) {
        String dateStr = AppUtil.localDatetimeNowString();
        productRepository.updateSoldCountAndStatusBySeqNo(seqNo, status, amount, dateStr);
    }

    public void updateProductOptionDetailPlusSoldCount(Long seqNo, Integer amount) {
        productOptionDetailRepository.updatePlusSoldCountBySeqNo(seqNo, amount);
    }

    public void updateProductOptionDetailMinusSoldCount(Long seqNo, Integer amount) {
        productOptionDetailRepository.updateMinusSoldCountBySeqNo(seqNo, amount);
    }

    public List<ProductImage> getProductImageList(Long productSeqNo, Boolean deligate) {

        if (deligate == null) {
            deligate = false;
        }
        return productImageRepository.findAllByProductSeqNoAndDeligate(productSeqNo, deligate);
    }

    public Integer getCountByPageSeqNoOnlyNormal(Long pageSeqNo) {
        return productPriceRepository.countByPageSeqNoOnlyNormal(pageSeqNo);
    }

    public Page<ProductPrice> getProductPriceListShipTypeIsLuckyBol(Long first, Long second, Long third, Long memberSeqNo, Boolean pick, String search, Pageable pageable) throws ResultCodeException {

        if (!AppUtil.isEmpty(search)) {
            search = "%" + search.replace(" ", "") + "%";
        }

        return productPriceRepository.findAllShipTypeWithIsLuckyball(first, second, third, memberSeqNo, pick, search, pageable);
    }

    public Page<ProductPrice> getProductPriceListShipTypeByShoppingGroup(Long shoppingGroupSeqNo, Long memberSeqNo, Pageable pageable) throws ResultCodeException {

        return productPriceRepository.findAllShipTypeByShoppingGroup(shoppingGroupSeqNo, memberSeqNo, pageable);
    }

    public Page<ProductPrice> getProductPriceListShipTypeByShoppingBrand(Long shoppingBrandSeqNo, Long memberSeqNo, Pageable pageable) {

        return productPriceRepository.findAllShipTypeByShoppingBrand(shoppingBrandSeqNo, memberSeqNo, pageable);
    }


    public List<ProductPrice> getProductPriceListShipTypeByRandom(Long memberSeqNo) {

        return productPriceRepository.findAllShipTypeByRandom(memberSeqNo);
    }


    public Page<ProductPrice> getProductPriceListShipTypeByPageSeqNo(Long pageSeqNo, Long first, Long second, Long third, Boolean isWholesale, Pageable pageable) throws ResultCodeException {
        if (isWholesale) {
            return productPriceRepository.findAllShipTypeByPageSeqNoIsWholesale(pageSeqNo, first, second, third, pageable);
        } else {
            return productPriceRepository.findAllShipTypeByPageSeqNo(pageSeqNo, first, second, third, pageable);
        }

    }

    public Page<ProductPrice> getProductPriceListTicketTypeByPageSeqNo(Long pageSeqNo, Long first, Long second, Long third, Pageable pageable) throws ResultCodeException {
        return productPriceRepository.findAllTicketTypeByPageSeqNo(pageSeqNo, first, second, third, pageable);

    }

    public Page<ProductPrice> getProductPriceListShipTypeByPageSeqNoOnlyNormal(Long pageSeqNo, Long memberSeqNo, Long first, Long second, Long third, Pageable pageable) throws ResultCodeException {
        return productPriceRepository.findAllByPageSeqNoAndSalesTypeOnlyNormal(pageSeqNo, memberSeqNo, SalesType.SHIPPING.getType(), first, second, third, pageable);
    }

    public Page<ProductPrice> getProductPriceListStoreTypeByPageSeqNoOnlyNormal(Long pageSeqNo, Long memberSeqNo, Long first, Long second, Long third, Pageable pageable) throws ResultCodeException {
        return productPriceRepository.findAllByPageSeqNoAndSalesTypeOnlyNormal(pageSeqNo, memberSeqNo, SalesType.TICKET.getType(), first, second, third, pageable);
    }

    public Page<ProductPrice> getProductPriceListStoreTypeByPageSeqNoAndDiscountOnlyNormal(Long pageSeqNo, Long memberSeqNo, Long first, Long second, Long third, Pageable pageable) throws ResultCodeException {
        return productPriceRepository.findAllByPageSeqNoAndSalesTypeAndDiscountOnlyNormal(pageSeqNo, memberSeqNo, SalesType.TICKET.getType(), first, second, third, pageable);
    }

    public Page<ProductPrice> getProductPriceListByIsSubscriptionAndIsPrepaymentOnlyNormal(Long pageSeqNo, Long memberSeqNo, Pageable pageable) throws ResultCodeException {
        return productPriceRepository.findAllByPageSeqNoAndIsSubscriptionAndIsPrepaymentOnlyNormal(pageSeqNo, memberSeqNo, pageable);
    }

    public Page<ProductPrice> getProductPriceListShipTypeByManageSeqNoOnlyNormal(Long manageSeqNo, Long memberSeqNo, Long first, Long second, Long third, Pageable pageable) throws ResultCodeException {
        return productPriceRepository.findAllByManageSeqNoAndSalesTypeOnlyNormal(manageSeqNo, memberSeqNo, SalesType.SHIPPING.getType(), first, second, third, pageable);
    }

    public Page<ProductPriceWithDistance> getProductPriceListTicketTypeByManageSeqNoOnlyNormal(Double latitude, Double longitude, Long manageSeqNo, Long memberSeqNo, Long first, Long second, Long third, Pageable pageable) throws ResultCodeException {
        return productPriceWithDistanceRepository.findAllByManageSeqNoAndSalesTypeOnlyNormalWithDistance(latitude, longitude, manageSeqNo, memberSeqNo, SalesType.TICKET.getType(), first, second, third, pageable);
    }

    public Page<ProductPrice> getProductPriceListShipTypeByPageAndDiscount(Long memberSeqNo, Long first, Long second, Long third, Pageable pageable) throws ResultCodeException {
        return productPriceRepository.findAllShipTypeByPageAndDiscount(memberSeqNo, first, second, third, pageable);
    }

    public Page<ProductPriceWithDistance> getProductPriceListStoreTypeByPageAndDiscountDistanceDesc(Double latitude, Double longitude, Long memberSeqNo, Long first, Long second, Long third, Pageable pageable) throws ResultCodeException {
        return productPriceWithDistanceRepository.findAllStoreTypeByPageAndDiscountDistanceDesc(latitude, longitude, memberSeqNo, first, second, third, pageable);
    }

    public Page<ProductPriceWithDistance> getPlusSubscriptionTypeOnlyNormalOrderByDistance(Long memberSeqNo, Double latitude, Double longitude, Pageable pageable) throws ResultCodeException {
        return productPriceWithDistanceRepository.findAllPlusSubscriptionTypeOnlyNormalOrderByDistance(memberSeqNo, latitude, longitude, pageable);
    }

    public Page<ProductPrice> getProductPriceListShipTypeIsWholesale(Long pageSeqNo, Long first, Long second, Long third, Boolean isPopular, Pageable pageable) throws ResultCodeException {
        if (isPopular) {
            return productPriceRepository.findAllShipTypeIsWholesaleOrderByPopular(pageSeqNo, first, second, third, pageable);
        }
        return productPriceRepository.findAllShipTypeIsWholesale(pageSeqNo, first, second, third, pageable);
    }

    public ProductPrice getProductPriceBySeqNo(Long seqNo) {
        return productPriceRepository.findBySeqNo(seqNo);
    }

    public ProductPrice getProductPriceByCode(String code) {
        return productPriceRepository.findByCode(code);
    }

    public ProductPrice getProductPriceByProductSeqNoAndPageSeqNo(Long productSeqNo, Long pageSeqNo) {
        return productPriceRepository.findByProductSeqNoAndPageSeqNo(productSeqNo, pageSeqNo);
    }

    public ProductPriceOnly getProductPriceOnlyBySeqNo(Long seqNo) {
        return productPriceOnlyRepository.findBySeqNo(seqNo);
    }

    public ProductPriceOnly getProductPriceOnlyByCode(String code) {
        return productPriceOnlyRepository.findByCode(code);
    }

    public ProductPriceOnly getProductPriceOnlyByGoodsSeqNoAndMarketType(Long productSeqNo, Integer marketType) {
        return productPriceOnlyRepository.findByProductSeqNoAndMarketType(productSeqNo, marketType);
    }

    public List<ProductPriceOnly> getProductPriceOnlyByProductSeqNo(Long productSeqNo) {
        return productPriceOnlyRepository.findByProductSeqNo(productSeqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateProductPriceStatusByProductSeqNo(Long productSeqNo, Integer status) {
        productPriceOnlyRepository.updateProductPriceStatusByProductSeqNo(status, productSeqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateProductPriceStatusByProductSeqNoAndSoldOut(Long productSeqNo, Integer status) {
        productPriceOnlyRepository.updateProductPriceStatusByProductSeqNoAndSoldOut(status, productSeqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveProductPriceOnly(ProductPriceOnly productPriceOnly) {
        productPriceOnlyRepository.save(productPriceOnly);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ProductPriceOnly saveAndFlushProductPriceOnly(ProductPriceOnly productPriceOnly) {

        if (productPriceOnly.getOriginPrice() != null && productPriceOnly.getPrice() != null) {
            Float discountRatio = 100 - (productPriceOnly.getPrice() / productPriceOnly.getOriginPrice() * 100);
            productPriceOnly.setDiscountRatio(discountRatio);
        }

        return productPriceOnlyRepository.saveAndFlush(productPriceOnly);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ProductPriceOnly insertProductPriceOnly(ProductPriceOnly productPriceOnly) throws ResultCodeException {

        ProductPrice productPrice = getProductPriceByProductSeqNoAndPageSeqNo(productPriceOnly.getProductSeqNo(), productPriceOnly.getPageSeqNo());

        if (productPrice != null) {
            throw new AlreadyExistsException("insertProductPriceOnly", "이미 존재하는 상품 입니다.");
        }

        productPriceOnly.setSeqNo(null);
        String dateStr = AppUtil.localDatetimeNowString();
        productPriceOnly.setRegDatetime(dateStr);
        productPriceOnly.setIsLuckyball(false);
        productPriceOnly.setMarketType(3);
        return saveAndFlushProductPriceOnly(productPriceOnly);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateProductPriceStatusBySeqNo(Long productPriceSeqNo, Integer status) throws ResultCodeException {
        ProductPriceOnly productPriceOnly = productPriceOnlyRepository.findBySeqNo(productPriceSeqNo);
        if(productPriceOnly.getIsTicket() || productPriceOnly.getIsSubscription() || productPriceOnly.getIsPrepayment()){
            productRepository.updateProductStatusBySeqNo(status, productPriceOnly.getProductSeqNo());
        }
        productPriceOnlyRepository.updateProductPriceStatusBySeqNo(status, productPriceSeqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertProductReview(ProductReview productReview) throws ResultCodeException {

        try {

            List<ProductReviewImage> productReviewImageList = productReview.getImageList();
            String dateStr = AppUtil.localDatetimeNowString();
            productReview.setSeqNo(null);
            productReview.setRegDatetime(dateStr);
            productReview.setModDatetime(dateStr);
            productReview = productReviewRepository.saveAndFlush(productReview);

            if (productReviewImageList != null && productReviewImageList.size() > 0) {

                for (ProductReviewImage productReviewImage : productReviewImageList) {
                    productReviewImage.setProductReviewSeqNo(productReview.getSeqNo());
                    productReviewImage.setType("thumbnail");
                    productReviewImageRepository.save(productReviewImage);
                }
            }
            return Const.E_SUCCESS;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new InvalidGoodsReviewException();
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateProductReview(ProductReview productReview) throws ResultCodeException {

        try {

            if (productReview.getSeqNo() == null) {
                throw new InvalidGoodsReviewException("/eventReview[PUT]", "eventReview.seq_no cannot be null");
            }

            String dateStr = AppUtil.localDatetimeNowString();

            ProductReview saved = productReviewRepository.findBySeqNo(productReview.getSeqNo());
            saved.setEval(productReview.getEval());
            saved.setReview(productReview.getReview());
            saved.setModDatetime(dateStr);

            List<ProductReviewImage> productReviewImageList = productReview.getImageList();


            saved = productReviewRepository.saveAndFlush(saved);

            productReviewImageRepository.deleteAllByProductReviewSeqNo(saved.getSeqNo());

            if (productReviewImageList != null && productReviewImageList.size() > 0) {

                for (ProductReviewImage productReviewImage : productReviewImageList) {
                    productReviewImage.setProductReviewSeqNo(saved.getSeqNo());
                    productReviewImage.setType("thumbnail");
                    productReviewImageRepository.save(productReviewImage);
                }
            }
            return Const.E_SUCCESS;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new InvalidGoodsReviewException();
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteProductReview(Long seqNo) throws ResultCodeException {

        productReviewImageRepository.deleteAllByProductReviewSeqNo(seqNo);
        ProductReview productReview = new ProductReview();
        productReview.setSeqNo(seqNo);
        productReviewRepository.delete(productReview);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateProductReviewReply(User user, ProductReview productReview) throws ResultCodeException {
        PageDetail page = pageJpaService.getPageByMemberSeqNo(user.getNo());

        if(!page.getSeqNo().equals(productReview.getPageSeqNo())){
            throw new InvalidGoodsReviewException("productReview not page owner");
        }

        String dateStr = AppUtil.localDatetimeNowString();

        ProductReview saved = productReviewRepository.findBySeqNo(productReview.getSeqNo());
        saved.setReviewReply(productReview.getReviewReply());
        saved.setModDatetime(dateStr);


        if(StringUtils.isNotEmpty(productReview.getReviewReply())){
            saved.setReviewReplyDate(dateStr);
        }else{
            saved.setReviewReplyDate(null);
        }
        saved = productReviewRepository.saveAndFlush(saved);
        return Const.E_SUCCESS;
    }

    public Page<ProductReview> getProductReviewByMemberSeqNo(User user, Pageable pageable) throws ResultCodeException {
        return productReviewRepository.findAllByMemberSeqNo(user.getNo(), pageable);
    }

    public Page<ProductReview> getProductReviewByProductSeqNo(Long productSeqNo, Pageable pageable) throws ResultCodeException {
        return productReviewRepository.findAllByProductSeqNo(productSeqNo, pageable);
    }

    public Page<ProductReview> getProductReviewByProductPriceSeqNo(Long productPriceSeqNo, Pageable pageable) throws ResultCodeException {
        return productReviewRepository.findAllByProductPriceSeqNo(productPriceSeqNo, pageable);
    }

    public Page<ProductReview> getProductReviewByPageSeqNo(Long pageSeqNo, Pageable pageable) throws ResultCodeException {
        return productReviewRepository.findAllByPageSeqNo(pageSeqNo, pageable);
    }

    public ProductReview getLastProductReviewByPageSeqNo(Long pageSeqNo) throws ResultCodeException {
        return productReviewRepository.findFirstByPageSeqNoOrderBySeqNoDesc(pageSeqNo);
    }

    public List<ProductReviewCountEval> getProductReviewCountGroupByEval(Long productPriceSeqNo) {

        List<ProductReviewCountEval> productReviewCountEvalList = new ArrayList<>();

        for (int i = 5; i > 0; i--) {
            ProductReviewCountEval productReviewCountEval = new ProductReviewCountEval();
            Integer count = productReviewRepository.findProductReviewCountGroupByEval(productPriceSeqNo, i);
            productReviewCountEval.setCount(count);
            productReviewCountEval.setEval(i);
            productReviewCountEvalList.add(productReviewCountEval);
        }

        return productReviewCountEvalList;
    }

    public List<ProductReviewCountEval> getProductReviewCountGroupByEvalByPageSeqNo(Long pageSeqNo) {

        List<ProductReviewCountEval> productReviewCountEvalList = new ArrayList<>();

        for (int i = 5; i > 0; i--) {
            ProductReviewCountEval productReviewCountEval = new ProductReviewCountEval();
            Integer count = productReviewRepository.findProductReviewCountGroupByEvalByPageSeqNo(pageSeqNo, i);
            productReviewCountEval.setCount(count);
            productReviewCountEval.setEval(i);
            productReviewCountEvalList.add(productReviewCountEval);
        }

        return productReviewCountEvalList;
    }

    public Integer getCountProductReviewByProductPriceSeqNo(Long productPriceSeqNo) {
        return productReviewRepository.countByProductPriceSeqNo(productPriceSeqNo);
    }

    public Integer getCountProductReviewByMemberSeqNo(Long memberSeqNo) {
        return productReviewRepository.countByMemberSeqNo(memberSeqNo);
    }

    public Integer getCountProductReviewByPageSeqNo(Long pageSeqNo) {
        return productReviewRepository.countByPageSeqNo(pageSeqNo);
    }

    public Integer getCountProductLike(User user) {
        return productLikeOnlyRepository.countByMemberSeqNo(user.getNo());
    }

    public Integer existProductLike(User user, ProductLikeOnly productLikeOnly) {
        ProductLikeOnly existProductLike = productLikeOnlyRepository.findByMemberSeqNoAndProductSeqNoAndProductPriceSeqNo(user.getNo(), productLikeOnly.getProductSeqNo(), productLikeOnly.getProductPriceSeqNo());

        return (existProductLike != null) ? Const.E_SUCCESS : Const.E_NOTFOUND;
    }

    public Product getProduct(Long seqNo) {
        return productRepository.findBySeqNo(seqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer insertProductLike(User user, ProductLikeOnly productLikeOnly) throws ResultCodeException {


        try {
            productLikeOnly.setStatus(1);
            productLikeOnly.setRegDatetime(AppUtil.localDatetimeNowString());

            Product product = getProduct(productLikeOnly.getProductSeqNo());
            if (product.getEndDate() != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                productLikeOnly.setExpireDatetime(format.format(product.getEndDate()));
            }

            ProductLikeOnly existProductLike = productLikeOnlyRepository.findByMemberSeqNoAndProductSeqNoAndProductPriceSeqNo(user.getNo(), productLikeOnly.getProductSeqNo(), productLikeOnly.getProductPriceSeqNo());
            if (existProductLike == null) {
                productLikeOnly = productLikeOnlyRepository.saveAndFlush(productLikeOnly);
            }

            return Const.E_SUCCESS;

        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidGoodsLikeException("/addProductLike[POST]", "insert error");
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer deleteProductLike(User user, ProductLikeOnly productLikeOnly) throws ResultCodeException {
        try {
            ProductLikeOnly existProductLike = productLikeOnlyRepository.findByMemberSeqNoAndProductSeqNoAndProductPriceSeqNo(user.getNo(), productLikeOnly.getProductSeqNo(), productLikeOnly.getProductPriceSeqNo());

            if (existProductLike != null) {
                productLikeOnlyRepository.delete(existProductLike);
            }
            return Const.E_SUCCESS;
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidGoodsLikeException("/deleteProductLike[DELETE]", e);
        }
    }

    public void deleteLikeAllByProductSeqNo(Long productSeqNo) {
        productLikeOnlyRepository.deleteAllByProductSeqNo(productSeqNo);
    }

    public Page<ProductLike> getProductLikeByMemberSeqNoShipping(User user, Pageable pageable) throws ResultCodeException {
        return productLikeRepository.findAllByMemberSeqNoShipping(user.getNo(), pageable);
    }

    public ProductInfo getProductInfoByProductSeqNo(Long productSeqNo) {
        return productInfoRepository.findByProductSeqNo(productSeqNo);
    }

    public ProductAuth getProductAuthByProductSeqNo(Long productSeqNo) {
        return productAuthRepository.findByProductSeqNo(productSeqNo);
    }

    public List<ProductNotice> getProductNoticeListByProductSeqNo(Long productSeqNo) {
        return productNoticeRepository.findAllByProductSeqNo(productSeqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void setExpire(){
        String dateStr = AppUtil.localDatetimeNowString();
        List<Product> list = productRepository.findAllByStatusAndEndDateLessThanEqual(1, dateStr);
        for(Product product : list){
            productPriceOnlyRepository.updateProductPriceStatusByProductSeqNo(-1, product.getSeqNo());
            product.setStatus(-1);
            product.setModDatetime(dateStr);
        }
        productRepository.saveAll(list);
    }

    public ProductPriceRef getMainProductPrice(Long pageSeqNo){
        return productPriceRefRepository.findFirstByPageSeqNoAndStatusAndIsTicketAndPickOrderBySeqNo(pageSeqNo, 1, true, true);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updatePick(Long productPriceSeqNo, Long pageSeqNo) throws ResultCodeException {

        try {
            productPriceOnlyRepository.updateProductPricePickByPageSeqNo(false, pageSeqNo);
            productPriceOnlyRepository.updateProductPricePickBySeqNo(true, productPriceSeqNo);
            return Const.E_SUCCESS;
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidGoodsLikeException("updatePick", e);
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ProductPriceOnly updateTicketProduct(User user, ProductPrice productPrice) throws ResultCodeException {
        String dateStr = AppUtil.localDatetimeNowString();

        Product product = productPrice.getProduct();
        Product savedProduct = productRepository.findBySeqNo(product.getSeqNo());

        Set<ProductImage> imageList = product.getImageList();

        product.setMarketType("retail");
        product.setSalesType(6L);
        product.setStatus(1);
        product.setBlind(false);
        product.setPriceMethod("fix");
        product.setSurtax(true);
        product.setSalesTerm(true);
        product.setSoldCount(savedProduct.getSoldCount());
        product.setStartDate(savedProduct.getStartDate());
        product.setEndDate(savedProduct.getEndDate());
        product.setCount(savedProduct.getCount());
        product.setUseOption(false);
        product.setRegister(user.getLoginId());
        product.setRegisterType("user");
        product.setModDatetime(dateStr);
        product = productRepository.saveAndFlush(product);

        productImageRepository.deleteAllByProductSeqNo(product.getSeqNo());

        if (imageList != null && imageList.size() > 0) {

            int i = 0;
            for (ProductImage image : imageList) {
                if(i == 0){
                    image.setDeligate(true);
                }else{
                    image.setDeligate(false);
                }
                i++;
                image.setProductSeqNo(product.getSeqNo());
                productImageRepository.save(image);
            }
        }

        ProductPriceOnly savedProductPrice = productPriceOnlyRepository.findBySeqNo(productPrice.getSeqNo());

        productPrice.setCode(savedProductPrice.getCode());
        productPrice.setProductSeqNo(product.getSeqNo());

        productPrice.setDiscountUnit("percent");
        productPrice.setRegDatetime(savedProductPrice.getRegDatetime());
        productPrice.setDailySoldCount(savedProductPrice.getDailySoldCount());
        productPrice.setIsLuckyball(false);
        productPrice.setMarketType(2);

        Float salePrice = productPrice.getOriginPrice() - (int)(productPrice.getOriginPrice()*(productPrice.getDiscountRatio()/100));
        productPrice.setPrice(salePrice);
        productPrice.setDiscount(productPrice.getOriginPrice() - productPrice.getPrice());

        productPrice.setStatus(1);
        productPrice.setPick(false);
        productPrice.setTemp(false);


        productPrice.setIsTicket(true);
        productPrice.setIsSubscription(false);
        productPrice.setIsPrepayment(false);

        ProductPriceOnly productPriceOnly  = productPrice.convert();
        productPriceOnly = productPriceOnlyRepository.saveAndFlush(productPriceOnly);

        return productPriceOnly;

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ProductPriceOnly regTicketProduct(User user, ProductPrice productPrice) throws ResultCodeException {



        String dateStr = AppUtil.localDatetimeNowString();
        String endDateStr = "2099-12-31 23:59:59";

        Product product = productPrice.getProduct();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = dateFormat.parse(endDateStr);
            product.setEndDate(date);
        }catch (Exception e){
            logger.error(e.toString());
        }

        Set<ProductImage> imageList = product.getImageList();

        product.setMarketType("retail");
        product.setSalesType(6L);
        product.setStatus(1);
        product.setBlind(false);
        product.setPriceMethod("fix");
        product.setSurtax(true);
        product.setSalesTerm(true);
        product.setStartDate(new Date());
        product.setSoldCount(0);
        product.setCount(999999);
        product.setUseOption(false);
        product.setRegister(user.getLoginId());
        product.setRegisterType("user");
        product.setRegDatetime(dateStr);
        product.setModDatetime(dateStr);

        product = productRepository.saveAndFlush(product);

        if (imageList != null && imageList.size() > 0) {

            int i = 0;
            for (ProductImage image : imageList) {
                if(i == 0){
                    image.setDeligate(true);
                }else{
                    image.setDeligate(false);
                }
                i++;
                image.setProductSeqNo(product.getSeqNo());
                productImageRepository.save(image);
            }
        }

        String code = SetID.getID("R");
        productPrice.setCode(code);
        productPrice.setProductSeqNo(product.getSeqNo());

        productPrice.setDiscountUnit("percent");
        productPrice.setRegDatetime(dateStr);
        productPrice.setIsLuckyball(false);
        productPrice.setMarketType(2);

        Float salePrice = productPrice.getOriginPrice() - (int)(productPrice.getOriginPrice()*(productPrice.getDiscountRatio()/100));
        productPrice.setPrice(salePrice);
        productPrice.setDiscount(productPrice.getOriginPrice() - productPrice.getPrice());

        productPrice.setStatus(1);
        productPrice.setPick(false);
        productPrice.setTemp(false);


        productPrice.setIsTicket(true);
        productPrice.setIsSubscription(false);
        productPrice.setIsPrepayment(false);
        productPrice.setDailySoldCount(0);

        ProductPriceOnly productPriceOnly  = productPrice.convert();
        productPriceOnly = productPriceOnlyRepository.saveAndFlush(productPriceOnly);

        return productPriceOnly;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ProductPriceOnly updateSubscription(User user, ProductPrice productPrice) throws ResultCodeException {
        String dateStr = AppUtil.localDatetimeNowString();

        Product product = productPrice.getProduct();
        Product savedProduct = productRepository.findBySeqNo(product.getSeqNo());

        product.setMarketType("retail");
        product.setSalesType(7L);
        product.setStatus(1);
        product.setBlind(false);
        product.setPriceMethod("fix");
        product.setSurtax(true);
        product.setSalesTerm(true);
        product.setSoldCount(savedProduct.getSoldCount());
        product.setStartDate(savedProduct.getStartDate());
        product.setEndDate(savedProduct.getEndDate());
        product.setCount(savedProduct.getCount());
        product.setUseOption(false);
        product.setRegister(user.getLoginId());
        product.setRegisterType("user");
        product.setModDatetime(dateStr);
        product = productRepository.saveAndFlush(product);


        ProductPriceOnly savedProductPrice = productPriceOnlyRepository.findBySeqNo(productPrice.getSeqNo());

        productPrice.setCode(savedProductPrice.getCode());
        productPrice.setProductSeqNo(product.getSeqNo());

        productPrice.setDiscountUnit("percent");
        productPrice.setRegDatetime(savedProductPrice.getRegDatetime());
        productPrice.setDailySoldCount(savedProductPrice.getDailySoldCount());
        productPrice.setIsLuckyball(false);
        productPrice.setMarketType(2);

        Float salePrice = productPrice.getOriginPrice() - (int)(productPrice.getOriginPrice()*(productPrice.getDiscountRatio()/100));
        productPrice.setPrice(salePrice);
        productPrice.setDiscount(productPrice.getDiscountRatio());

        productPrice.setStatus(1);
        productPrice.setPick(false);
        productPrice.setTemp(false);


        productPrice.setIsSubscription(true);
        productPrice.setIsTicket(false);
        productPrice.setIsPrepayment(false);

        ProductPriceOnly productPriceOnly  = productPrice.convert();
        productPriceOnly = productPriceOnlyRepository.saveAndFlush(productPriceOnly);

        return productPriceOnly;

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ProductPriceOnly regSubscription(User user, ProductPrice productPrice) throws ResultCodeException {



        String dateStr = AppUtil.localDatetimeNowString();
        String endDateStr = "2099-12-31 23:59:59";

        Product product = productPrice.getProduct();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = dateFormat.parse(endDateStr);
            product.setEndDate(date);
        }catch (Exception e){
            logger.error(e.toString());
        }

        product.setMarketType("retail");
        product.setSalesType(7L);
        product.setStatus(1);
        product.setBlind(false);
        product.setPriceMethod("fix");
        product.setSurtax(true);
        product.setSalesTerm(true);
        product.setStartDate(new Date());
        product.setSoldCount(0);
        product.setCount(-1);
        product.setUseOption(false);
        product.setRegister(user.getLoginId());
        product.setRegisterType("user");
        product.setRegDatetime(dateStr);
        product.setModDatetime(dateStr);

        product = productRepository.saveAndFlush(product);

        String code = SetID.getID("R");
        productPrice.setCode(code);
        productPrice.setProductSeqNo(product.getSeqNo());

        productPrice.setDiscountUnit("percent");
        productPrice.setRegDatetime(dateStr);
        productPrice.setIsLuckyball(false);
        productPrice.setMarketType(2);

        Float salePrice = productPrice.getOriginPrice() - (int)(productPrice.getOriginPrice()*(productPrice.getDiscountRatio()/100));
        productPrice.setPrice(salePrice);
        productPrice.setDiscount(productPrice.getDiscountRatio());

        productPrice.setStatus(1);
        productPrice.setPick(false);
        productPrice.setTemp(false);

        productPrice.setIsSubscription(true);
        productPrice.setIsTicket(false);
        productPrice.setIsPrepayment(false);

        ProductPriceOnly productPriceOnly  = productPrice.convert();
        productPriceOnly = productPriceOnlyRepository.saveAndFlush(productPriceOnly);

        return productPriceOnly;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ProductPriceOnly updateMoney(User user, ProductPrice productPrice) throws ResultCodeException {
        String dateStr = AppUtil.localDatetimeNowString();

        Product product = productPrice.getProduct();
        Product savedProduct = productRepository.findBySeqNo(product.getSeqNo());

        product.setMarketType("retail");
        product.setSalesType(8L);
        product.setStatus(1);
        product.setBlind(false);
        product.setPriceMethod("fix");
        product.setSurtax(true);
        product.setSalesTerm(true);
        product.setSoldCount(savedProduct.getSoldCount());
        product.setStartDate(savedProduct.getStartDate());
        product.setEndDate(savedProduct.getEndDate());
        product.setCount(savedProduct.getCount());
        product.setUseOption(false);
        product.setRegister(user.getLoginId());
        product.setRegisterType("user");
        product.setModDatetime(dateStr);
        product = productRepository.saveAndFlush(product);


        ProductPriceOnly savedProductPrice = productPriceOnlyRepository.findBySeqNo(productPrice.getSeqNo());

        productPrice.setCode(savedProductPrice.getCode());
        productPrice.setProductSeqNo(product.getSeqNo());

        productPrice.setRegDatetime(savedProductPrice.getRegDatetime());
        productPrice.setDailySoldCount(savedProductPrice.getDailySoldCount());
        productPrice.setIsLuckyball(false);
        productPrice.setMarketType(2);

        Float discountRatio = 100 - (productPrice.getPrice() / productPrice.getOriginPrice() * 100);
        productPrice.setDiscountRatio(discountRatio);
        productPrice.setIsDiscount(true);
        productPrice.setDiscount(discountRatio);
        productPrice.setDiscountUnit("percent");
        productPrice.setRemainDays(365);

        productPrice.setStatus(1);
        productPrice.setPick(false);
        productPrice.setTemp(false);


        productPrice.setIsSubscription(false);
        productPrice.setIsTicket(false);
        productPrice.setIsPrepayment(true);

        ProductPriceOnly productPriceOnly  = productPrice.convert();
        productPriceOnly = productPriceOnlyRepository.saveAndFlush(productPriceOnly);

        return productPriceOnly;

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ProductPriceOnly regMoney(User user, ProductPrice productPrice) throws ResultCodeException {



        String dateStr = AppUtil.localDatetimeNowString();
        String endDateStr = "2099-12-31 23:59:59";

        Product product = productPrice.getProduct();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = dateFormat.parse(endDateStr);
            product.setEndDate(date);
        }catch (Exception e){
            logger.error(e.toString());
        }

        product.setMarketType("retail");
        product.setSalesType(8L);
        product.setStatus(1);
        product.setBlind(false);
        product.setPriceMethod("fix");
        product.setSurtax(true);
        product.setSalesTerm(true);
        product.setStartDate(new Date());
        product.setSoldCount(0);
        product.setCount(-1);
        product.setUseOption(false);
        product.setRegister(user.getLoginId());
        product.setRegisterType("user");
        product.setRegDatetime(dateStr);
        product.setModDatetime(dateStr);

        product = productRepository.saveAndFlush(product);

        String code = SetID.getID("R");
        productPrice.setCode(code);
        productPrice.setProductSeqNo(product.getSeqNo());

        productPrice.setRegDatetime(dateStr);
        productPrice.setIsLuckyball(false);
        productPrice.setMarketType(2);

        Float discountRatio = 100 - (productPrice.getPrice() / productPrice.getOriginPrice() * 100);
        productPrice.setDiscountRatio(discountRatio);
        productPrice.setIsDiscount(true);
        productPrice.setDiscount(discountRatio);
        productPrice.setDiscountUnit("percent");
        productPrice.setRemainDays(365);

        productPrice.setStatus(1);
        productPrice.setPick(false);
        productPrice.setTemp(false);

        productPrice.setIsSubscription(false);
        productPrice.setIsTicket(false);
        productPrice.setIsPrepayment(true);


        ProductPriceOnly productPriceOnly  = productPrice.convert();
        productPriceOnly = productPriceOnlyRepository.saveAndFlush(productPriceOnly);

        return productPriceOnly;
    }

    public Page<ProductPrice> getProductPriceListSubscriptionTypeByPageSeqNo(Long pageSeqNo, Pageable pageable) throws ResultCodeException {
        return productPriceRepository.findAllSubscriptionTypeByPageSeqNo(pageSeqNo, pageable);
    }


    public Page<ProductPrice> getProductPriceListSubscriptionTypeByPageSeqNoOnlyNormal(Long pageSeqNo, Pageable pageable) throws ResultCodeException {
        return productPriceRepository.findAllSubscriptionTypeByPageSeqNoOnlyNormal(pageSeqNo, pageable);
    }

    public Page<ProductPrice> getProductPriceListMoneyTypeByPageSeqNoOnlyNormal(Long pageSeqNo, Pageable pageable) throws ResultCodeException {
        return productPriceRepository.findAllMoneyTypeByPageSeqNoOnlyNormal(pageSeqNo, pageable);
    }

    public ProductPrice getLastSubscriptionTypeByPageSeqNoOnlyNormal(Long pageSeqNo) throws ResultCodeException {
        return productPriceRepository.findLastSubscriptionTypeByPageSeqNoOnlyNormal(pageSeqNo);
    }

    public ProductPrice getLastMoneyTypeByPageSeqNoOnlyNormal(Long pageSeqNo) throws ResultCodeException {
        return productPriceRepository.findLastMoneyTypeByPageSeqNoOnlyNormal(pageSeqNo);
    }

    public Page<ProductPrice> getProductPriceListMoneyTypeByPageSeqNo(Long pageSeqNo, Pageable pageable) throws ResultCodeException {
        return productPriceRepository.findAllMoneyTypeByPageSeqNo(pageSeqNo, pageable);
    }
}
