package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.QrCode;
import kr.co.pplus.store.api.jpa.repository.QrCodeRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.CommonService;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class QrCodeService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(QrCodeService.class);

	@Autowired
	private QrCodeRepository qrCodeRepository;

	@Autowired
	CommonService commonService;

	@Value("${STORE.TYPE}")
	String storeType = "LOCAL" ;

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public QrCode getQrCodeByPagSeqNo(Long pageSeqNo) throws ResultCodeException {
		QrCode qrCode = qrCodeRepository.findByPageSeqNo(pageSeqNo);
		if(qrCode == null){
			Long max = qrCodeRepository.findMaxSeqNo();
			if(max < 10000){
				String dateStr = AppUtil.localDatetimeNowString();
				String code = "p"+String.format("%04d", max+1);
				qrCode = new QrCode();
				qrCode.setCode(code);
				qrCode.setPageSeqNo(pageSeqNo);
				qrCode.setRegDatetime(dateStr);
				qrCode.setConDatetime(dateStr);

				if( storeType.equals("PROD") ) {
					qrCode.setQrImage(commonService.makeQrCode("https://www.plusmember.co.kr/shop/qr/"+code));
				}else{
					qrCode.setQrImage(commonService.makeQrCode("https://stg-www.plusmember.co.kr/shop/qr/"+code));
				}
				qrCode = qrCodeRepository.saveAndFlush(qrCode);
			}
		}

		return qrCode;
	}
}
