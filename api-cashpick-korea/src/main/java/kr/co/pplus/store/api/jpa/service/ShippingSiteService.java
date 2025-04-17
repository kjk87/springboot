package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.IslandsRegion;
import kr.co.pplus.store.api.jpa.model.ShippingSite;
import kr.co.pplus.store.api.jpa.repository.IslandsRegionRepository;
import kr.co.pplus.store.api.jpa.repository.ShippingSiteRepository;
import kr.co.pplus.store.exception.InvalidShippingSiteException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class ShippingSiteService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(ShippingSiteService.class);



	@Autowired
	private ShippingSiteRepository shippingSiteRepository;

	@Autowired
	private IslandsRegionRepository islandsRegionRepository;

	public List<ShippingSite> getShippingSiteByMemberSeqNo(Long memberSeqNo){
		return shippingSiteRepository.findAllByMemberSeqNoOrderByIsDefaultDesc(memberSeqNo);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ShippingSite insertSite(User user, ShippingSite shippingSite) throws ResultCodeException{

		try {
			List<ShippingSite> shippingSiteList = getShippingSiteByMemberSeqNo(user.getNo());

			shippingSite.setSeqNo(null);
			shippingSite.setMemberSeqNo(user.getNo());
			if(shippingSiteList == null || shippingSiteList.size() == 0){
				shippingSite.setIsDefault(true);
			}else{
				shippingSite.setIsDefault(false);
			}

			shippingSite = shippingSiteRepository.saveAndFlush(shippingSite);

			return shippingSite;
		}catch (Exception e){
			throw new InvalidShippingSiteException("insertSite", e);
		}


	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ShippingSite updateShippingSite(User user, ShippingSite shippingSite) throws ResultCodeException{
		try {
			shippingSite.setMemberSeqNo(user.getNo());
			shippingSite = shippingSiteRepository.saveAndFlush(shippingSite);
			return shippingSite;
		}catch (Exception e){
			throw new InvalidShippingSiteException("updateShippingSite", e);
		}

	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void deleteShippingSite(User user, Long seqNo) throws ResultCodeException{

		try {
			ShippingSite shippingSite = shippingSiteRepository.findBySeqNo(seqNo);
			boolean isDefault = shippingSite.getIsDefault();
			shippingSiteRepository.delete(shippingSite);


			if(isDefault){
				List<ShippingSite> shippingSiteList = getShippingSiteByMemberSeqNo(user.getNo());
				if(shippingSiteList != null && shippingSiteList.size() > 0){
					shippingSiteList.get(0).setIsDefault(true);
					shippingSiteRepository.save(shippingSiteList.get(0));
				}
			}
		}catch (Exception e){
			throw new InvalidShippingSiteException("deleteShippingSite", e);
		}

	}

	public boolean checkIslandsRegion(String postCode){
		return islandsRegionRepository.existsByPostcode(postCode);
	}

	public IslandsRegion getIsLandsRegion(String postCode){
		return islandsRegionRepository.findByPostcode(postCode);
	}
}
