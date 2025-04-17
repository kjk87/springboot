package kr.co.pplus.store.api.jpa.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kr.co.pplus.store.api.jpa.model.DeliveryStatus;
import kr.co.pplus.store.api.jpa.model.PurchaseDelivery;
import kr.co.pplus.store.api.jpa.model.PurchaseProduct;
import kr.co.pplus.store.api.jpa.model.ShippingCompany;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.CommonService;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class ShippingService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(ShippingService.class);


//    private static final String API_KEY = "52GuLnLwyT1zfZ6XLy52Lg";
    private static final String BASE_URL = "http://info.sweettracker.co.kr/";

//    @Autowired
//    private BuyGoodsService buyGoodsService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    CommonService commonService;

    public List<ShippingCompany> getCompanyList() {
        List<ShippingCompany> shippingCompanyList = new ArrayList<>();
        try {

            String apiKey = commonService.getShippingKey();
            Map<String, String> params = new HashMap<String, String>();
            params.put("t_key", apiKey);
            String res = getRequest(BASE_URL + "api/v1/companylist", params, "UTF-8", 30000, 30000);
            Gson gson = new Gson() ;
            JsonObject jsonObj = gson.fromJson (res, JsonElement.class).getAsJsonObject();
            JsonArray array = jsonObj.getAsJsonArray("Company");

            ShippingCompany shippingCompany;



            List<String> unUseCodeList = Arrays.asList("12,13,21,14,26,25,28,33,38,48,51,57,63,67,73,78,84,93,99,29,34,41,49,52,60,65,69,76,80,87,95,100,30,37,42,50,55,61,66,70,77,81,91,97".split(","));

            for(JsonElement jsonElement : array){
                String code = jsonElement.getAsJsonObject().get("Code").getAsString();
                shippingCompany = new ShippingCompany();
                if(unUseCodeList.contains(code)){
                   continue;
                }
                shippingCompany.setCode(code);
                shippingCompany.setName(jsonElement.getAsJsonObject().get("Name").getAsString());
                shippingCompanyList.add(shippingCompany);
            }

        }catch (Exception e){
            logger.error(e.toString());
        }

        return shippingCompanyList;

    }

//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public void deliveryComplete(){
//
//        try {
//            List<BuyGoods> shippingList = buyGoodsService.getShippingList();
//
//            String dateStr = AppUtil.localDatetimeNowString();
//            Map<String, String> params;
//            for(BuyGoods buyGoods : shippingList){
//                if(StringUtils.isEmpty(buyGoods.getShippingCompanyCode())|| StringUtils.isEmpty(buyGoods.getTransportNumber())){
//                    continue;
//                }
//                params = new HashMap<>();
//                params.put("t_key", API_KEY);
//                params.put("t_code", buyGoods.getShippingCompanyCode());
//                params.put("t_invoice", buyGoods.getTransportNumber());
//                String res = getRequest(BASE_URL + "api/v1/trackingInfo", params, "UTF-8", 30000, 30000);
//                logger.debug("tracking result = "+res);
//                Gson gson = new Gson() ;
//                JsonObject jsonObj = gson.fromJson (res, JsonElement.class).getAsJsonObject();
//
//                JsonElement completeElement = jsonObj.get("completeYN");
//                if(completeElement != null){
//                    String completeYN = completeElement.getAsString();
//                    if(completeYN.equals("Y")){
//                        buyGoods.setOrderProcess(OrderProcess.DELIVERY_COMPLETE.getProcess());
//                        buyGoods.setDeliveryCompleteDatetime(dateStr);
//                        buyGoods.setModDatetime(dateStr);
//                        buyGoodsService.save(buyGoods);
//                    }
//                }
//            }
//        }catch (Exception e){
//            logger.error(e.toString());
//        }
//
//    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deliveryPurchaseComplete(){

        try {
            List<PurchaseProduct> shippingList = purchaseService.getShippingList();

            String dateStr = AppUtil.localDatetimeNowString();
            Map<String, String> params;
            String apiKey = commonService.getShippingKey();
            for(PurchaseProduct purchaseProduct : shippingList){
                PurchaseDelivery purchaseDelivery = purchaseService.getPurchaseDeliveryByPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                if(org.springframework.util.StringUtils.isEmpty(purchaseDelivery.getShippingCompanyCode())|| org.springframework.util.StringUtils.isEmpty(purchaseDelivery.getTransportNumber())){
                    continue;
                }


                params = new HashMap<>();
                params.put("t_key", apiKey);
                params.put("t_code", purchaseDelivery.getShippingCompanyCode());
                params.put("t_invoice", purchaseDelivery.getTransportNumber());
                String res = getRequest(BASE_URL + "api/v1/trackingInfo", params, "UTF-8", 30000, 30000);
                logger.debug("tracking result = "+res);
                Gson gson = new Gson() ;
                JsonObject jsonObj = gson.fromJson (res, JsonElement.class).getAsJsonObject();

                JsonElement completeElement = jsonObj.get("completeYN");
                if(completeElement != null){
                    String completeYN = completeElement.getAsString();
                    if(completeYN.equals("Y")){
                        purchaseDelivery.setDeliveryCompleteDatetime(dateStr);
                        purchaseDelivery = purchaseService.savePurchaseDelivery(purchaseDelivery);

                        purchaseProduct.setDeliveryStatus(DeliveryStatus.COMPLETE.getStatus());
                        purchaseProduct.setChangeStatusDatetime(dateStr);
                        purchaseProduct = purchaseService.savePurchaseProduct(purchaseProduct);
                    }
                }
            }
        }catch (Exception e){
            logger.error(e.toString());
        }

    }

    public String getRequest(String urlStr, Map<String, String> params, String charset, int connectionTimeout, int readTimeout) throws Exception {
        OutputStream os = null;
        HttpURLConnection conn = null;
        URL url = null;
        PrintWriter writer = null;

        if (params != null) {
            StringBuffer buf = new StringBuffer();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    buf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), charset));
                    first = false;
                } else {
                    buf.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), charset));
                }
            }

            if (!first) {
                urlStr += "?" + buf.toString();
            }
        }

        logger.debug("url : "+urlStr);

        url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectionTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setUseCaches(false);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
        StringBuffer sb = new StringBuffer();
        int read = 0;
        char[] buf = new char[1024];
        while ((read = br.read(buf)) > 0) {
            sb.append(buf, 0, read);
        }
        br.close();

        return sb.toString();
    }
}
