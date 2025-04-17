package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    @Query(value="select * from product_like pl "
            +" where member_seq_no = :memberSeqNo "
            +" and (select count(1) from product p where p.seq_no = pl.product_seq_no and p.sales_type = '3') > 0"
            +" and product_price_seq_no is not null",
            countQuery = "select count(1) from product_like pl " +
                    "             where member_seq_no = :memberSeqNo " +
                    "             and (select count(1) from product p where p.seq_no = pl.product_seq_no and p.sales_type = '3') > 0"
                    +"            and product_price_seq_no is not null",
            nativeQuery = true)
    Page<ProductLike> findAllByMemberSeqNoShipping(Long memberSeqNo, Pageable pageable);

}