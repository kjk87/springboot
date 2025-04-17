package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageRepository extends JpaRepository<Page, Long> {
    Page findBySeqNo(Long seqNo) ;
    
    @Query(value="SELECT PG.* " +
            "          FROM page PG " +
            "          INNER JOIN page_virtual_number PVN ON PG.seq_no=PVN.page_seq_no " +
            "          INNER JOIN virtual_number VN ON PVN.virtual_number=VN.virtual_number " +
            "          WHERE " +
            "               VN.virtual_number = :prnumber " +
            "               AND PG.status='normal' ", nativeQuery=true)
    Page findByPrNumber(@Param("prnumber") String prnumber) ;

    Page findFirstByMemberSeqNo(Long memberSeqNo);
}