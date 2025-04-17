package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.Cart;
import kr.co.pplus.store.api.jpa.model.CartDetail;
import kr.co.pplus.store.api.jpa.model.CartOption;
import kr.co.pplus.store.api.jpa.repository.CartDetailRepository;
import kr.co.pplus.store.api.jpa.repository.CartOptionRepository;
import kr.co.pplus.store.api.jpa.repository.CartRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
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
public class CartService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(CartService.class);

	@Autowired
	CartDetailRepository cartDetailRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	CartOptionRepository cartOptionRepository;

	public Integer getCartCount(Long memberSeqNo, Integer salesType){
		return cartDetailRepository.countByMemberSeqNoAndSalesType(memberSeqNo, salesType);
	}

	public List<CartDetail> getCartList(Long memberSeqNo, Integer salesType){
		return cartDetailRepository.findAllByMemberSeqNoAndSalesType(memberSeqNo, salesType);
	}

	public Boolean checkCartPage(Long memberSeqNo, Long pageSeqNo, Integer salesType){
		List<Cart> cartList = cartRepository.findAllByMemberSeqNoAndSalesType(memberSeqNo, salesType);
		if(cartList != null && cartList.size() > 0){
			return cartList.get(0).getPageSeqNo().equals(pageSeqNo);
		}
		return true;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveCart(Cart cart){
		List<Cart> cartList = cartRepository.findAllByMemberSeqNoAndSalesType(cart.getMemberSeqNo(), cart.getSalesType());
		if(cartList != null && cartList.size() > 0){
			if(!cartList.get(0).getPageSeqNo().equals(cart.getPageSeqNo())){
				clearCart(cart.getMemberSeqNo(), cart.getSalesType());
			}
		}

		List<CartOption> cartOptionList = cart.getCartOptionList();
		String strDate = AppUtil.localDatetimeNowString();
		cart.setRegDatetime(strDate);
		cart.setModDatetime(strDate);
		cart = cartRepository.saveAndFlush(cart);
		for(CartOption cartOption : cartOptionList){
			cartOption.setCartSeqNo(cart.getSeqNo());
			cartOptionRepository.save(cartOption);
		}

		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateAmount(User user, Long cartSeqNo, Integer amount){
		Cart cart = cartRepository.findBySeqNoAndMemberSeqNo(cartSeqNo, user.getNo());
		cart.setAmount(amount);
		cart = cartRepository.saveAndFlush(cart);
		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteCart(User user, Long cartSeqNo){

		Cart cart = cartRepository.findBySeqNoAndMemberSeqNo(cartSeqNo, user.getNo());

		if(cart != null){
			cartOptionRepository.deleteByCartSeqNo(cart.getSeqNo());
			cartRepository.delete(cart);
		}

		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int clearCart(Long memberSeqNo, Integer salesType){
		if(salesType == 5){
			salesType = 2;
		}

		List<Cart> cartList = cartRepository.findAllByMemberSeqNoAndSalesType(memberSeqNo, salesType);

		for(Cart cart : cartList){
			cartOptionRepository.deleteByCartSeqNo(cart.getSeqNo());
		}

		cartRepository.deleteByMemberSeqNoAndSalesType(memberSeqNo, salesType);

		return Const.E_SUCCESS;
	}
}
