package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BetaPost;
import kr.co.pplus.store.api.jpa.model.Lotto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LottoRepository extends JpaRepository<Lotto, Long> {

    Lotto findBySeqNo(Long seqNo);
}