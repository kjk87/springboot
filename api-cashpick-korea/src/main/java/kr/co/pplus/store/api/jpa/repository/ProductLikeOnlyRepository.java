package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsLike;
import kr.co.pplus.store.api.jpa.model.ProductLike;
import kr.co.pplus.store.api.jpa.model.ProductLikeOnly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface ProductLikeOnlyRepository extends JpaRepository<ProductLikeOnly, Long> {

    ProductLikeOnly findByMemberSeqNoAndProductSeqNoAndProductPriceSeqNo(Long memberSeqNo, Long productSeqNo, Long productPriceSeqNo) ;

    Integer countByMemberSeqNo(Long memberSeqNo);

    void deleteAllByProductSeqNo(Long productSeqNo);

    @Modifying
    @Query(value="delete from product_like where expire_datetime < now()", nativeQuery=true)
    void deleteExpiredProduct() ;

    @Modifying
    @Query(value="update product_like set expire_datetime = :expireDatetime where product_seq_no=:productSeqNo", nativeQuery=true)
    void updateExpiredDatetime(@Param("productSeqNo") Long productSeqNo, @Param("expireDatetime") String expireDatetime) ;

}