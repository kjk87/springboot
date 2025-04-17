package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsReview ;
import kr.co.pplus.store.api.jpa.model.PageEvalResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsReviewRepository extends JpaRepository<GoodsReview, Long> {


    //JPQL


//    @Query("select new kr.co.pplus.store.api.jpa.model.PageEvalResult( sum(pe.review_count) , sum(pe.goods_count) , sum(pe.eval) ) " +
//            "from ( " +
//            "       select count(1) as reviewCount, 0 as goodsCount, AVG(gr.eval) as eval " +
//            "       from goodsReview gr inner join goods g on gr.goodsSeqNo = g.seqNo where g.pageSeqNo = :no " +
//            "       union all\n" +
//            "       select 0 as reviewCount, count(1) as goodsCount, 0.0 as eval " +
//            "       from goods g where g.page_seq_no = :no " +
//            "       ) pe")
//@Query("select new kr.co.pplus.store.api.jpa.model.PageEvalResult( COUNT(gr), AVG(gr.eval) ) "
//        + " from goodsReview gr inner join goods g on gr.goodsSeqNo = g.seqNo where g.pageSeqNo = :no")

    @Query("select new kr.co.pplus.store.api.jpa.model.PageEvalResult( COUNT(gr), AVG(gr.eval) ) "
            + " from goodsReview gr inner join goods g on gr.goodsSeqNo = g.seqNo where g.pageSeqNo = :no")
    PageEvalResult findPageEvalResult(@Param("no") Long no);


    GoodsReview findBySeqNo(Long seqNo);
    Page<GoodsReview> findAllByMemberSeqNo(Long memberSeqNo, Pageable pageable);
    Page<GoodsReview> findAllByGoodsSeqNo(Long goodsSeqNo, Pageable pageable);
    Page<GoodsReview> findAllByMemberSeqNoAndGoodsSeqNo(Long memberSeqNo, Long goodsSeqNo, Pageable pageable);
    Page<GoodsReview> findAll(Pageable pageable);
    Page<GoodsReview> findAllByPageSeqNo(Long pageSeqNo, Pageable pageable) ;

    Integer countAllByMemberSeqNo(Long memberSeqNo);
    Integer countAllByGoodsSeqNo(Long goodsSeqNo);
    Integer countAllByGoodsPriceSeqNo(Long goodsPriceSeqNo);
    Integer countAllByMemberSeqNoAndGoodsSeqNo(Long memberSeqNo, Long goodsSeqNo);
    Integer countAllBy();
    Integer countAllByPageSeqNo(Long pageSeqNo) ;

}