package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuffPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuffPostLikeRepository extends JpaRepository<BuffPostLike, Long> {

    void deleteByMemberSeqNoAndBuffPostSeqNo(Long memberSeqNo, Long buffPostSeqNo);

    boolean existsByMemberSeqNoAndBuffPostSeqNo(Long memberSeqNo, Long buffPostSeqNo);

}
