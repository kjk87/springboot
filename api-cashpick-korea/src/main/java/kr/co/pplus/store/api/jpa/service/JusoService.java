package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.Juso;
import kr.co.pplus.store.api.jpa.model.Province;
import kr.co.pplus.store.api.jpa.repository.ProvinceRepository;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.util.StoreUtil;
import kr.co.pplus.store.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class JusoService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(JusoService.class);

	@Autowired
	ProvinceRepository provinceRepository;

	private static final String JUSO_ADDRESS = "http://www.juso.go.kr/getAreaCode.do";

	public List<Province> getProvinceList(){
		return provinceRepository.findAll();
	}

	public List<Juso> getGuList(String value){
		Map<String, String> params = new HashMap<String, String>();
		params.put("from", "city");
		params.put("to", "county");
		params.put("valFrom", value);
		params.put("valTo", "county1");
		return getJusoList(params);

	}

	public List<Juso> getDongList(String value){
		Map<String, String> params = new HashMap<String, String>();
		params.put("from", "county");
		params.put("to", "town");
		params.put("valFrom", value);
		return getJusoList(params);

	}

	private List<Juso> getJusoList(Map<String, String> params){
		List<Juso> jusoList = new ArrayList<>();
		try {
			String res = StoreUtil.getRequest(JUSO_ADDRESS, params, "UTF-8", 30000, 30000);

			Document doc = XmlUtil.parseXml(res);
			NodeList valueNodeList = doc.getDocumentElement().getElementsByTagName("value");
			NodeList nameNodeList = doc.getDocumentElement().getElementsByTagName("name");
			NodeList engNameNodeList = doc.getDocumentElement().getElementsByTagName("engname");
			Juso juso = null;
			for(int i = 0; i < valueNodeList.getLength(); i++){
				juso = new Juso();
				juso.setValue(valueNodeList.item(i).getTextContent());
				juso.setName(nameNodeList.item(i).getTextContent());
				juso.setEngname(engNameNodeList.item(i).getTextContent());
				jusoList.add(juso);
			}

		}catch (Exception e){

		}

		return jusoList;

	}
}
