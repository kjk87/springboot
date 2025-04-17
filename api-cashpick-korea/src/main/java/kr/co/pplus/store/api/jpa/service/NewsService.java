package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.News;
import kr.co.pplus.store.api.jpa.model.NewsReview;
import kr.co.pplus.store.api.jpa.model.NewsReviewOnly;
import kr.co.pplus.store.api.jpa.repository.NewsRepository;
import kr.co.pplus.store.api.jpa.repository.NewsReviewOnlyRepository;
import kr.co.pplus.store.api.jpa.repository.NewsReviewRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class NewsService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(NewsService.class);

	@Autowired
	NewsRepository newsRepository;

	@Autowired
	NewsReviewRepository newsReviewRepository;

	@Autowired
	NewsReviewOnlyRepository newsReviewOnlyRepository;

	public Integer getNewsCountByPageSeqNo(Long pageSeqNo){
		return newsRepository.countByPageSeqNoAndDeleted(pageSeqNo, false);
	}

	public Page<News> getNewsListByPageSeqNo(Long pageSeqNo, Pageable pageable) throws ResultCodeException {

		return newsRepository.findAllByPageSeqNoAndDeletedOrderBySeqNoDesc(pageSeqNo, false, pageable);
	}

	public News getNews(Long seqNo){
		return newsRepository.findBySeqNo(seqNo);
	}

	public Page<News> getPlusNewsList(Long memberSeqNo, Pageable pageable) throws ResultCodeException {

		return newsRepository.findPlusAllByWith(memberSeqNo, pageable);
	}

	public Page<News> getPlusNewsListByPageSeqNo(Long pageSeqNo, Pageable pageable) throws ResultCodeException {
		return newsRepository.findAllByPageSeqNoAndDeleted(pageSeqNo, false, pageable);
	}

	public Page<NewsReview> getNewsReviewList(Long newsSeqNo, Pageable pageable) throws ResultCodeException {

		return newsReviewRepository.findAllByNewsSeqNoAndDeleted(newsSeqNo, false, pageable);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int insertNewsReview(NewsReviewOnly newsReviewOnly){

		String dateStr = AppUtil.localDatetimeNowString();
		newsReviewOnly.setRegDatetime(dateStr);
		newsReviewOnly.setModDatetime(dateStr);
		newsReviewOnly.setDeleted(false);
		newsReviewOnlyRepository.saveAndFlush(newsReviewOnly);

		return Const.E_SUCCESS;

	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateNewsReview(User user, NewsReviewOnly newsReviewOnly){

		logger.debug("no : "+user.getNo() + " memSeqNo : " +newsReviewOnly.getMemberSeqNo());
		if(!user.getNo().equals(newsReviewOnly.getMemberSeqNo())){
			return Const.E_INVALID_OAUTH;
		}

		String dateStr = AppUtil.localDatetimeNowString();
		newsReviewOnly.setModDatetime(dateStr);
		newsReviewOnly.setDeleted(false);
		newsReviewOnlyRepository.saveAndFlush(newsReviewOnly);

		return Const.E_SUCCESS;

	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteNewsReview(User user, Long seqNo){

		NewsReviewOnly newsReviewOnly = newsReviewOnlyRepository.findBySeqNo(seqNo);

		if(!user.getNo().equals(newsReviewOnly.getMemberSeqNo())){
			return Const.E_INVALID_OAUTH;
		}

		String dateStr = AppUtil.localDatetimeNowString();
		newsReviewOnly.setModDatetime(dateStr);
		newsReviewOnly.setDeleted(true);
		newsReviewOnlyRepository.saveAndFlush(newsReviewOnly);

		return Const.E_SUCCESS;

	}
}
