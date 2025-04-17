package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductPrice;
import kr.co.pplus.store.api.jpa.model.ProductPriceWithDistance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductPriceWithDistanceRepository extends JpaRepository<ProductPriceWithDistance, Long>, JpaSpecificationExecutor<ProductPriceWithDistance>{

	static final String HAVERSINE_PART = "(6371 * acos(cos(radians(:latitude)) * cos(radians(pg.latitude)) * cos(radians(pg.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(pg.latitude))))";

	@Query(value="select pp2.*, ( select avg(eval) from product_review where product_price_seq_no = pp2.seq_no ) as avg_eval "
			+ ", " + HAVERSINE_PART + " as distance "
			+ ", ((select count(1) from product_like where product_price_seq_no = pp2.seq_no and member_seq_no = :memberSeqNo) > 0) as is_like "
			+ " from page pg "
			+ " inner join product_price pp2 on pp2.page_seq_no = pg.seq_no and pp2.seq_no = (select pp1.seq_no from product_price pp1"
			+ " inner join product p on p.seq_no = pp1.product_seq_no"
			+ " where 1=1 "
			+ " and pp1.discount_ratio >= 30 "
			+ " and pp1.market_type IN (2, 3)"
			+ " and p.blind != true"
			+ " and p.sales_type = 6"
			+ " and p.status = 1 "
			+ " and pp1.status = 1 "
			+ " and ( isnull(:first) = 1 or p.first = :first ) "
			+ " and ( isnull(:second) = 1 or p.second = :second ) "
			+ " and ( isnull(:third) = 1 or p.third = :third ) "
			+ " order by pp1.discount_ratio desc limit 1) "
			+ " order by distance asc ",
			countQuery = "select count(1) "
					+ " from page pg "
					+ " inner join product_price pp2 on pp2.page_seq_no = pg.seq_no and pp2.seq_no = (select pp1.seq_no from product_price pp1"
					+ " inner join product p on p.seq_no = pp1.product_seq_no"
					+ " where 1=1 "
					+ " and pp1.discount_ratio >= 30 "
					+ " and pp1.market_type IN (2, 3)"
					+ " and p.blind != true"
					+ " and p.sales_type = 6"
					+ " and p.status = 1 "
					+ " and pp1.status = 1 "
					+ " and ( isnull(:first) = 1 or p.first = :first ) "
					+ " and ( isnull(:second) = 1 or p.second = :second ) "
					+ " and ( isnull(:third) = 1 or p.third = :third ) "
					+ " order by pp1.discount_ratio desc limit 1) ", nativeQuery = true)
	Page<ProductPriceWithDistance> findAllStoreTypeByPageAndDiscountDistanceDesc(@Param("latitude") final Double latitude,
                                                                     @Param("longitude") final Double longitude,
                                                                     @Param("memberSeqNo") Long memberSeqNo,
                                                                     @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select pp.*, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval "
			+ ", " + HAVERSINE_PART + " as distance "
			+ ", ((select count(1) from product_like where product_price_seq_no = pp.seq_no and member_seq_no = :memberSeqNo) > 0) as is_like "
			+ " from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " inner join virtual_number_group_item vngi on vngi.product_price_seq_no = pp.seq_no"
			+ " inner join page pg on pg.seq_no = pp.page_seq_no"
			+ " where 1=1 "
			+ " and pp.market_type IN (2, 3)"
			+ " and p.blind != true"
			+ " and p.sales_type = :salesType"
			+ " and ( p.status = 1 ) "
			+ " and pp.status = 1 "
			+ " and ( isnull(:first) = 1 or p.first = :first ) "
			+ " and ( isnull(:second) = 1 or p.second = :second ) "
			+ " and ( isnull(:third) = 1 or p.third = :third ) "
			+ " and ( vngi.manage_seq_no = :manageSeqNo ) "
			+ " order by distance asc ",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " inner join virtual_number_group_item vngi on vngi.product_price_seq_no = pp.seq_no"
					+ " where 1=1 "
					+ " and pp.market_type IN (2, 3)"
					+ " and p.blind != true"
					+ " and p.sales_type = :salesType"
					+ " and ( p.status = 1 ) "
					+ " and pp.status = 1 "
					+ " and ( isnull(:first) = 1 or p.first = :first ) "
					+ " and ( isnull(:second) = 1 or p.second = :second ) "
					+ " and ( isnull(:third) = 1 or p.third = :third ) "
					+ " and ( vngi.manage_seq_no = :manageSeqNo ) ", nativeQuery = true)
	Page<ProductPriceWithDistance> findAllByManageSeqNoAndSalesTypeOnlyNormalWithDistance(@Param("latitude") final Double latitude,
																			  @Param("longitude") final Double longitude,@Param("manageSeqNo") Long manageSeqNo, @Param("memberSeqNo") Long memberSeqNo, @Param("salesType") Long salesType, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like, " + HAVERSINE_PART + " as distance "
			+ " from product_price pp"
			+ " inner join product p on pp.product_seq_no = p.seq_no "
			+ " inner join page pg on pp.page_seq_no = pg.seq_no"
			+ " inner join plus ps on ps.page_seq_no = pg.seq_no and ps.block = 'N' and ps.member_seq_no = :memberSeqNo "
			+ " where 1=1 "
			+ " and pp.market_type IN (2, 3)"
			+ " and pp.is_subscription = true"
			+ " and p.blind != true"
			+ " and p.status = 1 "
			+ " and pp.status = 1 "
			+ " order by distance asc",
			countQuery = "select count(1) "
					+ " from product_price pp"
					+ " inner join product p on pp.product_seq_no = p.seq_no "
					+ " inner join page pg on pp.page_seq_no = pg.seq_no"
					+ " inner join plus ps on ps.page_seq_no = pg.seq_no and ps.block = 'N' and ps.member_seq_no = :memberSeqNo "
					+ " where 1=1 "
					+ " and pp.market_type IN (2, 3)"
					+ " and pp.is_subscription = true"
					+ " and p.blind != true"
					+ " and p.status = 1 "
					+ " and pp.status = 1 ", nativeQuery = true)
	Page<ProductPriceWithDistance> findAllPlusSubscriptionTypeOnlyNormalOrderByDistance(@Param("memberSeqNo") Long memberSeqNo, @Param("latitude") final Double latitude, @Param("longitude") final Double longitude, Pageable pageable);
}
