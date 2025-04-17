package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.ProductPrice;
import kr.co.pplus.store.api.jpa.model.ShoppingBrand;
import kr.co.pplus.store.api.jpa.model.ShoppingBrandCategory;
import kr.co.pplus.store.api.jpa.repository.ShoppingBrandCategoryRepository;
import kr.co.pplus.store.api.jpa.repository.ShoppingBrandRepository;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class ShoppingBrandService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(ShoppingBrandService.class);



	@Autowired
	ShoppingBrandCategoryRepository shoppingBrandCategoryRepository;

	@Autowired
	ShoppingBrandRepository shoppingBrandRepository;

	@Autowired
	ProductService productService;

	public ShoppingBrand getShoppingBrand(Long seqNo){
		return shoppingBrandRepository.findBySeqNo(seqNo);
	}


	public List<ShoppingBrandCategory> getShoppingBrandCategoryList(){

		return shoppingBrandCategoryRepository.findAllByStatusOrderByArrayAsc("active");
	}

	public Page<ShoppingBrand> getShoppingBrandListByCategory(Long memberSeqNo, Long shoppingBrandCategorySeqNo, Pageable pageable){
		Page<ShoppingBrand> page = shoppingBrandRepository.findAllByStatusAndShoppingBrandCategorySeqNoOrderByArrayAsc("active", shoppingBrandCategorySeqNo, pageable);

		for(int i = 0; i < page.getContent().size(); i++){

			Pageable productPageable = new Pageable() {
				@Override
				public int getPageNumber() {
					return 0;
				}

				@Override
				public int getPageSize() {
					return 5;
				}

				@Override
				public long getOffset() {
					return 0;
				}

				@Override
				public Sort getSort() {
					return Sort.by(Sort.Direction.DESC, "pick","seq_no") ;
				}

				@Override
				public Pageable next() {
					return this;
				}

				@Override
				public Pageable previousOrFirst() {
					return this;
				}

				@Override
				public Pageable first() {
					return this;
				}

				@Override
				public boolean hasPrevious() {
					return false;
				}
			};

			Page<ProductPrice> productPage = productService.getProductPriceListShipTypeByShoppingBrand(page.getContent().get(i).getSeqNo(), memberSeqNo, productPageable);
			page.getContent().get(i).setProductPriceList(productPage.getContent());
			page.getContent().get(i).setTotalProductPriceElements(productPage.getTotalElements());
		}

		return page;
	}
}
