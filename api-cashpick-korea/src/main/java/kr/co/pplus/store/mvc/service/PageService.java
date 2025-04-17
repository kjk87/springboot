package kr.co.pplus.store.mvc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pplus.store.api.jpa.model.PageGoodsCategory;
import kr.co.pplus.store.api.jpa.model.PageGoodsInfo;
import kr.co.pplus.store.api.jpa.model.PageImage;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkAddRequest;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayCommonResponse;
import kr.co.pplus.store.api.jpa.repository.PageGoodsInfoRepository;
import kr.co.pplus.store.api.jpa.repository.PageImageRepository;
import kr.co.pplus.store.api.jpa.service.BusinessLicenseService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.FTLinkPayApi;
import kr.co.pplus.store.util.RedisUtil;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@Transactional(transactionManager = "transactionManager")
public class PageService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(PageService.class);

    @Autowired
    ArticleService articleSvc;

    @Autowired
    FanService fanSvc;

    @Autowired
    AttachmentService attachSvc;

    @Autowired
    ContactService contactSvc;

    @Autowired
    MsgService msgSvc;

    @Autowired
    QueueService queueSvc;

    @Autowired
    ObjectMapper om;

    @Autowired
    CommonService commonService;

    @Value("${STORE.DAUM_COORD_URL}")
    String DAUM_COORD_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    @Value("${STORE.DAUM_COORD_KEY}")
    String DAUM_COORD_KEY = "8da7c5baa82bdb282190a323710ca8cf";

    @Value("${STORE.REDIS_PREFIX}")
    static String REDIS_PREFIX = "pplus-";

    @Value("${STORE.TYPE}")
    String storeType = "LOCAL" ;

    @Autowired
    private PageImageRepository pageImageRepository;

    @Autowired
    PageGoodsInfoRepository pageGoodsInfoRepository ;

    @Autowired
    BusinessLicenseService businessLicenseService;

