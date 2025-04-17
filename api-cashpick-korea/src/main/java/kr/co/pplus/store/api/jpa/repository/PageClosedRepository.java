package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageClosed;
import kr.co.pplus.store.api.jpa.model.PageOpentime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageClosedRepository extends JpaRepository<PageClosed, Long> {

    PageClosed findBySeqNo(Long seqNo) ;
    PageClosed findByPageSeqNoAndEveryWeekAndWeekDay(long pageSeqNo, Integer everyWeek, String weekDay) ;
    void deleteAllByPageSeqNo(Long pageSeqNo) ;
    List<PageClosed> findAllByPageSeqNo(Long pageSeqNo) ;
}
