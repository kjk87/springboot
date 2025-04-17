package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxDelivery;
import kr.co.pplus.store.api.jpa.model.ReapPayBillkey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface ReapPayBillkeyRepository extends JpaRepository<ReapPayBillkey, Long> {

    int countByMemberSeqNo(Long memberSeqNo);

    List<ReapPayBillkey> findAllByMemberSeqNoOrderByRepresentDescSeqNoDesc(Long memberSeqNo);

    ReapPayBillkey findFirstByMemberSeqNoOrderBySeqNoDesc(Long memberSeqNo);

    @Modifying
    @Query(value="update reappay_billkey set represent = false where seq_no != :seqNo", nativeQuery = true)
    void updateNotRepresent(Long seqNo) ;

    ReapPayBillkey findBySeqNo(Long seqNo);

    void deleteBySeqNo(Long seqNo);

}