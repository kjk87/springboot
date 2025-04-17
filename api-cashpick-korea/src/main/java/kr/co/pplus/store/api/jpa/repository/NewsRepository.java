package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface NewsRepository extends JpaRepository<News, Long> {

    Integer countByPageSeqNoAndDeleted(Long pageSeqNo, Boolean deleted);

    Page<News> findAllByPageSeqNoAndDeletedOrderBySeqNoDesc(Long pageSeqNo, Boolean deleted, Pageable pageable);

    News findBySeqNo(Long seqNo);

    @Query(value = " select n.* "
            + " from news n inner join page pg  on pg.seq_no = n.page_seq_no and pg.status = 'normal' "
            + "              inner join plus ps on ps.page_seq_no = pg.seq_no and ps.block = 'N' and ps.member_seq_no = :memberSeqNo "
            + " where deleted = false "
            ,
            countQuery = "select count(1) "
                    + " from news n inner join page pg  on pg.seq_no = n.page_seq_no and pg.status = 'normal' "
                    + "              inner join plus ps on ps.page_seq_no = pg.seq_no and ps.block = 'N' and ps.member_seq_no = :memberSeqNo "
                    + " where deleted = false "
            , nativeQuery = true)
    Page<News> findPlusAllByWith(@Param("memberSeqNo") Long memberSeqNo, Pageable pageable);


    Page<News> findAllByPageSeqNoAndDeleted(Long pageSeqNo, Boolean deleted, Pageable pageable);
}