//	@Autowired
//	PageGoodsCategoryRepository pageGoodsCategoryRepository ;

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer insert(Page page, String loginId, String password, String mobile) throws ResultCodeException {
        page.setAuthCode("1111");
        page.setGoodsNotiType("text");
        page.setPhone(StoreUtil.getValidatePhoneNumber(page.getPhone()));

        if(page.getStoreType() == null){
            page.setStoreType("offline");
        }

        if (page.getCoopStatus() == null)
            page.setCoopStatus("normal");

        if (page.getTotalViewCount() == null)
            page.setTotalViewCount(0L);

        if (page.getTodayViewCount() == null)
            page.setTodayViewCount(0L);

        if (page.getCustomerCount() == null)
            page.setCustomerCount(0);

        if (page.getPlusCount() == null)
            page.setPlusCount(0);

        if (page.getLevel() == null)
            page.setLevel(1);

        if (page.getValuationCount() == null)
            page.setValuationCount(0L);

        if (page.getValuationPoint() == null)
            page.setValuationPoint(0L);

        if (page.getIncorrectAuthCodeCount() == null)
            page.setIncorrectAuthCodeCount(0);

        if (page.getIsLink() == null)
            page.setIsLink(false);

        if (page.getIsSeller() == null)
            page.setIsSeller(false);

        if (page.getIsShopOrderable() == null)
            page.setIsShopOrderable(false);

        if (page.getIsDeliveryOrderable() == null)
            page.setIsDeliveryOrderable(false);

        if (page.getIsPackingOrderable() == null)
            page.setIsPackingOrderable(false);

        if (page.getIsHolidayClosed() == null)
            page.setIsHolidayClosed(false);

        if (page.getIsParkingAvailable() == null) {
            page.setIsParkingAvailable(false);
        }
        if (page.getIsValetParkingAvailable() == null) {
            page.setIsValetParkingAvailable(false);
        }
        if (page.getIsChain() == null) {
            page.setIsChain(false);
        }
        if (page.getIsDelivery() == null) {
            page.setIsDelivery(false);
        }

        if (page.getUsePrnumber() == null)
            page.setUsePrnumber(false);

        if(page.getMarketType() == null){
            page.setMarketType("retail");
        }

        Attachment att = new Attachment();
        att.setTargetType("pageBackground");
        att = attachSvc.getRandomImage(att);
        page.setBackgroundImageUrl(att.getUrl());
        logger.info("page : " + page.toString());
        if (page.getHashtag() != null) {
            page.setHashtag(page.getHashtag().replaceAll("#", ",")
                    .replaceAll("^,", "")
                    .replaceAll("\\s+", "")
                    .replaceAll("[\\{\\}\\[\\]\\/?.;:|\\)*~`!^\\-_+<>@\\#$%&\\\\\\=\\(\\'\\\"]", "")
                    .replaceAll(",,", ","));

            String[] hashtags = page.getHashtag().split(",");
            for (String hashtag : hashtags) {
                String exist = RedisUtil.getInstance().hGet(REDIS_PREFIX + "hashtag", hashtag.trim());
                if (exist == null) {
                    RedisUtil.getInstance().hSet(REDIS_PREFIX + "hashtag", hashtag.trim(), "1");
                }
            }
        }

        BusinessLicense businessLicense = page.getBusinessLicense();


        try {
            if(businessLicense != null){
                FTLinkAddRequest ftLinkAddRequest = new FTLinkAddRequest();

                ftLinkAddRequest.setDistid("pplus3");
                ftLinkAddRequest.setLoginid(loginId);
                ftLinkAddRequest.setLoginpwd(password);

                ftLinkAddRequest.setShopname(page.getName());
                if(businessLicense.getBusinessOperatorType().equals("individual")){
                    ftLinkAddRequest.setShop_type("1");
                }else{
                    ftLinkAddRequest.setShop_type("2");
                }
                ftLinkAddRequest.setShop_bizowner(businessLicense.getCeo());
                ftLinkAddRequest.setShop_cust_tel(page.getPhone());
                ftLinkAddRequest.setShop_bizname(businessLicense.getCompanyName());
                ftLinkAddRequest.setShop_bizno(businessLicense.getCorporateNumber());
                ftLinkAddRequest.setShop_bizaddr(businessLicense.getCompanyAddress());
                ftLinkAddRequest.setShop_cust_hp(mobile);

                FTLinkPayCommonResponse ftLinkPayCommonResponse = FTLinkPayApi.add(ftLinkAddRequest);
                if(ftLinkPayCommonResponse.getErrcode().equals("0000") || ftLinkPayCommonResponse.getErrcode().equals("00")){
                    page.setShopCode(ftLinkPayCommonResponse.getShopcode());
                }
            }

        }catch (Exception e){
            logger.error("ftLinkAddRequest error : " + e.toString());
        }


        int effected = sqlSession.insert("Page.insert", page);
        if (effected > 0) {

            //PR 게시판과 리뷰 게시판을 생성한다.
            BulletinBoard prBoard = new BulletinBoard();
            prBoard.setType("page_pr");
            articleSvc.insertBoard(prBoard);

            BulletinBoard reviewBoard = new BulletinBoard();
            reviewBoard.setType("page_review");
            articleSvc.insertBoard(reviewBoard);

            articleSvc.bindPageAndBoards(page, prBoard, reviewBoard);

            FanGroup fanGroup = new FanGroup();
            fanGroup.setDefaultGroup(true);
            fanGroup.setName("ALL");
            fanGroup.setPriority(100);
            fanGroup.setPage(page);
            fanSvc.insertGroup(fanGroup);
			
			/*if (!StringUtils.isEmpty(page.getSearchKeyword())) {
				updateSearchKeywordAll(page);
			}*/

            int exists = sqlSession.selectOne("Page.existsBindCategory", page);
            bindCategoryAndPage(page);

            PageGoodsCategory pgc = new PageGoodsCategory();
            pgc.setGoodsCategorySeqNo(1L);
            pgc.setPageSeqNo(page.getNo());
            pgc.setSeqNo(null);

            // Mybatis Transaction Manager 와 Jpa Trsaction Manger 동시 사용시 Foreign Key sync 가 안 맞는 경우가 있음
            //pageGoodsCategoryRepository.saveAndFlush(pgc) ;

            sqlSession.insert("Page.insertPageGoodsCategory", pgc);

            businessLicense.setPage(page.getNo());
            sqlSession.insert("Page.insertBusinessLicense", businessLicense);

//            if(businessLicense != null){
//                kr.co.pplus.store.api.jpa.model.BusinessLicense jpaBusinessLicense = new kr.co.pplus.store.api.jpa.model.BusinessLicense();
//                jpaBusinessLicense.setCompanyName(businessLicense.getCompanyName());
//                jpaBusinessLicense.setPage(page.getNo());
//                jpaBusinessLicense.setCeo(businessLicense.getCeo());
//                jpaBusinessLicense.setCorporateNumber(businessLicense.getCorporateNumber());
//                jpaBusinessLicense.setCompanyAddress(businessLicense.getCompanyAddress());
//                jpaBusinessLicense.setBusinessOperatorType(businessLicense.getBusinessOperatorType());
//                businessLicenseService.insertOrUpdate(jpaBusinessLicense);
//            }

        }
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer update(Page page) throws ResultCodeException {

        if (page.getAddress() != null && page.getLatitude() == null && page.getLongitude() == null) {
            Coord c = null;
            try {
                if (page.getAddress().getRoadBase() != null)
                    c = StoreUtil.converAddressToCoord(om, DAUM_COORD_URL, DAUM_COORD_KEY, page.getAddress().getRoadBase());
                else
                    c = StoreUtil.converAddressToCoord(om, DAUM_COORD_URL, DAUM_COORD_KEY, page.getAddress().getParcelBase());

                if (c != null) {
                    page.setLatitude(c.getY());
                    page.setLongitude(c.getX());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (page.getHashtag() != null) {
            page.setHashtag(page.getHashtag().replaceAll("#", ",")
                    .replaceAll("^,", "")
                    .replaceAll("\\s+", "")
                    .replaceAll("[\\{\\}\\[\\]\\/?.;:|\\)*~`!^\\-_+<>@\\#$%&\\\\\\=\\(\\'\\\"]", "")
                    .replaceAll(",,", ","));

            String[] hashtags = page.getHashtag().split(",");
            for (String hashtag : hashtags) {
                String exist = RedisUtil.getInstance().hGet(REDIS_PREFIX + "hashtag", hashtag.trim());
                if (exist == null) {
                    RedisUtil.getInstance().hSet(REDIS_PREFIX + "hashtag", hashtag.trim(), "1");
                }
            }
        }

        int effected = sqlSession.update("Page.update", page);
        if (effected > 0 && page.getUser() != null) {
            bindCategoryAndPage(page);
			
			/*if (!StringUtils.isEmpty(page.getSearchKeyword())) {
				updateSearchKeywordAll(page);
			}*/
        }
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    private void bindCategoryAndPage(Page page) {
        int exists = sqlSession.selectOne("Page.existsBindCategory", page);
        if (exists > 0) {
            sqlSession.delete("Page.deleteBindCategoryAll", page);
        }

        if (page.getCategory() != null) {
            sqlSession.insert("Page.bindCategoryAndPage", page);
        } else {
            PageCategory category = new PageCategory();
            category.setNo(53L);
            page.setCategory(category);
            sqlSession.insert("Page.bindCategoryAndPage", page);
        }
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer activate(Page page) throws ResultCodeException {

        if (page.getAddress() != null && page.getLatitude() == null && page.getLongitude() == null) {
            Coord c = null;
            try {
                if (page.getAddress().getRoadBase() != null)
                    c = StoreUtil.converAddressToCoord(om, DAUM_COORD_URL, DAUM_COORD_KEY, page.getAddress().getRoadBase());
                else
                    c = StoreUtil.converAddressToCoord(om, DAUM_COORD_URL, DAUM_COORD_KEY, page.getAddress().getParcelBase());

                if (c != null) {
                    page.setLatitude(c.getY());
                    page.setLongitude(c.getX());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        //update와 다른 점은 등록일을 현재로 변경한다.
        int effected = sqlSession.update("Page.activate", page);
        if (effected > 0 && page.getUser() != null) {
            bindCategoryAndPage(page);

            if (!StringUtils.isEmpty(page.getSearchKeyword())) {
                updateSearchKeywordAll(page);
            }
        }
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public Integer updateSeller(Long seqNo, Boolean isSeller) throws ResultCodeException {

        Page page = new Page();
        page.setNo(seqNo);
        page.setIsSeller(isSeller);
        int effected = sqlSession.update("Page.updateSeller", page);

        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }


    public Integer start(User user) throws ResultCodeException {
        Page saved = sqlSession.selectOne("Page.getPageByUser", user);
        if (saved == null)
            throw new NotFoundTargetException("user", "page not found");

        if (!saved.getStatus().equals("ready"))
            throw new InvalidArgumentException("status", "not ready.");

        saved.setStatus("normal");

        int effected = sqlSession.update("Page.updateStatus", saved);

        user.setPage(saved);

        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public Integer requestApproval(User user) throws ResultCodeException {
        Page saved = sqlSession.selectOne("Page.getPageByUser", user);
        if (saved == null)
            throw new NotFoundTargetException("user", "page not found");

        if (saved.getStatus().equals("pending"))
            throw new InvalidArgumentException("status", "pending. required activate.");

        saved.setStatus("reqApproval");

        int effected = sqlSession.update("Page.updateStatus", saved);

        user.setPage(saved);

        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }


    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateProperties(User user, Page page) throws ResultCodeException {
        Page saved = sqlSession.selectOne("Page.getPageByUser", user);
        if (saved == null)
            throw new NotFoundTargetException("user", "page not found");

        if (!saved.getNo().equals(page.getNo()))
            throw new NotPermissionException("user", "not page owner");

        if (page.getProperties() == null)
            throw new NotFoundTargetException("properties", "empty");

        for (Map.Entry<String, Object> entry : page.getProperties().entrySet()) {
            updateProperty(saved, entry.getKey(), entry.getValue());
        }
        page.setProperties(saved.getProperties());
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updatePropertiesAll(User user, Page page) throws ResultCodeException {
        Page saved = sqlSession.selectOne("Page.getPageByUser", user);
        if (saved == null)
            throw new NotFoundTargetException("user", "page not found");

        if (!saved.getNo().equals(page.getNo()))
            throw new NotPermissionException("user", "not page owner");

        sqlSession.update("Page.updateProperties", page);
        return Const.E_SUCCESS;
    }

    private int updateProperty(Page saved, String key, Object value) {
        if (value == null) {
            if (saved.getProperties() != null &&
                    saved.getProperties().containsKey(key)) {
                saved.getProperties().remove(key);
            }
        } else {
            if (saved.getProperties() == null)
                saved.setProperties(new HashMap<String, Object>());

            saved.getProperties().put(key, value);
        }
        return sqlSession.update("Page.updateProperties", saved);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateSearchKeywordAll(Page page) {
        if (!page.getSearchKeyword().isEmpty()) {
            String[] tags = page.getSearchKeyword().split("\\#");
            if (tags != null && tags.length > 0) {
                sqlSession.delete("Page.deleteBindSearchKeywordAll", page);
                for (int i = 0; i < tags.length; i++) {
                    String tag = tags[i].trim();
                    if (!StringUtils.isEmpty(tag)) {
                        bindSearchKeyword(page, tag);
                    }
                }
            }
        }
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void bindSearchKeyword(Page page, String text) {
        int exists = sqlSession.selectOne("Page.existsSearchKeyword", text);

        if (exists == 0) {
            SearchKeyword keyword = new SearchKeyword();
            keyword.setKeyword(text);
            keyword.setRegUser(page.getUser());
            sqlSession.insert("Page.insertSearchKeyword", keyword);
        }

        ParamMap map = new ParamMap();
        map.put("page", page);
        map.put("keyword", text);
        sqlSession.selectOne("Page.bindSearchKeywordAndPage", map); //MGK page, text);
    }

    public Integer delete(Page page) {
        int effected = sqlSession.delete("Page.delete", page);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateProfileImage(Page page) throws ResultCodeException {
        Page saved = sqlSession.selectOne("Page.getPage", page);
        if (saved == null)
            throw new UnknownException();

        if (saved.getProfileImage() != null) {
            if (page.getProfileImage() == null) {
                attachSvc.delete(saved.getProfileImage());
            } else if (!page.getProfileImage().getNo().equals(saved.getProfileImage().getNo())) {
                attachSvc.delete(saved.getProfileImage());
            }
        }
        if(page.getProfileImage() != null){
            Attachment attachment = sqlSession.selectOne("Attachment.get", page.getProfileImage());
            page.setThumbnail(attachment.getUrl());
        }

        int effected = sqlSession.update("Page.updateProfileImage", page);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public String updateQrImage(Page page) throws ResultCodeException {

        if( storeType.equals("PROD") ) {
            page.setQrImage(commonService.makeQrCode("https://www.plusmember.co.kr/shop/"+page.getNo()+"/qr"));
        }else{
            page.setQrImage(commonService.makeQrCode("https://stg-www.plusmember.co.kr/shop/"+page.getNo()+"/qr"));
        }


        int effected = sqlSession.update("Page.updateQrImage", page);
        return effected > 0 ? page.getQrImage() : null;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateThumbnail(Page page) throws ResultCodeException {
        int effected = sqlSession.update("Page.updateThumbnail", page);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateBackgroundImage(Page page) throws ResultCodeException {
        Page saved = sqlSession.selectOne("Page.getPage", page);
        if (saved == null)
            throw new UnknownException();

//        if (saved.getBackgroundImage() != null) {
//            if (page.getBackgroundImage() == null) {
//                attachSvc.delete(saved.getBackgroundImage());
//            } else if (!page.getBackgroundImage().getNo().equals(saved.getBackgroundImage().getNo())) {
//                attachSvc.delete(saved.getBackgroundImage());
//            }
//        }

        int effected = sqlSession.update("Page.updateBackgroundImage", page);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;

    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updatePagePoint(Page page) throws ResultCodeException {
        int effected = sqlSession.update("Page.updatePagePoint", page);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateMainMovie(Page page) throws ResultCodeException {
        int effected = sqlSession.update("Page.updateMainMovie", page);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public List<PageIntroImage> getIntroImageAll(Page page) {
        return sqlSession.selectList("Page.getIntroImageAll", page);
    }

    public List<PageImage> getPageImageAll(Page page){
        return  pageImageRepository.findByPageSeqNoOrderByArrayAsc(page.getNo());
    }

    public List<PageIntroMovie> getIntroMovieAll(Page page) {
        return sqlSession.selectList("Page.getIntroMovieAll", page);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateIntroImageList(Page page) throws ResultCodeException {
        List<PageIntroImage> prevList = getIntroImageAll(page);
        if (prevList != null) {
            if (page.getIntroImageList() != null) {
                for (PageIntroImage intro : page.getIntroImageList()) {
                    exceptImage(prevList, intro);
                }
            }

            sqlSession.delete("Page.deallocateIntroImageAll", page);

            for (PageIntroImage prev : prevList) {
                attachSvc.delete(prev);
            }
        }
        pageImageRepository.deleteAllByPageSeqNo(page.getNo());
        if (page.getIntroImageList() != null && page.getIntroImageList().size() > 0) {
            int array = 0;
            PageImage pageImage;
            for (PageIntroImage intro : page.getIntroImageList()) {
                array++;
                ParamMap map = new ParamMap();
                map.put("page", page);
                map.put("intro", intro);
                sqlSession.insert("Page.allocateIntroImage", map); //MGK page, intro);
                Attachment attachment = sqlSession.selectOne("Attachment.get", intro.getNo());
                pageImage = new PageImage();
                pageImage.setPageSeqNo(page.getNo());
                pageImage.setImage(attachment.getUrl());
                pageImage.setArray(array);
                pageImageRepository.save(pageImage);
            }
        }
        return Const.E_SUCCESS;

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updatePageImageList(List<PageImage> pageImageList) throws ResultCodeException {
        if(pageImageList != null && pageImageList.size() > 0){
            pageImageRepository.deleteAllByPageSeqNo(pageImageList.get(0).getPageSeqNo());
            for(PageImage pageImage : pageImageList){
                pageImage.setId(null);
                pageImageRepository.save(pageImage);
            }
        }
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer deletePageImage(PageImage pageImage) throws ResultCodeException {
        pageImageRepository.deleteById(pageImage.getId());

        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateIntroMovieList(Page page) throws ResultCodeException {
        sqlSession.delete("Page.deallocateIntroMovieAll", page);
        if (page.getIntroMovieList() != null) {
            short i = 1;
            for (PageIntroMovie intro : page.getIntroMovieList()) {
                intro.setNo(i++);
                ParamMap map = new ParamMap();
                map.put("page", page);
                map.put("intro", intro);
                sqlSession.insert("Page.allocateIntroMovie", map); //MGK page, intro);
            }
        }
        return Const.E_SUCCESS;

    }

    private void exceptImage(List<PageIntroImage> src, PageIntroImage dest) {
        int idx = -1;
        for (int j = 0; j < src.size(); j++) {
            PageIntroImage s = src.get(j);
            if (dest.getNo().equals(s.getNo())) {
                idx = j;
                break;
            }
        }

        if (idx >= 0)
            src.remove(idx);
        return;

    }

    public Integer updateIntroImages(Page page) throws ResultCodeException {
        return Const.E_NOTIMPLEMENT;
    }

    public Page getPageByUser(User user) {
        return sqlSession.selectOne("Page.getPageByUser", user);
    }

    public Page getPage(Page page) throws NotFoundTargetException {
        Page dbVal = sqlSession.selectOne("Page.getPage", page);
        if (dbVal == null)
            throw new NotFoundTargetException();
        return dbVal;
    }

    public Page getPlusPage(User user, Page page) throws NotFoundTargetException {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", page);
        Page dbVal = sqlSession.selectOne("Page.getPlusPage", map); //MGK user, page);
        if (dbVal == null)
            throw new NotFoundTargetException();
        return dbVal;
    }

    public Page getPageWithUser(Page page) throws NotFoundTargetException {
        Page dbVal = sqlSession.selectOne("Page.getPageWithUser", page);
        if (dbVal == null)
            throw new NotFoundTargetException();
        return dbVal;
    }

    public List<Page> getPageAllByStatus(SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("opt", opt);
        return sqlSession.selectList("Page.getPageAllByStatus", map);
    }

    public void insertPageSalesType(PageSalesType pageSalesType){
        sqlSession.insert("Page.insertPageSalesType", pageSalesType);
    }

    public boolean existPageSalesType(Long pageSeqNo){
        return ((Integer) sqlSession.selectOne("Page.existsPageSalesType", pageSeqNo) > 0) ? true : false;
    }

    public boolean existsPage(long pageNo) {
        return ((Integer) sqlSession.selectOne("Page.existsPage", pageNo) > 0) ? true : false;
    }

    public boolean existsNormalPage(User user) {
        return ((Integer) sqlSession.selectOne("Page.existsNormalPage", user) > 0) ? true : false;
    }

    public boolean existsPendingPage(User user) {
        return ((Integer) sqlSession.selectOne("Page.existsPendingPage", user) > 0) ? true : false;
    }

    public List<PageCategory> getCategoryAll(PageCategory category) {
        if (StringUtils.isEmpty(category.getType()))
            category.setType("store");
        if (category.getThema() == null)
            category.setThema(false);
        return sqlSession.selectList("Page.getCategoryAll", category);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer saveSnsLink(User user, SnsLink snsLink) throws ResultCodeException {
        Page saved = sqlSession.selectOne("Page.getPageByUser", user);
        if (saved == null)
            throw new NotFoundTargetException("user", "page not found");

        if (!saved.getNo().equals(snsLink.getPage().getNo()))
            throw new NotPermissionException("user", "not page owner");

        int exists = sqlSession.selectOne("Page.existsSysLinkByType", snsLink);
        int effected = 0;
        if (exists == 0) {
            snsLink.setNo((Integer) sqlSession.selectOne("Page.getNewSnsLinkNo", snsLink.getPage()));
            effected = sqlSession.insert("Page.insertSnsLink", snsLink);
        } else {
            effected = updateSnsLinkByType(user, snsLink);
        }
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateSnsLink(User user, SnsLink snsLink) throws ResultCodeException {
        Page saved = sqlSession.selectOne("Page.getPageByUser", user);
        if (saved == null)
            throw new NotFoundTargetException("user", "page not found");

        if (!saved.getNo().equals(snsLink.getPage().getNo()))
            throw new NotPermissionException("user", "not page owner");

        int effected = sqlSession.update("Page.updateSnsLink", snsLink);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateSnsLinkByType(User user, SnsLink snsLink) throws ResultCodeException {
        SnsLink saved = sqlSession.selectOne("Page.getSnsLinkByType", snsLink);
        snsLink.setNo(saved.getNo());
        return updateSnsLink(user, snsLink);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer deleteSnsLink(User user, SnsLink snsLink) throws ResultCodeException {
        Page saved = sqlSession.selectOne("Page.getPageByUser", user);
        if (saved == null)
            throw new NotFoundTargetException("user", "page not found");

        if (!saved.getNo().equals(snsLink.getPage().getNo()))
            throw new NotPermissionException("user", "not page owner");

        int effected = sqlSession.delete("Page.deleteSnsLink", snsLink);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer deleteSnsLinkByType(User user, SnsLink snsLink) throws ResultCodeException {
        SnsLink saved = sqlSession.selectOne("Page.getSnsLinkByType", snsLink);
        snsLink.setNo(saved.getNo());
        return deleteSnsLink(user, snsLink);
    }

    public List<SnsLink> getSnsLinkAll(Page page) {
        return sqlSession.selectList("Page.getSnsLinkAll", page);
    }

    public SnsLink getSnsLink(SnsLink snsLink) throws ResultCodeException {
        SnsLink saved = sqlSession.selectOne("Page.getSnsLink", snsLink);
        if (saved == null)
            throw new NotFoundTargetException();
        return saved;
    }

    public SnsLink getSnsLinkByType(SnsLink snsLink) throws ResultCodeException {
        SnsLink saved = sqlSession.selectOne("Page.getSnsLinkByType", snsLink);
        if (saved == null)
            throw new NotFoundTargetException();
        return saved;
    }

    public Integer increaseCustomerCount(Page page, Integer count) {
        ParamMap map = new ParamMap();
        map.put("page", page);
        map.put("count", count);
        int effected = sqlSession.update("Page.increaseCustomerCount", map); //MGK page, count);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public Integer decreaseCustomerCount(Page page, Integer count) {
        ParamMap map = new ParamMap();
        map.put("page", page);
        map.put("count", count);
        int effected = sqlSession.update("Page.decreaseCustomerCount", map); //MGK page, count);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public Integer increasePlusCount(Page page, Integer count) {
        ParamMap map = new ParamMap();
        map.put("page", page);
        map.put("count", count);
        int effected = sqlSession.update("Page.increasePlusCount", map); //MGK page, count);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public Integer decreasePlusCount(Page page, Integer count) {
        ParamMap map = new ParamMap();
        map.put("page", page);
        map.put("count", count);
        int effected = sqlSession.update("Page.decreasePlusCount", map); //MGK page, count);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public Integer increaseValuation(Page page, Integer point) {
        ParamMap map = new ParamMap();
        map.put("page", page);
        map.put("point", point);
        int effected = sqlSession.update("Page.increaseValuation", map); //MGK page, point);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public Integer decreaseValuation(Page page, Integer point) {
        ParamMap map = new ParamMap();
        map.put("page", page);
        map.put("point", point);
        int effected = sqlSession.update("Page.decreaseValuation", map); //MGK page, point);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    private boolean existsPageAction(User user, Page page) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", page);
        int exists = sqlSession.selectOne("Page.existsPageAction", map); //MGK user, page);
        return exists > 0 ? true : false;
    }


    public Integer insertPageAction(PageAction action) {
        int effected = sqlSession.insert("Page.insertPageAction", action);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public int updatePageAction(PageAction action) {
        int effected = sqlSession.update("Page.updatePageAction", action);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public PageAction getPageAction(User user, Page page) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", page);
        return sqlSession.selectOne("Page.getPageAction", map); //MGK user, page);
    }

    public PageAction getPageActionByReview(User user, Article review) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("review", review);
        return sqlSession.selectOne("Page.getPageActionByReview", map); //MGK user, review);
    }


    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public PageAction giveReviewBol(Page page, User user) {
        PageAction action = null;
        if (!existsPageAction(user, page)) {
            action = new PageAction();
            action.setPage(page);
            action.setUser(user);
            action.setRecvReviewBol(true);
            action.setUseCount(0);
            insertPageAction(action);
        } else {
            action = getPageAction(user, page);
            if (!action.getRecvReviewBol()) {
                action.setRecvReviewBol(true);
                updatePageAction(action);
            }
        }
        return action;
    }

    public int getPageCountByKeyword(SearchOpt opt) {
        return sqlSession.selectOne("Page.getPageCountByKeyword", opt);
    }

    public List<Page> getPageListByKeyword(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectList("Page.getPageListByKeyword", map); //MGK user, opt);
    }

    public int getPageCountByArea(User user, MapArea area, Long categoryMinorSeqNo, Long categoryMajorSeqNo, SearchOpt opt, Boolean isPoint, Boolean isCoupon) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("area", area);
        map.put("opt", opt);
        map.put("isPoint", isPoint);
        map.put("isCoupon", isCoupon);
        map.put("categoryMinorSeqNo", categoryMinorSeqNo);
        map.put("categoryMajorSeqNo", categoryMajorSeqNo);
        return sqlSession.selectOne("Page.getPageCountByArea", map);
//        if (category == null || category.getNo() == null) {
//            return sqlSession.selectOne("Page.getPageCountByArea", map); //MGK user, area, opt);
//        } else {
//            map.put("category", category);
//            return sqlSession.selectOne("Page.getPageCountByAreaByCategory", map); //MGK user, area, opt);
//        }
    }

    public List<Page> getPageListByArea(User user, MapArea area, Long categoryMinorSeqNo, Long categoryMajorSeqNo, SearchOpt opt, GeoPosition position, Boolean isPoint) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("area", area);
        map.put("opt", opt);
        map.put("position", position);
        map.put("isPoint", isPoint);
        map.put("categoryMinorSeqNo", categoryMinorSeqNo);
        map.put("categoryMajorSeqNo", categoryMajorSeqNo);
        return sqlSession.selectList("Page.getPageListByArea", map);

    }

    public List<Page> getPageListByAreaAndManageSeqNo(User user, MapArea area, Long manageSeqNo, SearchOpt opt, GeoPosition position) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("area", area);
        map.put("opt", opt);
        map.put("position", position);
        map.put("manageSeqNo", manageSeqNo);
        return sqlSession.selectList("Page.getPageListByAreaAndManageSeqNo", map);

    }

    public Page getPageByNumber(User user, SearchOpt opt) throws ResultCodeException {
        if (opt.getSearch() == null || !opt.getSearch().matches("^[\\d]+$"))
            throw new InvalidArgumentException("argument", "empty or not number format.");

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectOne("Page.getPageByNumber", map);
    }

    public int getPageCountByAreaByTheme(User user, MapArea area, Long themeSeqNo) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("area", area);
        map.put("themeSeqNo", themeSeqNo);
        return sqlSession.selectOne("Page.getPageCountByAreaByTheme", map);
    }

    public List<Page> getPageListByAreaByTheme(User user, MapArea area, Long themeSeqNo, SearchOpt opt, GeoPosition position) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("area", area);
        map.put("opt", opt);
        map.put("position", position);
        map.put("themeSeqNo", themeSeqNo);
        return sqlSession.selectList("Page.getPageListByAreaByTheme", map);
    }

    public int getPageCountByTheme(User user, Long themeSeqNo, SearchOpt opt) {
//        ParamMap map = new ParamMap();
//        map.put("user", user);
//        map.put("opt", opt);
//        map.put("themeSeqNo", themeSeqNo);
        return sqlSession.selectOne("Page.getPageCountByTheme", themeSeqNo);
    }

    public List<Page> getPageListByTheme(User user, Long themeSeqNo, GeoPosition position, SearchOpt opt) {

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("position", position);
        map.put("opt", opt);
        map.put("themeSeqNo", themeSeqNo);
        return sqlSession.selectList("Page.getPageListByTheme", map);
    }

    public int getPageCount(User user, Long categoryMinorSeqNo, Long categoryMajorSeqNo, SearchOpt opt, String type, Boolean onlyPoint, String storeType) {
        if (opt.getSearch() != null && opt.getSearch().matches("^[\\d]+$")) {
            return getPageCountByNumber(user, opt);
        }
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        map.put("type", type);
        if(onlyPoint == null){
            onlyPoint = false;
        }
        map.put("onlyPoint", onlyPoint);
        map.put("categoryMinorSeqNo", categoryMinorSeqNo);
        map.put("categoryMajorSeqNo", categoryMajorSeqNo);
        map.put("storeType", storeType);
        return sqlSession.selectOne("Page.getPageCount", map);
    }

    public List<Page> getPageList(User user, Long categoryMinorSeqNo, Long categoryMajorSeqNo, GeoPosition position, SearchOpt opt, String type, Boolean onlyPoint, String storeType) {
        if (opt.getSearch() != null && opt.getSearch().matches("^[\\d]+$"))
            return getPageListByNumber(user, opt);

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("position", position);
        map.put("opt", opt);
        map.put("type", type);
        if(onlyPoint == null){
            onlyPoint = false;
        }
        map.put("onlyPoint", onlyPoint);
        map.put("categoryMinorSeqNo", categoryMinorSeqNo);
        map.put("categoryMajorSeqNo", categoryMajorSeqNo);
        map.put("storeType", storeType);
        return sqlSession.selectList("Page.getPageList", map);
    }

    public int getPageCountByManageSeqNo(User user, Long manageSeqNo) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("manageSeqNo", manageSeqNo);
        return sqlSession.selectOne("Page.getPageCountByManageSeqNo", map);
    }

    public List<Page> getPageListByManageSeqNo(User user, Long manageSeqNo, GeoPosition position, SearchOpt opt) {

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("position", position);
        map.put("manageSeqNo", manageSeqNo);
        map.put("opt", opt);
        return sqlSession.selectList("Page.getPageListByManageSeqNo", map);
    }

    public Page getRandomPage(User user) {
        SearchOpt opt = new SearchOpt();
        int count = getPageCount(user, null, null, opt, null, false, null);
        int totalPage = (count % opt.getSz() == 0) ? count / opt.getSz() : count / opt.getSz() + 1;
        opt.setPg(StoreUtil.getRandomNumber(totalPage - 1) + 1);

        List<Page> pageList = getPageList(user, null, null, new GeoPosition(), opt, null, false, null);
        if (pageList != null && pageList.size() > 0) {
            Collections.shuffle(pageList);
            return pageList.get(0);
        }
        return null;
    }

    public int getPageCountByNumber(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectOne("Page.getPageCountByNumber", map); //MGK user, opt);
    }

    public List<Page> getPageListByNumber(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectList("Page.getPageListByNumber", map); //MGK user, opt);
    }

    public List<Page> getActivatePageAllByRegDate(Duration duration) {
        ParamMap map = new ParamMap();
        map.put("duration", duration);
        return sqlSession.selectList("Page.getActivatePageAllByRegDate", duration);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void alertOpenPage(Page page) throws ResultCodeException {
        Page p = page;
        if (page.getUser() == null)
            p = sqlSession.selectOne("Page.getPageWithUser", page);

        List<User> list = contactSvc.getReverseFriendAll(page.getUser());
        for (User user : list) {
            if (user.getPage() == null || user.getPage().getNo() == null)
                continue;

            MsgOnly msg = new MsgOnly();
            msg.setInput("system");
            msg.setStatus("ready");
            msg.setType("push");
            msg.setMoveType1("inner");
            msg.setMoveType2("pageDetail");
            msg.setMoveTarget(user.getPage());
            msg.setPushCase(Const.USER_PUSH_PAGE);
            msg.setSubject(page.getUser().getDisplayName() + " 님이 PR#을 개설하였습니다.");
            msg.setContents(msg.getSubject());
            msg.setAppType(Const.APP_TYPE_USER);
            queueSvc.insertMsgBox(StoreUtil.getCommonAdmin(), msg, user, Const.APP_TYPE_USER);
        }
    }

    //점주용 앱에서 맞는지 검사. 5회 이상이면 본인 인증 후
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer checkAuthCode(User user, Page page) throws ResultCodeException {
        Page saved = sqlSession.selectOne("Page.getPage", page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

        int count = 0;
        if ((saved.getAuthCode() == null && page.getAuthCode() == null) || page.getAuthCode().equals(saved.getAuthCode())) {
            //잘못 입력된 기록 삭제
            saved.setIncorrectAuthCodeCount(count);
            sqlSession.update("Page.updateIncorrectAuthCodeCount", saved);
        } else {
            count = saved.getIncorrectAuthCodeCount() + 1;
            saved.setIncorrectAuthCodeCount(count);
            throw new NotMatchedValueException("errorCount", count);
        }
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer checkAuthCodeForUser(User user, Page page) throws ResultCodeException {
        Page saved = sqlSession.selectOne("Page.getPage", page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

        int count = 0;
        Date prev24Hour = DateUtil.getDateAdd(DateUtil.getCurrentDate(), DateUtil.SECOND, -(60 * 60 * 24));
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", page);
        Date lastDate = sqlSession.selectOne("Page.getLastIncorrectAuthCodeDate", map); //MGK user, page);
        boolean elapse = false;
        if (lastDate != null && lastDate.getTime() < prev24Hour.getTime()) {
            sqlSession.delete("Page.deleteIncorrectAuthCodeAll", map); //MGK user, page);
            elapse = true;
        }

        if ((saved.getAuthCode() == null && page.getAuthCode() == null) || page.getAuthCode().equals(saved.getAuthCode())) {
            if (elapse == false)
                sqlSession.delete("Page.deleteIncorrectAuthCodeAll", map); //MGK user, page);
        } else {
            if (elapse == false)
                count = sqlSession.selectOne("Page.getIncorrectAuthCodeCountInHour", map); //MGK user, page);
            else
                count = 1;

            if (count < 5) {
                sqlSession.insert("Page.insertIncorrectAuthCode", map); //MGK user, page);
                count++;
            }
            throw new NotMatchedValueException("errorCount", count);
        }
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateAuthCode(Page page) throws ResultCodeException {
        if (page.getNo() == null)
            throw new NotFoundTargetException("page", "not found");
        int effected = sqlSession.update("Page.updateAuthCode", page);

        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer checkAndUpdateAuthCode(User user, Page page, String newAuthCode) throws ResultCodeException {
        if (Const.E_SUCCESS.equals(checkAuthCode(user, page))) {
            page.setAuthCode(newAuthCode);
            return updateAuthCode(page);
        }
        return Const.E_UNKNOWN;
    }

    public int getKeywordCount(User user, SearchOpt opt) {
        Page saved = sqlSession.selectOne("Page.getPageByUser", user);
        ParamMap map = new ParamMap();
        map.put("page", saved);
        map.put("opt", opt);
        return sqlSession.selectOne("Page.getKeywordCount", map); //MGK saved, opt);
    }

    public List<PageKeyword> getKeywordList(User user, SearchOpt opt) {
        Page saved = sqlSession.selectOne("Page.getPageByUser", user);
        ParamMap map = new ParamMap();
        map.put("page", saved);
        map.put("opt", opt);
        return sqlSession.selectList("Page.getKeywordList", map); //MGK saved, opt);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer clickKeyword(Page page, String keyword) {
        ParamMap map = new ParamMap();
        map.put("page", page);
        map.put("keyword", keyword);
        int effected = sqlSession.update("Page.increaseVisitCountByKeyword", map); //MGK page, keyword);
        if (effected > 0)
            return Const.E_SUCCESS;
        return Const.E_UNKNOWN;
    }

    public List<String> getPageHashtagList(SearchOpt opt) {
        List<String> arr = sqlSession.selectList("Page.getPageHashtagList", opt);

        return arr;
    }

    public Integer updateGoodsNoti(Page page) throws ResultCodeException {

        int effected = sqlSession.update("Page.updateGoodsNoti", page);

        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public PageGoodsInfo addPageGoodsNoticeInfo(PageGoodsInfo pageGoodsInfo) throws ResultCodeException{
        try {

            String dateStr = AppUtil.localDatetimeNowString() ;

            pageGoodsInfo.setSeqNo(null);
            pageGoodsInfo.setRegDatetime(dateStr);
            pageGoodsInfoRepository.saveAndFlush(pageGoodsInfo) ;

            Page page = new Page();
            page.setNo(pageGoodsInfo.getPageSeqNo());
            page.setGoodsNotiType("select");
            page.setGoodsNotification("");
            updateGoodsNoti(page);

            return pageGoodsInfo;
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new InvalidPageGoodsInfoException("[POST]" + this.getClass().getCanonicalName(), e);
        }
    }

    public PageGoodsInfo updatePageGoodsNoticeInfo(PageGoodsInfo pageGoodsInfo) throws ResultCodeException{
        try {

            String dateStr = AppUtil.localDatetimeNowString() ;

            pageGoodsInfo.setRegDatetime(dateStr);
            pageGoodsInfoRepository.saveAndFlush(pageGoodsInfo) ;

            Page page = new Page();
            page.setNo(pageGoodsInfo.getPageSeqNo());
            page.setGoodsNotiType("select");
            page.setGoodsNotification("");
            updateGoodsNoti(page);

            return pageGoodsInfo;
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new InvalidPageGoodsInfoException("[PUT]" + this.getClass().getCanonicalName(), e);
        }
    }

}
