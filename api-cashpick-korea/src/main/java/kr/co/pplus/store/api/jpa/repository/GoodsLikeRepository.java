package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsLikeRepository extends JpaRepository<GoodsLike, Long> {

    GoodsLike findByMemberSeqNoAndPageSeqNoAndGoodsSeqNoAndGoodsPriceSeqNo(Long memberSeqNo, Long pageSeqNo, Long goodsSeqNo, Long goodsPriceSeqNo) ;

    GoodsLike findByMemberSeqNoAndPageSeqNoAndGoodsSeqNoAndStatus(Long memberSeqNo, Long pageSeqNo, Long goodsSeqNo, Integer status) ;

    GoodsLike findByMemberSeqNoAndPageSeqNoAndGoodsSeqNoAndGoodsPriceSeqNoAndStatus(Long memberSeqNo, Long pageSeqNo, Long goodsSeqNo, Long goodsPriceSeqNo, Integer status) ;

    Page<GoodsLike>  findAllByMemberSeqNoAndStatus(Long memberSeqNo, Integer status, Pageable pageable) ;

    Page<GoodsLike>  findAllByPageSeqNoAndStatus(Long pageSeqNo, Integer status, Pageable pageable) ;

    Page<GoodsLike>  findAllByMemberSeqNoAndPageSeqNoAndStatus(Long memberSeqNo, Long pageSeqNo, Integer status, Pageable pageable) ;

    Page<GoodsLike>  findAllByMemberSeqNoAndGoodsSeqNoAndStatus(Long memberSeqNo, Long goodsSeqNo, Integer status, Pageable pageable) ;

    Page<GoodsLike>  findAllByPageSeqNoAndGoodsSeqNoAndStatus(Long pageSeqNo, Long goodsSeqNo, Integer status, Pageable pageable) ;

    Page<GoodsLike>  findAllByGoodsSeqNoAndStatus(Long goodsSeqNo, Integer status, Pageable pageable) ;

    Page<GoodsLike>  findAllByStatus(Integer status, Pageable pageable) ;

    Integer  countAllByMemberSeqNoAndPageSeqNoAndGoodsSeqNoAndStatus(Long memberSeqNo, Long pageSeqNo, Long goodsSeqNo, Integer status) ;
    Integer  countAllByMemberSeqNoAndStatus(Long memberSeqNo, Integer status) ;
    Integer  countAllByPageSeqNoAndStatus(Long pageSeqNo, Integer status) ;
    Integer  countAllByMemberSeqNoAndPageSeqNoAndStatus(Long memberSeqNo, Long pageSeqNo, Integer status) ;
    Integer  countAllByMemberSeqNoAndGoodsSeqNoAndStatus(Long memberSeqNo, Long goodsSeqNo, Integer status) ;
    Integer  countAllByPageSeqNoAndGoodsSeqNoAndStatus(Long pageSeqNo, Long goodsSeqNo, Integer status) ;
    Integer  countAllByGoodsSeqNoAndStatus(Long goodsSeqNo, Integer status) ;
    Integer  countAllByStatus(Integer status) ;

    void deleteAllByGoodsSeqNo(Long goodsSeqNo);

    @Modifying
    @Query(value="delete from goods_like where expire_datetime < now()", nativeQuery=true)
    void deleteExpiredGoods() ;

    @Modifying
    @Query(value="update goods_like set expire_datetime = :expireDatetime where goods_seq_no=:goodsSeqNo", nativeQuery=true)
    void updateExpiredDatetime(@Param("goodsSeqNo") Long goodsSeqNo, @Param("expireDatetime") String expireDatetime) ;

}