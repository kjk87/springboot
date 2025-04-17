package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BetaPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BetaPostRepository extends JpaRepository<BetaPost, Long> {

    BetaPost findBySeqNo(Long seqNo);

    Page<BetaPost> findAll(Pageable pageable);

    @Query(value = "select * from beta_post \n"
            + " where beta_code = :betaCode and \n "
            + " active = true order by random() limit 1", nativeQuery = true)
    BetaPost findByRandom(@Param("betaCode") String betaCode);

    @Query(value = "select * from beta_post \n"
            + " active = true order by random() limit 1", nativeQuery = true)
    BetaPost findByRandom();
}