package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventWin;
import kr.co.pplus.store.api.jpa.model.EventWinWithJoin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface EventWinWithJoinRepository extends JpaRepository<EventWinWithJoin, Long> {

    Page<EventWinWithJoin> findAllByEventSeqNo(Long eventSeqNo, Pageable pageable);

}