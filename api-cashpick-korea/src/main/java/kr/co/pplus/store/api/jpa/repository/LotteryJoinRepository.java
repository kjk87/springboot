package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LotteryJoin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LotteryJoinRepository extends JpaRepository<LotteryJoin, String> {

    int countByMemberSeqNo(Long memberSeqNo);


    Page<LotteryJoin> findAllByMemberSeqNoOrderBySeqNoDesc(Long memberSeqNo, Pageable pageable);

}