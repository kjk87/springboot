package kr.co.pplus.store.api.jpa.controller;

import com.google.gson.Gson;
import kr.co.pplus.store.StoreApplication;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Cancel;
import kr.co.pplus.store.api.jpa.model.bootpay.request.SubscribeBilling;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Token;
import kr.co.pplus.store.api.jpa.model.bootpay.response.ResToken;
import kr.co.pplus.store.api.jpa.model.bootpay.response.ResTokenV2;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class BootPayApi {

    private static final Logger logger = LoggerFactory.getLogger(BootPayApi.class);


    private final String BASE_URL = "https://api.bootpay.co.kr/";
    private final String URL_ACCESS_TOKEN = BASE_URL + "request/token";
    private final String URL_ACCESS_TOKEN_V2 = BASE_URL + "v2/request/token";
    private final String URL_VERIFY = BASE_URL + "receipt";
    private final String URL_GET = BASE_URL + "v2/receipt";
    private final String URL_CANCEL = BASE_URL + "v2/cancel";
    private final String URL_CERTIFICATE = BASE_URL + "certificate";//본인인증 검증하기
    private final String URL_SUBSCRIBE_BILLING = BASE_URL + "subscribe/billing";

    @Value("${STORE.BOOTPAY.CASH_APP_ID}")
    String CASH_APP_ID = "";

    @Value("${STORE.BOOTPAY.CASH_PRIVATE_KEY}")
    String CASH_PRIVATE_KEY = "";

    private String token;
    private String tokenV2;

    public BootPayApi() {}

    public void setToken(String token) {
        this.token = token;
    }

    private HttpGet getGet(String url) throws Exception {
        HttpGet get = new HttpGet(url);
        URI uri = new URIBuilder(get.getURI()).build();
        get.setURI(uri);
        return get;
    }

    private HttpGet getGet(String url, List<NameValuePair> nameValuePairList) throws Exception {
        HttpGet get = new HttpGet(url);
        get.setHeader("Accept", "application/json");
        get.setHeader("Content-Type", "application/json");
        get.setHeader("Accept-Charset", "utf-8");
        URI uri = new URIBuilder(get.getURI()).addParameters(nameValuePairList).build();
        get.setURI(uri);
        return get;
    }

    private HttpPost getPost(String url, StringEntity entity) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept-Charset", "utf-8");
        post.setEntity(entity);
        return post;
    }

    public void getAccessToken() throws Exception {
        if(StoreApplication.bootpayAppId == null || StoreApplication.bootpayAppId.isEmpty()) throw new Exception("application_id 값이 비어있습니다.");
        if(StoreApplication.bootpayPrivateKey == null || StoreApplication.bootpayPrivateKey.isEmpty()) throw new Exception("private_key 값이 비어있습니다.");

        Token token = new Token();
        token.application_id = StoreApplication.bootpayAppId ;
        token.private_key = StoreApplication.bootpayPrivateKey ;

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = getPost(URL_ACCESS_TOKEN, new StringEntity(new Gson().toJson(token), "UTF-8"));

        HttpResponse res = client.execute(post);
        String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        ResToken resToken = new Gson().fromJson(str, ResToken.class);

        logger.debug("bootPay.getAccessToken() response : " + str) ;
        if(resToken.status == 200)
            this.token = resToken.data.token;
    }

    public void getAccessTokenV2() throws Exception {
        if(StoreApplication.bootpayAppId == null || StoreApplication.bootpayAppId.isEmpty()) throw new Exception("application_id 값이 비어있습니다.");
        if(StoreApplication.bootpayPrivateKey == null || StoreApplication.bootpayPrivateKey.isEmpty()) throw new Exception("private_key 값이 비어있습니다.");

        Token token = new Token();
        token.application_id = StoreApplication.bootpayAppId ;
        token.private_key = StoreApplication.bootpayPrivateKey ;

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = getPost(URL_ACCESS_TOKEN_V2, new StringEntity(new Gson().toJson(token), "UTF-8"));

        HttpResponse res = client.execute(post);
        String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        ResTokenV2 resToken = new Gson().fromJson(str, ResTokenV2.class);

        logger.debug("bootPay.getAccessToken() response : " + str) ;
        if(res.getStatusLine().getStatusCode() == 200){
            this.tokenV2 = resToken.access_token;
        }

    }

    public HttpResponse certificate(String receipt_id) throws Exception {
        if(this.token == null || this.token.isEmpty()) throw new Exception("token 값이 비어있습니다.");

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = getGet(URL_CERTIFICATE + "/" + receipt_id);
        get.setHeader("Authorization", this.token);
        return client.execute(get);
    }

    public HttpResponse verify(String receipt_id) throws Exception {
        if(this.token == null || this.token.isEmpty()) throw new Exception("token 값이 비어있습니다.");

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = getGet(URL_VERIFY + "/" + receipt_id);
        get.setHeader("Authorization", this.token);
        return client.execute(get);
    }

    public HttpResponse get(String receipt_id) throws Exception {
        if(this.tokenV2 == null || this.tokenV2.isEmpty()) throw new Exception("token 값이 비어있습니다.");

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = getGet(URL_GET + "/" + receipt_id);
        get.setHeader("Authorization", "Bearer "+this.tokenV2) ;
        return client.execute(get);
    }

    public HttpResponse cancel(Cancel cancel) throws Exception {
        if(this.tokenV2 == null || this.tokenV2.isEmpty()) throw new Exception("token 값이 비어있습니다.");

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = getPost(URL_CANCEL, new StringEntity(new Gson().toJson(cancel), "UTF-8"));
        post.setHeader("Authorization", "Bearer "+this.tokenV2) ;
        return client.execute(post);
    }

    public HttpResponse subscribe_billing(SubscribeBilling subscribeBilling) throws Exception {
        if(this.token == null || this.token.isEmpty()) throw new Exception("token 값이 비어있습니다.");

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = getPost(URL_SUBSCRIBE_BILLING, new StringEntity(new Gson().toJson(subscribeBilling), "UTF-8"));
        post.setHeader("Authorization", this.token) ;
        return client.execute(post);
    }

}