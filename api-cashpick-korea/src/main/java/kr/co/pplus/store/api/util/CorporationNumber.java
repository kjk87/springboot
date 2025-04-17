package kr.co.pplus.store.api.util;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

public class CorporationNumber {

    private final String url = "https://teht.hometax.go.kr/wqAction.do?actionId=ATTABZAA001R08&screenId=UTEABAAA13&popupYn=false&realScreenId=";

    private boolean flag;

    CloseableHttpClient client;

    public boolean getInfo(String number) {

        flag = false;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/xml; charset=utf-8");

        byte[] xml;
        try {
            xml = this.getXml(number).getBytes("UTF-8");
            httpPost.setEntity(new ByteArrayEntity(xml));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 응답을 문자열로 변환하는 Handler 를 생성합니다.
        ResponseHandler<String> responseHandler = new BasicResponseHandler();

        String responseString;
        try {
            responseString = client.execute(httpPost, responseHandler);


            client.close();

            DocumentBuilderFactory factory  =  DocumentBuilderFactory.newInstance();
            DocumentBuilder builder    =  factory.newDocumentBuilder();

            Document document     =  builder.parse(new InputSource(new StringReader(responseString)));

            NodeList text     =  document.getElementsByTagName("trtCntn");  // 부가가치세 일반과세자 입니다. 사업을 하지 않고 있습니다.
            NodeList yN     =  document.getElementsByTagName("nrgtTxprYn"); // N, Y

            Node textNode      =  text.item(0).getChildNodes().item(0);
            Node yNNode      =  yN.item(0).getChildNodes().item(0);

            //element의 text 얻기

            if(textNode.getNodeValue().equals("부가가치세 일반과세자 입니다.") && yNNode.getNodeValue().equals("N")) {
                flag = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;

    }

    public String getXml(String number) {
        StringBuffer sb = new StringBuffer();
        sb.append("<map id='ATTABZAA001R08'><pubcUserNo/><mobYn>N</mobYn><inqrTrgtClCd>1</inqrTrgtClCd><txprDscmNo>");
        sb.append(number);
        sb.append("</txprDscmNo><dongCode>05</dongCode><psbSearch>Y</psbSearch><map id='userReqInfoVO'/></map>");

        return sb.toString();
    }

}
