package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.News;
import kr.co.pplus.store.api.jpa.model.NewsReviewOnly;
import kr.co.pplus.store.api.jpa.service.NewsService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidProductException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class NewsController extends RootController {

    private Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/news/getNewsCountByPageSeqNo")
    public Map<String,Object> getNewsCountByPageSeqNo(Session session, Long pageSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", newsService.getNewsCountByPageSeqNo(pageSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/news/getListByPageSeqNo")
    public Map<String,Object> getNewsListByPageSeqNo(Session session, Pageable pageable, Long pageSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", newsService.getNewsListByPageSeqNo(pageSeqNo, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/news/get")
    public Map<String,Object> getNews(Session session, Long seqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", newsService.getNews(seqNo));
    }

    @GetMapping(value = baseUri + "/news/getPlusNewsList")
    public Map<String, Object> getPlusNewsList(Session session, Pageable pageable, HttpServletRequest request) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);


            Page<News> page = newsService.getPlusNewsList(session.getNo(), pageable);


            System.out.println("/news/getPlusNewsList[GET] : " + page.toString());
            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/news/getPlusNewsList[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/news/getNewsReviewList")
    public Map<String,Object> getNewsReviewList(Session session, Pageable pageable, Long newsSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", newsService.getNewsReviewList(newsSeqNo, pageable));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/news/review/insert")
    public Map<String,Object> insertEventReply(Session session, @RequestBody NewsReviewOnly newsReviewOnly) throws ResultCodeException {
        newsReviewOnly.setMemberSeqNo(session.getNo());
        return result(newsService.insertNewsReview(newsReviewOnly));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/news/review/update")
    public Map<String,Object> updateEventReply(Session session, @RequestBody NewsReviewOnly newsReviewOnly) throws ResultCodeException {
        return result(newsService.updateNewsReview(session, newsReviewOnly));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/news/review/delete")
    public Map<String,Object> deleteEventReply(Session session, Long seqNo) throws ResultCodeException {
        return result(newsService.deleteNewsReview(session, seqNo));
    }

}
