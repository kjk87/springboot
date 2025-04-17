package kr.co.pplus.store.api.jpa.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EchossService {
    private final static Logger logger = LoggerFactory.getLogger(EchossService.class);

    private final String TestApiKey = "d753785d6831149459518370d327bac85";
    private final String RealApiKey = "p1b7467e27d224d0cb6cfb26a1bf4fb1e";
    private final String ClientID = "c8743a26-007c-4a8c-815a-12f2e7d7f386";
    private final String ClientSecret = "32313261303261382D626135662D343837612D396165612D363466336562353831393535";
    private final String testUrl= "http://platform-function-dev.echoss.co.kr";
    private final String realUrl= "https://platform-function.echoss.co.kr";

    private HttpGet httpGet;
    private JSONObject data;

    public String verificationToken(String storeType, String token) throws Exception {

        String url = "";
        String apiKey = "";

        if (storeType.equals("PROD")) {
            url = realUrl + "/fcm/gateway/token/"+token;
//            apiKey = RealApiKey;
        }else {
            url = testUrl + "/fcm/gateway/token/"+token;
//            apiKey = TestApiKey;
        }

        // http client 생성
        CloseableHttpClient client = HttpClients.createDefault();
        httpGet = new HttpGet(url);

        httpGet.setHeader("Authorization", "Basic "+ getEncodeBase64String());
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("Accept-Charset", "UTF-8");


        HttpResponse res = client.execute(httpGet);

        String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        logger.debug("result : "+str);

        client.close();

        return str;
    }

    public String getEncodeBase64String(){
        return Base64.encodeBase64String((ClientID+":"+ClientSecret).getBytes());
    }
}
