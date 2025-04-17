package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.TodayPick;
import kr.co.pplus.store.api.jpa.model.TodayPickJoin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface TodayPickJoinRepository extends JpaRepository<TodayPickJoin, Long> {

    TodayPickJoin findFirstByMemberSeqNoAndTodayPickSeqNo(Long memberSeqNo, Long todayPickSeqNo);

    Page<TodayPickJoin> findAllByTodayPickSeqNoAndStatus(Long todayPickSeqNo, String status, Pageable pageable);
}
