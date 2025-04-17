package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsLike;
import kr.co.pplus.store.api.jpa.model.GoodsLikeDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsLikeDetailRepository extends JpaRepository<GoodsLikeDetail, Long> {

    GoodsLikeDetail findByMemberSeqNoAndPageSeqNoAndGoodsSeqNoAndStatus(Long memberSeqNo, Long pageSeqNo, Long goodsSeqNo, Integer status) ;
    Page<GoodsLikeDetail>  findAllByMemberSeqNoAndStatus(Long memberSeqNo, Integer status, Pageable pageable) ;
    Page<GoodsLikeDetail>  findAllByPageSeqNoAndStatus(Long pageSeqNo, Integer status, Pageable pageable) ;
    Page<GoodsLikeDetail>  findAllByMemberSeqNoAndPageSeqNoAndStatus(Long memberSeqNo, Long pageSeqNo, Integer status, Pageable pageable) ;
    Page<GoodsLikeDetail>  findAllByMemberSeqNoAndGoodsSeqNoAndStatus(Long memberSeqNo, Long goodsSeqNo, Integer status, Pageable pageable) ;
    Page<GoodsLikeDetail>  findAllByPageSeqNoAndGoodsSeqNoAndStatus(Long pageSeqNo, Long goodsSeqNo, Integer status, Pageable pageable) ;
    Page<GoodsLikeDetail>  findAllByGoodsSeqNoAndStatus(Long goodsSeqNo, Integer status, Pageable pageable) ;
    Page<GoodsLikeDetail>  findAllByStatus(Integer status, Pageable pageable) ;

    @Query(value="select * from goods_like gl "
            +" where member_seq_no = :memberSeqNo "
            +" and (select count(1) from goods g where g.seq_no = gl.goods_seq_no and g.sales_types like '%3%') > 0"
            +" and goods_price_seq_no is not null",
            countQuery = "select count(1) from goods_like gl " +
                    "             where member_seq_no = :memberSeqNo " +
                    "             and (select count(1) from goods g where g.seq_no = gl.goods_seq_no and g.sales_types like '%3%') > 0"
                    +"            and goods_price_seq_no is not null",
            nativeQuery = true)
    Page<GoodsLikeDetail> findAllByMemberSeqNoShipping(Long memberSeqNo, Pageable pageable);

    @Query(value="select * from goods_like gl "
            +" where member_seq_no = :memberSeqNo "
            +" and (select count(1) from goods g where g.seq_no = gl.goods_seq_no and g.sales_types like '%1%') > 0",

            // +" ORDER BY CASE WHEN isnull(:sort)=1 THEN seq_no DESC ELSE :sort END",
            countQuery = "select count(1) from goods_like gl " +
                    "             where member_seq_no = :memberSeqNo " +
                    "             and (select count(1) from goods g where g.seq_no = gl.goods_seq_no and g.sales_types like '%1%') > 0",
            nativeQuery = true)
    Page<GoodsLikeDetail> findAllByMemberSeqNo(Long memberSeqNo, Pageable pageable);
}