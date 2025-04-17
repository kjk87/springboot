package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Goods;
import kr.co.pplus.store.api.jpa.model.PageOpentime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageOpentimeRepository extends JpaRepository<PageOpentime, Long> {

    PageOpentime findBySeqNo(Long seqNo) ;
    PageOpentime findByPageSeqNoAndTypeAndWeekDay(long pageSeqNo, Integer type, String weekDay) ;
    void deleteAllByPageSeqNo(Long pageSeqNo) ;
    List<PageOpentime> findAllByPageSeqNo(Long pageSeqNo) ;
}
