package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long>, JpaSpecificationExecutor<ProductPrice>{

	static final String HAVERSINE_PART = "(6371 * acos(cos(radians(:latitude)) * cos(radians(pg.latitude)) * cos(radians(pg.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(pg.latitude))))";

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval "
			+ ", ((select count(1) from product_like where product_price_seq_no = pp.seq_no and member_seq_no = :memberSeqNo) > 0) as is_like "
			+ " from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.is_luckyball = true "
			+ " and pp.market_type IN (2, 3)"
			+ " and p.blind != true"
			+ " and p.sales_type = '3'"
			+ " and p.status = 1 "
			+ " and pp.status = 1 "
			+ " and ( isnull(:first) = 1 or p.first = :first ) "
			+ " and ( isnull(:second) = 1 or p.second = :second ) "
			+ " and ( isnull(:third) = 1 or p.third = :third ) "
			+ " and ( isnull(:pick) = 1 or pp.pick = :pick ) "
			+ " and ( isnull(:search) = 1 or replace(p.name, ' ', '') like :search) ",
			countQuery = "select count(1) "
					+ "from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and pp.is_luckyball = true "
					+ " and pp.market_type IN (2, 3)"
					+ " and p.blind != true"
					+ " and p.sales_type = '3'"
					+ " and ( p.status = 1 ) "
					+ " and pp.status = 1 "
					+ " and ( isnull(:first) = 1 or p.first = :first ) "
					+ " and ( isnull(:second) = 1 or p.second = :second ) "
					+ " and ( isnull(:third) = 1 or p.third = :third ) "
					+ " and ( isnull(:pick) = 1 or pp.pick = :pick ) "
					+ " and ( isnull(:search) = 1 or replace(p.name, ' ', '') like :search) ", nativeQuery = true)
	Page<ProductPrice> findAllShipTypeWithIsLuckyball(@Param("first") Long first, @Param("second") Long second, @Param("third") Long third, @Param("memberSeqNo") Long memberSeqNo, @Param("pick") Boolean pick, @Param("search") String search, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp where pp.seq_no = :seqNo and pp.status != -999", nativeQuery = true)
	ProductPrice findBySeqNo(Long seqNo);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp where pp.code = :code and pp.status != -999", nativeQuery = true)
	ProductPrice findByCode(String code);



	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.market_type IN (2, 3)"
			+ " and p.blind != true"
			+ " and p.sales_type = '3'"
			+ " and ( p.status = 1 or p.status = 0 ) "
			+ " and pp.status != -999 "
			+ " and ( isnull(:first) = 1 or p.first = :first ) "
			+ " and ( isnull(:second) = 1 or p.second = :second ) "
			+ " and ( isnull(:third) = 1 or p.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and pp.market_type IN (2, 3)"
					+ " and p.blind != true"
					+ " and p.sales_type = '3'"
					+ " and ( p.status = 1 or p.status = 0 ) "
					+ " and pp.status != -999 "
					+ " and ( isnull(:first) = 1 or p.first = :first ) "
					+ " and ( isnull(:second) = 1 or p.second = :second ) "
					+ " and ( isnull(:third) = 1 or p.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<ProductPrice> findAllShipTypeByPageSeqNo(@Param("pageSeqNo") Long pageSeqNo, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.market_type IN (2, 3)"
			+ " and p.blind != true"
			+ " and p.sales_type = '6'"
			+ " and p.status != -999 "
			+ " and pp.status != -999 "
			+ " and ( isnull(:first) = 1 or p.first = :first ) "
			+ " and ( isnull(:second) = 1 or p.second = :second ) "
			+ " and ( isnull(:third) = 1 or p.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and pp.market_type IN (2, 3)"
					+ " and p.blind != true"
					+ " and p.sales_type = '6'"
					+ " and p.status != -999 "
					+ " and pp.status != -999 "
					+ " and ( isnull(:first) = 1 or p.first = :first ) "
					+ " and ( isnull(:second) = 1 or p.second = :second ) "
					+ " and ( isnull(:third) = 1 or p.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<ProductPrice> findAllTicketTypeByPageSeqNo(@Param("pageSeqNo") Long pageSeqNo, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.market_type = 1 "
			+ " and p.sales_type = '3'"
			+ " and pp.status != -999 "
			+ " and ( isnull(:first) = 1 or p.first = :first ) "
			+ " and ( isnull(:second) = 1 or p.second = :second ) "
			+ " and ( isnull(:third) = 1 or p.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and pp.market_type = 1 "
					+ " and p.sales_type = '3'"
					+ " and pp.status != -999 "
					+ " and ( isnull(:first) = 1 or p.first = :first ) "
					+ " and ( isnull(:second) = 1 or p.second = :second ) "
					+ " and ( isnull(:third) = 1 or p.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<ProductPrice> findAllShipTypeByPageSeqNoIsWholesale(@Param("pageSeqNo") Long pageSeqNo, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval "
			+ ", ((select count(1) from product_like where product_price_seq_no = pp.seq_no and member_seq_no = :memberSeqNo) > 0) as is_like "
			+ " from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.market_type IN (2, 3)"
			+ " and p.blind != true"
			+ " and p.sales_type = :salesType"
			+ " and ( p.status = 1 ) "
			+ " and pp.status = 1 "
			+ " and ( isnull(:first) = 1 or p.first = :first ) "
			+ " and ( isnull(:second) = 1 or p.second = :second ) "
			+ " and ( isnull(:third) = 1 or p.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and pp.market_type IN (2, 3)"
					+ " and p.blind != true"
					+ " and p.sales_type = :salesType"
					+ " and ( p.status = 1 ) "
					+ " and pp.status = 1 "
					+ " and ( isnull(:first) = 1 or p.first = :first ) "
					+ " and ( isnull(:second) = 1 or p.second = :second ) "
					+ " and ( isnull(:third) = 1 or p.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<ProductPrice> findAllByPageSeqNoAndSalesTypeOnlyNormal(@Param("pageSeqNo") Long pageSeqNo, @Param("memberSeqNo") Long memberSeqNo, @Param("salesType") Long salesType, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval "
			+ ", ((select count(1) from product_like where product_price_seq_no = pp.seq_no and member_seq_no = :memberSeqNo) > 0) as is_like "
			+ " from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and (pp.is_subscription = true or pp.is_prepayment = true)"
			+ " and p.blind != true"
			+ " and p.status = 1 "
			+ " and pp.status = 1 "
			+ " and pp.page_seq_no = :pageSeqNo ",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and (pp.is_subscription = true or pp.is_prepayment = true)"
					+ " and p.blind != true"
					+ " and p.status = 1 "
					+ " and pp.status = 1 "
					+ " and pp.page_seq_no = :pageSeqNo ", nativeQuery = true)
	Page<ProductPrice> findAllByPageSeqNoAndIsSubscriptionAndIsPrepaymentOnlyNormal(@Param("pageSeqNo") Long pageSeqNo, @Param("memberSeqNo") Long memberSeqNo, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval "
			+ ", ((select count(1) from product_like where product_price_seq_no = pp.seq_no and member_seq_no = :memberSeqNo) > 0) as is_like "
			+ " from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and p.blind != true"
			+ " and p.sales_type = :salesType"
			+ " and p.status = 1  "
			+ " and pp.status = 1 "
			+ " and ( isnull(:first) = 1 or p.first = :first ) "
			+ " and ( isnull(:second) = 1 or p.second = :second ) "
			+ " and ( isnull(:third) = 1 or p.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) "
			+ " order by pp.pick desc",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and p.blind != true"
					+ " and p.sales_type = :salesType"
					+ " and p.status = 1 "
					+ " and pp.status = 1 "
					+ " and ( isnull(:first) = 1 or p.first = :first ) "
					+ " and ( isnull(:second) = 1 or p.second = :second ) "
					+ " and ( isnull(:third) = 1 or p.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<ProductPrice> findAllByPageSeqNoAndSalesTypeAndDiscountOnlyNormal(@Param("pageSeqNo") Long pageSeqNo, @Param("memberSeqNo") Long memberSeqNo, @Param("salesType") Long salesType, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.market_type = 1 "
			+ " and p.blind != true"
			+ " and p.sales_type = '3'"
			+ " and ( p.status = 1 ) "
			+ " and pp.status = 1 "
			+ " and ( isnull(:first) = 1 or p.first = :first ) "
			+ " and ( isnull(:second) = 1 or p.second = :second ) "
			+ " and ( isnull(:third) = 1 or p.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and pp.market_type = 1 "
					+ " and p.blind != true"
					+ " and p.sales_type = '3'"
					+ " and ( p.status = 1 ) "
					+ " and pp.status = 1 "
					+ " and ( isnull(:first) = 1 or p.first = :first ) "
					+ " and ( isnull(:second) = 1 or p.second = :second ) "
					+ " and ( isnull(:third) = 1 or p.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<ProductPrice> findAllShipTypeIsWholesale(@Param("pageSeqNo") Long pageSeqNo, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.market_type = 1 "
			+ " and p.blind != true"
			+ " and p.sales_type = '3'"
			+ " and ( p.status = 1 ) "
			+ " and pp.status = 1 "
			+ " and ( isnull(:first) = 1 or p.first = :first ) "
			+ " and ( isnull(:second) = 1 or p.second = :second ) "
			+ " and ( isnull(:third) = 1 or p.third = :third ) "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) "
			+ " order by p.sold_count desc",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and pp.market_type = 1 "
					+ " and p.blind != true"
					+ " and p.sales_type = '3'"
					+ " and ( p.status = 1 ) "
					+ " and pp.status = 1 "
					+ " and ( isnull(:first) = 1 or p.first = :first ) "
					+ " and ( isnull(:second) = 1 or p.second = :second ) "
					+ " and ( isnull(:third) = 1 or p.third = :third ) "
					+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<ProductPrice> findAllShipTypeIsWholesaleOrderByPopular(@Param("pageSeqNo") Long pageSeqNo, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp where pp.product_seq_no = :productSeqNo and pp.page_seq_no = :pageSeqNo and pp.status != -999 order by seq_no desc limit 1", nativeQuery = true)
	ProductPrice findByProductSeqNoAndPageSeqNo(Long productSeqNo, Long pageSeqNo);

	@Query(value="select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.market_type IN (2, 3)"
			+ " and p.blind != true"
			+ " and ( p.status = 1 ) "
			+ " and pp.status = 1 "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Integer countByPageSeqNoOnlyNormal(@Param("pageSeqNo") Long pageSeqNo);

	@Query(value="select pp2.*, ( select avg(eval) from product_review where product_price_seq_no = pp2.seq_no ) as avg_eval "
			+ ", ((select count(1) from product_like where product_price_seq_no = pp2.seq_no and member_seq_no = :memberSeqNo) > 0) as is_like "
			+ " from page pg "
			+ " inner join product_price pp2 on pp2.page_seq_no = pg.seq_no and pp2.seq_no = (select pp1.seq_no from product_price pp1"
							+ " inner join product p on p.seq_no = pp1.product_seq_no"
							+ " where 1=1 "
							+ " and pp1.discount_ratio >= 30 "
							+ " and pp1.market_type IN (2, 3)"
							+ " and p.blind != true"
							+ " and p.sales_type = 3"
							+ " and p.status = 1 "
							+ " and pp1.status = 1 "
							+ " and ( isnull(:first) = 1 or p.first = :first ) "
							+ " and ( isnull(:second) = 1 or p.second = :second ) "
							+ " and ( isnull(:third) = 1 or p.third = :third ) "
							+ " order by pp1.discount_ratio desc limit 1) "
			+ " order by pp2.discount_ratio desc ",
			countQuery = "select count(1) "
					+ " from page pg "
					+ " inner join product_price pp2 on pp2.page_seq_no = pg.seq_no and pp2.seq_no = (select pp1.seq_no from product_price pp1"
									+ " inner join product p on p.seq_no = pp1.product_seq_no"
									+ " where 1=1 "
									+ " and pp1.discount_ratio >= 30 "
									+ " and pp1.market_type IN (2, 3)"
									+ " and p.blind != true"
									+ " and p.sales_type = 3"
									+ " and p.status = 1 "
									+ " and pp1.status = 1 "
									+ " and ( isnull(:first) = 1 or p.first = :first ) "
									+ " and ( isnull(:second) = 1 or p.second = :second ) "
									+ " and ( isnull(:third) = 1 or p.third = :third ) "
									+ " order by pp1.discount_ratio desc limit 1) ", nativeQuery = true)
	Page<ProductPrice> findAllShipTypeByPageAndDiscount(@Param("memberSeqNo") Long memberSeqNo, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	ProductPrice findFirstByPageSeqNoAndStatusAndIsTicketOrderBySeqNoDesc(Long pageSeqNo, Integer status, Boolean isTicket);


	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval "
			+ ", ((select count(1) from product_like where product_price_seq_no = pp.seq_no and member_seq_no = :memberSeqNo) > 0) as is_like "
			+ " from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
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
			+ " and ( vngi.manage_seq_no = :manageSeqNo ) ",
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
	Page<ProductPrice> findAllByManageSeqNoAndSalesTypeOnlyNormal(@Param("manageSeqNo") Long manageSeqNo, @Param("memberSeqNo") Long memberSeqNo, @Param("salesType") Long salesType, @Param("first") Long first, @Param("second") Long second, @Param("third") Long third, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.market_type IN (2, 3)"
			+ " and pp.is_subscription = true"
			+ " and p.blind != true"
			+ " and p.status != -999 "
			+ " and pp.status != -999 "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and pp.market_type IN (2, 3)"
					+ " and pp.is_subscription = true"
					+ " and p.blind != true"
					+ " and p.status != -999 "
					+ " and pp.status != -999 "
					+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<ProductPrice> findAllSubscriptionTypeByPageSeqNo(@Param("pageSeqNo") Long pageSeqNo, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.is_subscription = true"
			+ " and p.blind != true"
			+ " and p.status = 1 "
			+ " and pp.status = 1 "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and pp.is_subscription = true"
					+ " and p.blind != true"
					+ " and p.status = 1 "
					+ " and pp.status = 1 "
					+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<ProductPrice> findAllSubscriptionTypeByPageSeqNoOnlyNormal(@Param("pageSeqNo") Long pageSeqNo, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.is_prepayment = true"
			+ " and p.blind != true"
			+ " and p.status = 1 "
			+ " and pp.status = 1 "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and pp.is_prepayment = true"
					+ " and p.blind != true"
					+ " and p.status = 1 "
					+ " and pp.status = 1 "
					+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<ProductPrice> findAllMoneyTypeByPageSeqNoOnlyNormal(@Param("pageSeqNo") Long pageSeqNo, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like "
			+ " from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.is_subscription = true"
			+ " and p.blind != true"
			+ " and p.status = 1 "
			+ " and pp.status = 1 "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) "
			+ " order by pp.seq_no desc limit 1", nativeQuery = true)
	ProductPrice findLastSubscriptionTypeByPageSeqNoOnlyNormal(@Param("pageSeqNo") Long pageSeqNo);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like "
			+ " from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.is_prepayment = true"
			+ " and p.blind != true"
			+ " and p.status = 1 "
			+ " and pp.status = 1 "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) "
			+ " order by pp.seq_no desc limit 1", nativeQuery = true)
	ProductPrice findLastMoneyTypeByPageSeqNoOnlyNormal(@Param("pageSeqNo") Long pageSeqNo);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval, false as is_like from product_price pp inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.market_type IN (2, 3)"
			+ " and pp.is_prepayment = true"
			+ " and p.blind != true"
			+ " and p.status != -999 "
			+ " and pp.status != -999 "
			+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ",
			countQuery = "select count(1) from product_price pp  inner join product p on pp.product_seq_no = p.seq_no "
					+ " where 1=1 "
					+ " and pp.market_type IN (2, 3)"
					+ " and pp.is_prepayment = true"
					+ " and p.blind != true"
					+ " and p.status != -999 "
					+ " and pp.status != -999 "
					+ " and ( isnull(:pageSeqNo) = 1 or pp.page_seq_no = :pageSeqNo ) ", nativeQuery = true)
	Page<ProductPrice> findAllMoneyTypeByPageSeqNo(@Param("pageSeqNo") Long pageSeqNo, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval "
			+ ", ((select count(1) from product_like where product_price_seq_no = pp.seq_no and member_seq_no = :memberSeqNo) > 0) as is_like "
			+ " from product_price pp "
			+ " inner join product p on pp.product_seq_no = p.seq_no "
			+ " inner join shopping_group_item sgi on sgi.product_price_seq_no = pp.seq_no "
			+ " inner join shopping_group sg on sgi.shopping_group_seq_no = sg.seq_no "
			+ " where 1=1 "
			+ " and pp.market_type IN (2, 3)"
			+ " and p.blind != true"
			+ " and p.sales_type = '3'"
			+ " and p.status = 1 "
			+ " and pp.status = 1 "
			+ " and sg.seq_no = :shoppingGroupSeqNo ",
			countQuery = "select count(1) "
					+ "from product_price pp "
					+ " inner join product p on pp.product_seq_no = p.seq_no "
					+ " inner join shopping_group_item sgi on sgi.product_price_seq_no = pp.seq_no "
					+ " inner join shopping_group sg on sgi.shopping_group_seq_no = sg.seq_no "
					+ " where 1=1 "
					+ " and pp.market_type IN (2, 3)"
					+ " and p.blind != true"
					+ " and p.sales_type = '3'"
					+ " and p.status = 1 "
					+ " and pp.status = 1 "
					+ " and sg.seq_no = :shoppingGroupSeqNo ", nativeQuery = true)
	Page<ProductPrice> findAllShipTypeByShoppingGroup(@Param("shoppingGroupSeqNo") Long shoppingGroupSeqNo, @Param("memberSeqNo") Long memberSeqNo, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval "
			+ ", ((select count(1) from product_like where product_price_seq_no = pp.seq_no and member_seq_no = :memberSeqNo) > 0) as is_like "
			+ " from product_price pp "
			+ " inner join product p on pp.product_seq_no = p.seq_no "
			+ " inner join shopping_brand_item sbi on sbi.product_price_seq_no = pp.seq_no "
			+ " inner join shopping_brand sb on sbi.shopping_brand_seq_no = sb.seq_no "
			+ " where 1=1 "
			+ " and pp.market_type IN (2, 3)"
			+ " and p.blind != true"
			+ " and p.sales_type = '3'"
			+ " and p.status = 1 "
			+ " and pp.status = 1 "
			+ " and sb.seq_no = :shoppingBrandSeqNo ",
			countQuery = "select count(1) "
					+ "from product_price pp "
					+ " inner join product p on pp.product_seq_no = p.seq_no "
					+ " inner join shopping_brand_item sbi on sbi.product_price_seq_no = pp.seq_no "
					+ " inner join shopping_brand sb on sbi.shopping_brand_seq_no = sb.seq_no "
					+ " where 1=1 "
					+ " and pp.market_type IN (2, 3)"
					+ " and p.blind != true"
					+ " and p.sales_type = '3'"
					+ " and p.status = 1 "
					+ " and pp.status = 1 "
					+ " and sb.seq_no = :shoppingBrandSeqNo ", nativeQuery = true)
	Page<ProductPrice> findAllShipTypeByShoppingBrand(@Param("shoppingBrandSeqNo") Long shoppingBrandSeqNo, @Param("memberSeqNo") Long memberSeqNo, Pageable pageable);

	@Query(value="select *, ( select avg(eval) from product_review where product_price_seq_no = pp.seq_no ) as avg_eval "
			+ ", ((select count(1) from product_like where product_price_seq_no = pp.seq_no and member_seq_no = :memberSeqNo) > 0) as is_like "
			+ " from product_price pp "
			+ " inner join product p on pp.product_seq_no = p.seq_no "
			+ " where 1=1 "
			+ " and pp.is_luckyball = true "
			+ " and pp.market_type IN (2, 3)"
			+ " and p.blind != true"
			+ " and p.sales_type = '3'"
			+ " and p.status = 1 "
			+ " and pp.status = 1 "
			+ " order by rand() "
			+ "limit 30", nativeQuery = true)
	List<ProductPrice> findAllShipTypeByRandom(@Param("memberSeqNo") Long memberSeqNo);
}
