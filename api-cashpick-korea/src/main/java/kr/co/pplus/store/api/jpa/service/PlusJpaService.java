package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.News;
import kr.co.pplus.store.api.jpa.model.Plus;
import kr.co.pplus.store.api.jpa.repository.PlusRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PlusJpaService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(PlusJpaService.class);



	@Autowired
	PlusRepository plusRepository;

	@Autowired
	NewsService newsService;


	public Page<Plus> getPlusListWithNews(User user, HttpServletRequest request, Pageable pageable) throws ResultCodeException {

        Map<String, String> sortMap = new HashMap<String, String>();
	    pageable = this.nativePageable(request, pageable, sortMap);
		Page<Plus> page = plusRepository.findAllByMemberSeqNo(user.getNo(), pageable);

		for(int i = 0; i < page.getContent().size(); i++){

			Pageable newsPageable = new Pageable() {
				@Override
				public int getPageNumber() {
					return 0;
				}

				@Override
				public int getPageSize() {
					return 5;
				}

				@Override
				public long getOffset() {
					return 0;
				}

				@Override
				public Sort getSort() {
					return Sort.by(Sort.Direction.DESC, "seqNo") ;
				}

				@Override
				public Pageable next() {
					return this;
				}

				@Override
				public Pageable previousOrFirst() {
					return this;
				}

				@Override
				public Pageable first() {
					return this;
				}

				@Override
				public boolean hasPrevious() {
					return false;
				}
			};
			Page<News> newsPage = newsService.getNewsListByPageSeqNo(page.getContent().get(i).getPageSeqNo(), newsPageable);
			page.getContent().get(i).setNewsList(newsPage.getContent());
			page.getContent().get(i).setTotalNewsElements(newsPage.getTotalElements());
		}

		return page;
	}

	public Page<Plus> getPlusListByMemberSeqNo(User user, HttpServletRequest request, Pageable pageable){

		Map<String, String> sortMap = new HashMap<String, String>();
		pageable = this.nativePageable(request, pageable, sortMap);
		Page<Plus> page = plusRepository.findAllByMemberSeqNo(user.getNo(), pageable);
		return page;
	}
	public Page<Plus> getPlusListByPageSeqNo(HttpServletRequest request, Pageable pageable, Long pageSeqNo, Boolean male, Boolean female, Boolean age10, Boolean age20
			, Boolean age30, Boolean age40, Boolean age50, Boolean age60, Integer buyCount, Integer lasBuyDay) throws ResultCodeException {
		Page<Plus> page = plusRepository.findAllByPageSeqNo(pageSeqNo, male, female, age10, age20, age30, age40, age50, age60, buyCount, lasBuyDay, pageable);
		return page;
	}

	public Integer getPlusCountByPageSeqNo(Long pageSeqNo){
		return plusRepository.countAllByPageSeqNo(pageSeqNo);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertPlus(Plus plus) throws ResultCodeException {

		Boolean exist = plusRepository.existsByMemberSeqNoAndPageSeqNo(plus.getMemberSeqNo(), plus.getPageSeqNo());

		if(!exist){
			plusRepository.save(plus);
		}

		return Const.E_SUCCESS;
	}
}
