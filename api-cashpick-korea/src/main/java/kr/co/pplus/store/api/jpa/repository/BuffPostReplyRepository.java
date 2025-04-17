package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuffPostLike;
import kr.co.pplus.store.api.jpa.model.BuffPostReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuffPostReplyRepository extends JpaRepository<BuffPostReply, Long> {

    BuffPostReply findBySeqNo(Long seqNo);

}
