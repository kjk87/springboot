package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsPriceRepository extends JpaRepository<GoodsPrice, Long>{

	@Query(value="select *, ( select avg(eval) from goods_review where goods_price_seq_no = gp.seq_no ) as avg_eval from goods_price gp inner join goods g on gp.goods_seq_no = g.seq_no "
			+ " where 1=1 "
			+ " and gp.is_luckyball = true "
			+ " and gp.is_wholesale = false "
			+ " and g.blind != true"
			+ " and g.sales_types like '%3%'"
			+ " and ( g.status = 1 ) "
			+ " and ( gp.status = 1 ) "
			+ " and ( isnull(:first) = 1 or g.first = :first ) "
			+ " and ( isnull(:second) = 1 or g.second = :second ) "
			+ " and ( isnull(:third) = 1 or g.third = :third ) ",
			countQuery = "select count(1) from goods_price gp  inner join goods g on gp.goods_seq_no = g.seq_no "
					+ " where 1=1 "
					+ " and gp.is_luckyball = true "
					+ " and gp.is_wholesale = false "
					+ " and g.blind != true"
					+ " and g.sales_types like '%3%'"
					+ " and ( g.status = 1 ) "
					+ " and ( gp.status = 1 ) "
					+ " and ( isnull(:first) = 1 or g.first = :first ) "
					+ " and ( isnull(:second) = 1 or g.second = :second ) "
					+ " and ( isnull(:third) = 1 or g.third = :third ) ", nativeQuery = true)
	Page<GoodsPrice> findAllShipTypeWithIsLuckyball(@Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from goods_review where goods_price_seq_no = gp.seq_no ) as avg_eval from goods_price gp where gp.seq_no = :seqNo and gp.status <> -999", nativeQuery = true)
	GoodsPrice findBySeqNo(Long seqNo);

	@Query(value="select *, ( select avg(eval) from goods_review where goods_price_seq_no = gp.seq_no ) as avg_eval from goods_price gp inner join goods g on gp.goods_seq_no = g.seq_no "
			+ " where 1=1 "
			+ " and gp.page_seq_no <> g.page_seq_no "
			+ " and gp.is_wholesale = false "
			+ " and g.sales_types like '%3%'"
			+ " and ( g.status = 1 or g.status = 0 ) "
			+ " and ( gp.status <> -999 ) "
			+ " and ( isnull(:first) = 1 or g.first = :first ) "
			+ " and ( isnull(:second) = 1 or g.second = :second ) "
			+ " and ( isnull(:third) = 1 or g.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or gp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from goods_price gp  inner join goods g on gp.goods_seq_no = g.seq_no "
					+ " where 1=1 "
					+ " and gp.page_seq_no <> g.page_seq_no "
					+ " and gp.is_wholesale = false "
					+ " and g.sales_types like '%3%'"
					+ " and ( g.status = 1 or g.status = 0 ) "
					+ " and ( gp.status <> -999 ) "
					+ " and ( isnull(:first) = 1 or g.first = :first ) "
					+ " and ( isnull(:second) = 1 or g.second = :second ) "
					+ " and ( isnull(:third) = 1 or g.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or gp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<GoodsPrice> findAllShipTypeByPageSeqNo(@Param("pageSeqNo") Long pageSeqNo, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from goods_review where goods_price_seq_no = gp.seq_no ) as avg_eval from goods_price gp inner join goods g on gp.goods_seq_no = g.seq_no "
			+ " where 1=1 "
			+ " and gp.is_wholesale = true "
			+ " and g.sales_types like '%3%'"
			+ " and ( gp.status <> -999 ) "
			+ " and ( isnull(:first) = 1 or g.first = :first ) "
			+ " and ( isnull(:second) = 1 or g.second = :second ) "
			+ " and ( isnull(:third) = 1 or g.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or gp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from goods_price gp  inner join goods g on gp.goods_seq_no = g.seq_no "
					+ " where 1=1 "
					+ " and gp.is_wholesale = true "
					+ " and g.sales_types like '%3%'"
					+ " and ( gp.status <> -999 ) "
					+ " and ( isnull(:first) = 1 or g.first = :first ) "
					+ " and ( isnull(:second) = 1 or g.second = :second ) "
					+ " and ( isnull(:third) = 1 or g.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or gp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<GoodsPrice> findAllShipTypeByPageSeqNoIsWholesale(@Param("pageSeqNo") Long pageSeqNo, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from goods_review where goods_price_seq_no = gp.seq_no ) as avg_eval from goods_price gp inner join goods g on gp.goods_seq_no = g.seq_no "
			+ " where 1=1 "
			+ " and gp.page_seq_no <> g.page_seq_no "
			+ " and gp.is_wholesale = false "
			+ " and g.sales_types like '%3%'"
			+ " and g.blind != true"
			+ " and ( g.status = 1 ) "
			+ " and ( gp.status = 1 ) "
			+ " and ( isnull(:first) = 1 or g.first = :first ) "
			+ " and ( isnull(:second) = 1 or g.second = :second ) "
			+ " and ( isnull(:third) = 1 or g.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or gp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from goods_price gp  inner join goods g on gp.goods_seq_no = g.seq_no "
					+ " where 1=1 "
					+ " and gp.page_seq_no <> g.page_seq_no "
					+ " and gp.is_wholesale = false "
					+ " and g.sales_types like '%3%'"
					+ " and g.blind != true"
					+ " and ( g.status = 1 ) "
					+ " and ( gp.status = 1 ) "
					+ " and ( isnull(:first) = 1 or g.first = :first ) "
					+ " and ( isnull(:second) = 1 or g.second = :second ) "
					+ " and ( isnull(:third) = 1 or g.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or gp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<GoodsPrice> findAllShipTypeByPageSeqNoOnlyNormal(@Param("pageSeqNo") Long pageSeqNo, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from goods_review where goods_price_seq_no = gp.seq_no ) as avg_eval from goods_price gp inner join goods g on gp.goods_seq_no = g.seq_no "
			+ " where 1=1 "
			+ " and gp.is_wholesale = true "
			+ " and g.blind != true"
			+ " and g.sales_types like '%3%'"
			+ " and ( g.status = 1 ) "
			+ " and ( gp.status = 1 ) "
			+ " and ( isnull(:first) = 1 or g.first = :first ) "
			+ " and ( isnull(:second) = 1 or g.second = :second ) "
			+ " and ( isnull(:third) = 1 or g.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or gp.page_seq_no != :pageSeqNo ) ",
			countQuery = "select count(1) from goods_price gp  inner join goods g on gp.goods_seq_no = g.seq_no "
					+ " where 1=1 "
					+ " and gp.is_wholesale = true "
					+ " and g.blind != true"
					+ " and g.sales_types like '%3%'"
					+ " and ( g.status = 1 ) "
					+ " and ( gp.status = 1 ) "
					+ " and ( isnull(:first) = 1 or g.first = :first ) "
					+ " and ( isnull(:second) = 1 or g.second = :second ) "
					+ " and ( isnull(:third) = 1 or g.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or gp.page_seq_no != :pageSeqNo ) ", nativeQuery = true)
	Page<GoodsPrice> findAllShipTypeIsWholesale(@Param("pageSeqNo") Long pageSeqNo, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from goods_review where goods_price_seq_no = gp.seq_no ) as avg_eval from goods_price gp inner join goods g on gp.goods_seq_no = g.seq_no "
			+ " where 1=1 "
			+ " and gp.is_wholesale = true "
			+ " and g.blind != true"
			+ " and g.sales_types like '%3%'"
			+ " and ( g.status = 1 ) "
			+ " and ( gp.status = 1 ) "
			+ " and ( isnull(:first) = 1 or g.first = :first ) "
			+ " and ( isnull(:second) = 1 or g.second = :second ) "
			+ " and ( isnull(:third) = 1 or g.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or gp.page_seq_no != :pageSeqNo ) "
			+ " order by g.sold_count desc",
			countQuery = "select count(1) from goods_price gp  inner join goods g on gp.goods_seq_no = g.seq_no "
					+ " where 1=1 "
					+ " and gp.is_wholesale = true "
					+ " and g.blind != true"
					+ " and g.sales_types like '%3%'"
					+ " and ( g.status = 1 ) "
					+ " and ( gp.status = 1 ) "
					+ " and ( isnull(:first) = 1 or g.first = :first ) "
					+ " and ( isnull(:second) = 1 or g.second = :second ) "
					+ " and ( isnull(:third) = 1 or g.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or gp.page_seq_no != :pageSeqNo ) ", nativeQuery = true)
	Page<GoodsPrice> findAllShipTypeIsWholesaleOrderByPopular(@Param("pageSeqNo") Long pageSeqNo, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from goods_review where goods_price_seq_no = gp.seq_no ) as avg_eval from goods_price gp where gp.goods_seq_no = :goodsSeqNo and gp.page_seq_no = :pageSeqNo and gp.status <> -999 order by seq_no desc limit 1", nativeQuery = true)
	GoodsPrice findByGoodsSeqNoAndPageSeqNo(Long goodsSeqNo, Long pageSeqNo);

}
