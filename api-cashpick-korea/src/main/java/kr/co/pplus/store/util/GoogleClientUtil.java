package kr.co.pplus.store.util;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.ProductPurchase;


public class GoogleClientUtil {
	public static GoogleCredential credential(HttpTransport httpTransport, JsonFactory jsonFactory, String email, File p12File, Collection<String> serviceAccountScopes) throws IOException, GeneralSecurityException {
		return new GoogleCredential.Builder()
				.setTransport(httpTransport)
				.setJsonFactory(jsonFactory)
				.setServiceAccountId(email)
				.setServiceAccountPrivateKeyFromP12File(p12File)
				.setServiceAccountScopes(serviceAccountScopes)
				.build();
	}


	public static AndroidPublisher getPublisher(HttpTransport httpTransport, JsonFactory jsonFactory, GoogleCredential credential, String packageName) {
		
		return new AndroidPublisher.Builder(httpTransport, jsonFactory, credential)
				.setApplicationName(packageName).build();
	}
	
	
	public static Map<String, Object> confirmPurchase(String email, String p12Path, String packageName, String productId, String purchaseToken) throws IOException, GeneralSecurityException, GoogleJsonResponseException  {
		//https://www.googleapis.com/auth/androidpublisher
		System.out.println("packageName=" + packageName + "&productId=" + productId + "&purchaseToken=" + purchaseToken);
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		File p12 = new File(p12Path);
		GoogleCredential crediential = credential(httpTransport, jsonFactory, email, p12, Collections.singleton("https://www.googleapis.com/auth/androidpublisher"));
		AndroidPublisher publisher = getPublisher(httpTransport, jsonFactory, crediential, packageName);
		AndroidPublisher.Purchases.Products.Get get = publisher.purchases().products().get(packageName, productId, purchaseToken);
		ProductPurchase productPurchase = get.execute();
		System.out.println(productPurchase.toPrettyString());
		
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("consumptionState", productPurchase.getConsumptionState());
		res.put("developerPayload", productPurchase.getDeveloperPayload());
		res.put("purchaseState", productPurchase.getPurchaseState());
		res.put("purchaseTimeMillis", productPurchase.getPurchaseTimeMillis());
		 return res;
	}
	 
}
