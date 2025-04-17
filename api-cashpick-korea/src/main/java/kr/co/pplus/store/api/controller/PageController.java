package kr.co.pplus.store.api.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.jpa.model.PageImage;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.mvc.service.ArticleService;
import kr.co.pplus.store.mvc.service.PageService;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class PageController extends RootController {
    @Autowired
    PageService svc;

    @Autowired
    ArticleService articleSvc;

    @Autowired
    UserService userSvc;

    @Value("${STORE.REDIS_PREFIX}")
    String REDIS_PREFIX = "pplus-";

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/hashtag/check")
    public Map<String, Object> checkPageHashtag(@RequestParam("hashtag") String hashtag) {
        String exists = RedisUtil.getInstance().hGet(REDIS_PREFIX + "hashtag", hashtag);
        if (exists != null && exists.equals("1"))
            return result(Const.E_NOTFOUND, "row", "fail");
        else
            return result(Const.E_SUCCESS, "row", "success");
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/hashtag/search")
    public Map<String, Object> getPageHashtagList(SearchOpt opt) {
        List<String> hashtagList = RedisUtil.getInstance().hScan(REDIS_PREFIX + "hashtag", opt);
        return result(200, "rows", hashtagList);
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/hashtag/list")
    public Map<String, Object> getPageHashtagCategoryList() {
        List<Map<String, String>> hashtagCategoryList = RedisUtil.getInstance().hScanAll(REDIS_PREFIX + "hashtagCategory");
        return result(200, "rows", hashtagCategoryList);
    }


    @Deprecated
    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPostCount/**")
    public Map<String, Object> getPostCount(Session session, BulletinBoard board) {
        return result(Const.E_SUCCESS, "row", articleSvc.getBoardArticleCount(board, new SearchOpt()));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPostCountList/**")
    public Map<String, Object> getPostCountList(Session session, Page page) throws ResultCodeException {
        Page saved = svc.getPage(page);
        List<BulletinBoard> boardList = new ArrayList<BulletinBoard>();
        boardList.add(saved.getPrBoard());
        boardList.add(saved.getReviewBoard());
        return result(Const.E_SUCCESS, "rows", articleSvc.getArticleCountList(boardList));
    }

    @Deprecated
    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPostList/**")
    public Map<String, Object> getPostList(Session session, BulletinBoard board, SearchOpt opt) {
        return result(Const.E_SUCCESS, "rows", articleSvc.getBoardArticleList(board, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/insertPost/**")
    public Map<String, Object> insertPost(Session session, @RequestBody Article post) throws ResultCodeException {
        return result(articleSvc.insertArticle(session, post), "row", post);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updatePage/**")
    public Map<String, Object> updatePage(Session session, @RequestBody Page page) throws ResultCodeException {
        Page saved = svc.getPageWithUser(page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

        if (!saved.getUser().getNo().equals(session.getNo()))
            throw new NotPermissionException("user", "not author");

        page.setUser(saved.getUser());
        if (page.getIsLink() == null)
            page.setIsLink(false);
        return result(svc.update(page), "row", page);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateProperties/**")
    public Map<String, Object> updateProperties(Session session, @RequestBody Page page) throws ResultCodeException {
        return result(svc.updateProperties(session, page), "row", page);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updatePropertiesAll/**")
    public Map<String, Object> updatePropertiesAll(Session session, @RequestBody Page page) throws ResultCodeException {
        return result(svc.updatePropertiesAll(session, page), "row", page);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateProfileImage/**")
    public Map<String, Object> updateProfileImage(Session session, Page page) throws ResultCodeException {
        Page saved = svc.getPageWithUser(page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

        if (!saved.getUser().getNo().equals(session.getNo()))
            throw new NotPermissionException("user", "not author");

        page.setUser(saved.getUser());

        return result(svc.updateProfileImage(page), "row", page);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateThumbnail/**")
    public Map<String, Object> updateThumbnail(Session session, Page page) throws ResultCodeException {
        Page saved = svc.getPageWithUser(page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

        if (!saved.getUser().getNo().equals(session.getNo()))
            throw new NotPermissionException("user", "not author");

        page.setUser(saved.getUser());

        return result(svc.updateThumbnail(page));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateQrImage/**")
    public Map<String, Object> updateQrImage(Session session, Page page) throws ResultCodeException {
        Page saved = svc.getPageWithUser(page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

//        if (!saved.getUser().getNo().equals(session.getNo()))
//            throw new NotPermissionException("user", "not author");

        page.setUser(saved.getUser());


        String url = svc.updateQrImage(page);
        if(StringUtils.isNotEmpty(url)){
            return result(Const.E_SUCCESS, "row", url);
        }else{
            return result(Const.E_UNKNOWN);
        }

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateMainMovie/**")
    public Map<String, Object> updateMainMovie(Session session, Page page) throws ResultCodeException {
        Page saved = svc.getPageWithUser(page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

        if (!saved.getUser().getNo().equals(session.getNo()))
            throw new NotPermissionException("user", "not author");

        page.setUser(saved.getUser());

        return result(svc.updateMainMovie(page), "row", page);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateBackgroundImage/**")
    public Map<String, Object> updateBackgroundImage(Session session, Page page) throws ResultCodeException {
        Page saved = svc.getPageWithUser(page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

        if (!saved.getUser().getNo().equals(session.getNo()))
            throw new NotPermissionException("user", "not author");

        page.setUser(saved.getUser());

        return result(svc.updateBackgroundImage(page), "row", page);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updatePagePoint/**")
    public Map<String, Object> updatePagePoint(Session session, Page page) throws ResultCodeException {
        Page saved = svc.getPageWithUser(page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

        if (!saved.getUser().getNo().equals(session.getNo()))
            throw new NotPermissionException("user", "not author");

        page.setUser(saved.getUser());

        return result(svc.updatePagePoint(page), "row", page);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateBackgroundImage2/**")
    public Map<String, Object> updateBackgroundImage2(Session session, @RequestBody Page page) throws ResultCodeException {
        Page saved = svc.getPageWithUser(page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

        if (!saved.getUser().getNo().equals(session.getNo()))
            throw new NotPermissionException("user", "not author");

        page.setUser(saved.getUser());

        return result(svc.updateBackgroundImage(page), "row", page);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateIntroImageList/**")
    public Map<String, Object> updateIntroImageList(Session session, @RequestBody Page page) throws ResultCodeException {
        Page saved = svc.getPageWithUser(page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

        if (!saved.getUser().getNo().equals(session.getNo()))
            throw new NotPermissionException("user", "not author");

        page.setUser(saved.getUser());

        return result(svc.updateIntroImageList(page), "row", page);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updatePageImageList/**")
    public Map<String, Object> updatePageImageList(Session session, @RequestBody List<PageImage> pageImageList) throws ResultCodeException {

        return result(svc.updatePageImageList(pageImageList));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/deletePageImage/**")
    public Map<String, Object> deletePageImage(Session session, PageImage pageImage) throws ResultCodeException {

        return result(svc.deletePageImage(pageImage));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateIntroMovieList/**")
    public Map<String, Object> updateIntroMovieList(Session session, @RequestBody Page page) throws ResultCodeException {
        Page saved = svc.getPageWithUser(page);
        if (saved == null)
            throw new NotFoundTargetException("page", "not found");

        if (!saved.getUser().getNo().equals(session.getNo()))
            throw new NotPermissionException("user", "not author");

        page.setUser(saved.getUser());

        return result(svc.updateIntroMovieList(page), "row", page);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/checkAuthCode/**")
    public Map<String, Object> checkAuthCode(Session session, Page page) throws ResultCodeException {
        int errCnt = 0;
        try {
            svc.checkAuthCode(session, page);
            return result(Const.E_SUCCESS);
        } catch (NotMatchedValueException ex) {
            if (ex.getExtra() != null && ex.getExtra().containsKey("errorCount"))
                errCnt = (Integer) ex.getExtra().get("errorCount");
            return result(ex.getResultCode(), "row", errCnt);
        }
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/checkAuthCodeForUser/**")
    public Map<String, Object> checkAuthCodeForUser(Session session, Page page) throws ResultCodeException {
        int errCnt = 0;
        try {
            svc.checkAuthCodeForUser(session, page);
            return result(Const.E_SUCCESS);
        } catch (NotMatchedValueException ex) {
            if (ex.getExtra() != null && ex.getExtra().containsKey("errorCount"))
                errCnt = (Integer) ex.getExtra().get("errorCount");
            return result(ex.getResultCode(), "row", errCnt);
        }
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/checkAndUpdateAuthCode/**")
    public Map<String, Object> checkAndUpdateAuthCode(Session session, Page page, String newAuthCode) throws ResultCodeException {
        return result(svc.checkAndUpdateAuthCode(session, page, newAuthCode));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateAuthCode/**")
    public Map<String, Object> updateAuthCode(Session session, Page page) throws ResultCodeException {
        return result(svc.updateAuthCode(page));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateGoodsNotification/**")
    public Map<String, Object> updateGoodsNotification(Session session, Page page) throws ResultCodeException {
        return result(svc.updateGoodsNoti(page));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getIntroImageAll/**")
    public Map<String, Object> getIntroImageAll(Page page) {
        return result(Const.E_SUCCESS, "rows", svc.getIntroImageAll(page));
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageImageAll/**")
    public Map<String, Object> getPageImageAll(Page page) {
        return result(Const.E_SUCCESS, "rows", svc.getPageImageAll(page));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getIntroMovieAll/**")
    public Map<String, Object> getIntroMovieAll(Page page) {
        return result(Const.E_SUCCESS, "rows", svc.getIntroMovieAll(page));
    }


    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getCategoryAll/**")
    public Map<String, Object> getCategoryAll(PageCategory category) {
        return result(Const.E_SUCCESS, "rows", svc.getCategoryAll(category));
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/saveSnsLink/**")
    public Map<String, Object> saveSnsLink(Session session, @RequestBody SnsLink snsLink) throws ResultCodeException {
        return result(svc.saveSnsLink(session, snsLink), "row", snsLink);
    }

    @Deprecated
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/updateSnsLink/**")
    public Map<String, Object> updateSnsLink(Session session, SnsLink snsLink) throws ResultCodeException {
        return result(svc.updateSnsLink(session, snsLink), "row", snsLink);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/deleteSnsLinkByType/**")
    public Map<String, Object> deleteSnsLinkByType(Session session, SnsLink snsLink) throws ResultCodeException {
        return result(svc.deleteSnsLinkByType(session, snsLink));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getSnsLinkAll/**")
    public Map<String, Object> getSnsLinkAll(Page page) {
        return result(Const.E_SUCCESS, "rows", svc.getSnsLinkAll(page));
    }

    @Deprecated
    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getSnsLink/**")
    public Map<String, Object> getSnsLink(SnsLink snsLink) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", svc.getSnsLink(snsLink));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getSnsLinkByType/**")
    public Map<String, Object> getSnsLinkByType(SnsLink snsLink) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", svc.getSnsLinkByType(snsLink));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/visitPage/**")
    public Map<String, Object> visitPage(Session session, Page page) {
        return result(Const.E_NOTIMPLEMENT);
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageAction/**")
    public Map<String, Object> getPageAction(Session session, Page page, User user) throws ResultCodeException {
        PageAction act = svc.getPageAction(user, page);
        if (act == null)
            throw new NotFoundTargetException("action", "not found");
        return result(Const.E_SUCCESS, "row", act);
    }

    @Deprecated
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/giveReviewBol/**")
    public Map<String, Object> giveReviewBol(Session session, Page page, User user) throws UnknownException {
        PageAction action = svc.giveReviewBol(page, user);
        if (action == null)
            throw new UnknownException();
        return result(Const.E_SUCCESS, "row", action);
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPage/**")
    public Map<String, Object> getPage(Session session, Page page) throws NotFoundTargetException {

        if (session == null || session.getNo() == null)
            return result(Const.E_SUCCESS, "row", svc.getPlusPage(userSvc.getGuest(), page));

        return result(Const.E_SUCCESS, "row", svc.getPlusPage(session, page));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageCountByKeyword/**")
    public Map<String, Object> getPageCountByKeyword(SearchOpt opt) {
        return result(Const.E_SUCCESS, "row", svc.getPageCountByKeyword(opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageListByKeyword/**")
    public Map<String, Object> getPageListByKeyword(Session session, SearchOpt opt) {
        if (session == null || session.getNo() == null)
            return result(Const.E_SUCCESS, "rows", svc.getPageListByKeyword(userSvc.getGuest(), opt));

        return result(Const.E_SUCCESS, "rows", svc.getPageListByKeyword(session, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageCountByArea/**")
    public Map<String, Object> getPageCountByArea(Session session, MapArea area, SearchOpt opt,
                                                  @RequestParam(value = "categoryMinorSeqNo", required = false) Long categoryMinorSeqNo,
                                                  @RequestParam(value = "categoryMajorSeqNo", required = false) Long categoryMajorSeqNo,
                                                  @RequestParam(value = "isPoint", required = false) Boolean isPoint,
                                                  @RequestParam(value = "isCoupon", required = false) Boolean isCoupon) {

        if (session == null || session.getNo() == null) {
            return result(Const.E_SUCCESS, "row", svc.getPageCountByArea(userSvc.getGuest(), area, categoryMinorSeqNo, categoryMajorSeqNo, opt, isPoint, isCoupon));
        }


        return result(Const.E_SUCCESS, "row", svc.getPageCountByArea(session, area, categoryMinorSeqNo, categoryMajorSeqNo, opt, isPoint, isCoupon));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageListByArea/**")
    public Map<String, Object> getPageListByArea(Session session, MapArea area, SearchOpt opt, GeoPosition position,
                                                 @RequestParam(value = "categoryMinorSeqNo", required = false) Long categoryMinorSeqNo,
                                                 @RequestParam(value = "categoryMajorSeqNo", required = false) Long categoryMajorSeqNo,
                                                 @RequestParam(value = "isPoint", required = false) Boolean isPoint) {

        if (session == null || session.getNo() == null) {
            return result(Const.E_SUCCESS, "rows", svc.getPageListByArea(userSvc.getGuest(), area, categoryMinorSeqNo, categoryMajorSeqNo, opt, position, isPoint));
        }

        return result(Const.E_SUCCESS, "rows", svc.getPageListByArea(session, area, categoryMinorSeqNo, categoryMajorSeqNo, opt, position, isPoint));
    }
    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageListByAreaAndManageSeqNo/**")
    public Map<String, Object> getPageListByAreaAndManageSeqNo(Session session, MapArea area, SearchOpt opt, GeoPosition position, Long manageSeqNo) {

        if (session == null || session.getNo() == null) {
            return result(Const.E_SUCCESS, "rows", svc.getPageListByAreaAndManageSeqNo(userSvc.getGuest(), area, manageSeqNo, opt, position));
        }

        return result(Const.E_SUCCESS, "rows", svc.getPageListByAreaAndManageSeqNo(session, area, manageSeqNo, opt, position));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageByNumber/**")
    public Map<String, Object> getPageByNumber(Session session, SearchOpt opt) throws ResultCodeException {
        if (session == null || session.getNo() == null)
            return result(Const.E_SUCCESS, "row", svc.getPageByNumber(userSvc.getGuest(), opt));
        return result(Const.E_SUCCESS, "row", svc.getPageByNumber(session, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageCountByNumber/**")
    public Map<String, Object> getPageCountByNumber(Session session, SearchOpt opt) {
        if (session == null || session.getNo() == null)
            return result(Const.E_SUCCESS, "row", svc.getPageCountByNumber(userSvc.getGuest(), opt));

        return result(Const.E_SUCCESS, "row", svc.getPageCountByNumber(session, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageListByNumber/**")
    public Map<String, Object> getPageListByNumber(Session session, SearchOpt opt) {
        if (session == null || session.getNo() == null)
            return result(Const.E_SUCCESS, "row", svc.getPageListByNumber(userSvc.getGuest(), opt));
        return result(Const.E_SUCCESS, "rows", svc.getPageListByNumber(session, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageCountByTheme/**")
    public Map<String, Object> getPageCountByTheme(Session session, SearchOpt opt,
                                            @RequestParam(value = "themeSeqNo") Long themeSeqNo) {

        User user = null;
        if (session == null || session.getNo() == null){
            user = userSvc.getGuest();
        }else{
            user = session;
        }

        return result(Const.E_SUCCESS, "row", svc.getPageCountByTheme(user, themeSeqNo, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageListByTheme/**")
    public Map<String, Object> getPageListByTheme(Session session, GeoPosition position, SearchOpt opt,
                                           @RequestParam(value = "themeSeqNo", required = false) Long themeSeqNo) {
        User user = null;
        if (session == null || session.getNo() == null){
            user = userSvc.getGuest();
        }else{
            user = session;
        }

        return result(Const.E_SUCCESS, "rows", svc.getPageListByTheme(user, themeSeqNo, position, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageCountByAreaByTheme/**")
    public Map<String, Object> getPageCountByAreaByTheme(Session session, MapArea area, SearchOpt opt, Long themeSeqNo) {

        User user = null;
        if (session == null || session.getNo() == null){
            user = userSvc.getGuest();
        }else{
            user = session;
        }

        return result(Const.E_SUCCESS, "row", svc.getPageCountByAreaByTheme(user, area, themeSeqNo));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageListByAreaByTheme/**")
    public Map<String, Object> getPageListByAreaByTheme(Session session, MapArea area, SearchOpt opt, GeoPosition position,
                                                 @RequestParam(value = "themeSeqNo", required = false) Long themeSeqNo) {

        User user = null;
        if (session == null || session.getNo() == null){
            user = userSvc.getGuest();
        }else{
            user = session;
        }

        return result(Const.E_SUCCESS, "rows", svc.getPageListByAreaByTheme(user, area, themeSeqNo, opt, position));
    }


    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageCount/**")
    public Map<String, Object> getPageCount(Session session, SearchOpt opt,
                                            @RequestParam(value = "categoryMinorSeqNo", required = false) Long categoryMinorSeqNo,
                                            @RequestParam(value = "categoryMajorSeqNo", required = false) Long categoryMajorSeqNo,
                                            @RequestParam(value = "type", required = false) String type,
                                            @RequestParam(value = "onlyPoint", required = false) Boolean onlyPoint,
                                            @RequestParam(value = "storeType", required = false) String storeType) {
        User user = null;
        if (session == null || session.getNo() == null){
            user = userSvc.getGuest();
        }else{
            user = session;
        }

        return result(Const.E_SUCCESS, "row", svc.getPageCount(user, categoryMinorSeqNo, categoryMajorSeqNo, opt, type, onlyPoint, storeType));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageList/**")
    public Map<String, Object> getPageList(Session session, GeoPosition position, SearchOpt opt,
                                           @RequestParam(value = "categoryMinorSeqNo", required = false) Long categoryMinorSeqNo,
                                           @RequestParam(value = "categoryMajorSeqNo", required = false) Long categoryMajorSeqNo,
                                           @RequestParam(value = "type", required = false) String type,
                                           @RequestParam(value = "onlyPoint", required = false) Boolean onlyPoint,
                                           @RequestParam(value = "storeType", required = false) String storeType
    ) {
        User user = null;
        if (session == null || session.getNo() == null){
            user = userSvc.getGuest();
        }else{
            user = session;
        }

        return result(Const.E_SUCCESS, "rows", svc.getPageList(user, categoryMinorSeqNo, categoryMajorSeqNo, position, opt, type, onlyPoint, storeType));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageCountByManageSeqNo/**")
    public Map<String, Object> getPageCountByManageSeqNo(Session session, Long manageSeqNo) {
        User user = null;
        if (session == null || session.getNo() == null){
            user = userSvc.getGuest();
        }else{
            user = session;
        }

        return result(Const.E_SUCCESS, "row", svc.getPageCountByManageSeqNo(user, manageSeqNo));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getPageListByManageSeqNo/**")
    public Map<String, Object> getPageListByManageSeqNo(Session session, GeoPosition position, SearchOpt opt, Long manageSeqNo) {

        User user = null;
        if (session == null || session.getNo() == null){
            user = userSvc.getGuest();
        }else{
            user = session;
        }

        return result(Const.E_SUCCESS, "rows", svc.getPageListByManageSeqNo(user, manageSeqNo, position, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getRandomPage/**")
    public Map<String, Object> getRandomPage(Session session) throws NotFoundTargetException {
        if (session == null || session.getNo() == null)
            return result(Const.E_SUCCESS, "row", svc.getRandomPage(userSvc.getGuest()));

        return result(Const.E_SUCCESS, "row", svc.getRandomPage(session));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getKeywordCount/**")
    public Map<String, Object> getKeywordCount(User user, SearchOpt opt) {
        return result(200, "row", svc.getKeywordCount(user, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/getKeywordList/**")
    public Map<String, Object> getKeywordList(User user, SearchOpt opt) {
        return result(200, "rows", svc.getKeywordList(user, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/page/visitByKeyword/**")
    public Map<String, Object> visitByKeyword(Page page, String keyword) {
        return result(svc.clickKeyword(page, keyword));
    }


}
