package kr.co.pplus.store.queue;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.ErrorCode;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.MsgService;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.type.model.UserApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class Firebase {

    @Autowired
    MsgService msgSvc ;

    private final static Logger logger = LoggerFactory.getLogger(Firebase.class);
    static Firebase firebaseLuckyBol = null ;
    static Firebase firebaseUser = null ;
    static Firebase firebaseBiz = null ;
    static Firebase firebaseOrder = null ;

    private static final String SCOPES[] = {
        "https://www.googleapis.com/auth/firebase.database",
        "https://www.googleapis.com/auth/userinfo.email",
        "https://www.googleapis.com/auth/firebase",
        "https://www.googleapis.com/auth/cloud-platform",
        "https://www.googleapis.com/auth/firebase.readonly"
    } ;

    private static final String FCM_LUCKYBOL_SDK_KEY_JSON = "google/luckybol-firebase-adminsdk-6jh5y-a0b099beb2.json";
    private static final String FCM_LUCKYBOL_SEND_URL = "https://fcm.googleapis.com/v1/projects/luckybol/messages:send" ;
    private static final String FCM_LUCKYBOL_DB_URL = "https://luckybol.firebaseio.com" ;

    private static final String FCM_USER_SDK_KEY_JSON ="google/project-8733797936564516582-firebase-adminsdk-jvnvk-9b4b87ab6b.json" ;
    private static final String FCM_USER_SEND_URL = "https://fcm.googleapis.com/v1/projects/project-8733797936564516582/messages:send" ;
    private static final String FCM_USER_DB_URL = "https://project-8733797936564516582.firebaseio.com" ;

    private static final String FCM_BIZ_SDK_KEY_JSON ="google/prnumberbiz-firebase-adminsdk-e51cl-19e3455c4f.json" ;
    private static final String FCM_BIZ_SEND_URL = "https://fcm.googleapis.com/v1/projects/prnumberbiz/messages:send" ;
    private static final String FCM_BIZ_DB_URL = "https://prnumberbiz.firebaseio.com" ;

    private static final String FCM_ORDER_SDK_KEY_JSON ="google/orimarketorder-8c1fe43094ec.json" ;
    private static final String FCM_ORDER_DB_URL = "https://orimarketorder.firebaseio.com" ;


    private AccessToken accessToken = null ;
    private GoogleCredentials googleCredentials = null ;
    private String dbUrl = null ;
    private String jsonKey = null ;
    private FirebaseOptions options = null ;
    private FirebaseApp firbaseApp = null ;
    private FirebaseMessaging firebaseMessaging = null ;

    public interface PushResultHandler {
        void error(Object arg, User user, String cause);

        void send(Object arg, User user);

        void notFoundPushKey(String pushKey);
    }

    public FirebaseApp getFirbaseApp() {
        return this.firbaseApp ;
    }

    public FirebaseMessaging getFirebaseMessaging() {
         return this.firebaseMessaging ;
    }


    public static Firebase getLuckyBolInstance(ApplicationContext context) {

        Firebase firebase = getLuckyBolInstance() ;
        firebase.msgSvc = (MsgService)context.getBean("msgService") ;
        return firebase ;
    }

    public static Firebase getUserInstance(ApplicationContext context) {

        Firebase firebase = getUserInstance() ;
        firebase.msgSvc = (MsgService)context.getBean("msgService") ;
        return firebase ;
    }

    public static Firebase getBizInstance(ApplicationContext context) {

        Firebase firebase = getBizInstance() ;
        firebase.msgSvc = (MsgService)context.getBean("msgService") ;
        return firebase ;
    }

    public static Firebase getOrderInstance() {
        if (firebaseOrder == null) {
            firebaseOrder = new Firebase();
            firebaseOrder.dbUrl = FCM_ORDER_DB_URL;
            firebaseOrder.jsonKey = FCM_ORDER_SDK_KEY_JSON;

            firebaseOrder.createApp() ;
        }

        return firebaseOrder;
    }

    public static Firebase getLuckyBolInstance() {
        if (firebaseLuckyBol == null) {
            firebaseLuckyBol = new Firebase();
            firebaseLuckyBol.dbUrl = FCM_LUCKYBOL_DB_URL;
            firebaseLuckyBol.jsonKey = FCM_LUCKYBOL_SDK_KEY_JSON;

            firebaseLuckyBol.createApp() ;
        }

        return firebaseLuckyBol;
    }

    public static Firebase getUserInstance() {
        if (firebaseUser == null) {
            firebaseUser = new Firebase();
            firebaseUser.dbUrl = FCM_USER_DB_URL;
            firebaseUser.jsonKey = FCM_USER_SDK_KEY_JSON;

            firebaseUser.createApp() ;
        }

        return firebaseUser;
    }

    public static Firebase getBizInstance() {
        logger.info("getBizInstance") ;
        if (firebaseBiz == null) {
            logger.info("firebaseBiz null") ;
            firebaseBiz = new Firebase();
            firebaseBiz.dbUrl = FCM_BIZ_DB_URL ;
            firebaseBiz.jsonKey = FCM_BIZ_SDK_KEY_JSON ;
            firebaseBiz.createApp() ;
        }
        return firebaseBiz;
    }

    public static File toFile(URL url) {
        if (url == null || !url.getProtocol().equals("file")) {
            return null;
        } else {
            String filename = url.getFile().replace('/', File.separatorChar);
            int pos =0;
            while ((pos = filename.indexOf('%', pos)) >= 0) {
                if (pos + 2 < filename.length()) {
                    String hexStr = filename.substring(pos + 1, pos + 3);
                    char ch = (char) Integer.parseInt(hexStr, 16);
                    filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
                }
            }
            return new File(filename);
        }
    }

    public static InputStream getClassLoaderFile(String filename) throws Exception  {
        // note that this method is used when initializing logging, so it must
        // not attempt to log anything.

        File file = null;
        ClassLoader loader = Firebase.class.getClassLoader();
        InputStream inputStream = loader.getResourceAsStream(filename);
        if( inputStream != null ) {
            return inputStream ;
        } else {
            URL url = loader.getResource(filename);
            if (url == null) {
                url = ClassLoader.getSystemResource(filename);
                if (url == null) {
                    throw new Exception("Unable to find " + filename);
                }
                file = toFile(url);
            } else {
                file = toFile(url);
            }
            if (file == null || !file.exists()) {
                return null;
            }
            return new FileInputStream(file);
        }
    }


    public FirebaseApp createApp() {

        try {
            logger.info("serviceAccount init") ;
            InputStream serviceAccount = this.getClassLoaderFile(this.jsonKey);
            if( serviceAccount == null ) {
                throw new Exception("Firebase serviceAccount is null : " + this.jsonKey) ;
            }
            logger.info("serviceAccount init2") ;
            this.googleCredentials =  GoogleCredentials.fromStream(serviceAccount).createScoped(Arrays.asList(SCOPES));
            logger.info("googleCredentials init");
            serviceAccount.close() ;
            logger.info("serviceAccount close") ;
            this.options = FirebaseOptions.builder()
                    .setCredentials(this.googleCredentials)
                    .setDatabaseUrl(this.dbUrl)
                    .build();

            this.firbaseApp = FirebaseApp.initializeApp(this.options, this.dbUrl);


            if(firbaseApp == null){
                logger.info("firbaseApp is null") ;
            }

            this.firebaseMessaging = FirebaseMessaging.getInstance(this.firbaseApp) ;

            return this.firbaseApp ;

        } catch(Exception e) {
            logger.error("createApp exception : " + AppUtil.excetionToString(e)) ;
            return null ;
        }
    }

    public void sendMulticastPush(Map<String, String> data, List<UserApp> targetList, PushResultHandler resultHandler, Object handlerArg) throws Exception {

        try {
            logger.debug("sendPush message : " + data.toString());

            List<String> pushKeyArr = new ArrayList<String>() ;
            List<UserApp> sepTargetList = new ArrayList<>();
//            for (UserApp target : targetList) {
//                logger.info("targetKey : "+target.getPushKey()) ;
//                pushKeyArr.add(target.getPushKey()) ;
//            }

            for(int i = 0; i < targetList.size(); i++){
                if(i%100 == 0){
                    if(i != 0){
                        send(data, sepTargetList, pushKeyArr, resultHandler, handlerArg);
                        Thread.sleep(500);
                    }
                    sepTargetList = new ArrayList<>();
                    pushKeyArr = new ArrayList<>();
                }

                sepTargetList.add(targetList.get(i));
                logger.debug("pushkey : "+targetList.get(i).getPushKey());
                pushKeyArr.add(targetList.get(i).getPushKey());
            }

            if(pushKeyArr.size() > 0){
                send(data, sepTargetList, pushKeyArr, resultHandler, handlerArg);
            }

//            Map<String, Object> apnsData = new HashMap<String, Object>();
//            apnsData.putAll(data);


        } catch(Exception e) {
            logger.error("sendPush exception : " + AppUtil.excetionToString(e)) ;
        } finally {
            //try { this.firbaseApp.delete(); } catch(Exception e){}
        }
    }

    private void send(Map<String, String> data, List<UserApp> targetList, List<String> pushKeyArr, PushResultHandler resultHandler, Object handlerArg) throws Exception{

        MulticastMessage message = MulticastMessage.builder()
//                    .setNotification(new Notification(
//                            data.get("title"),
//                            data.get("contents")))
                .putAllData(data)
                .addAllTokens(pushKeyArr)
                .setApnsConfig(ApnsConfig.builder().setAps(Aps.builder().setAlert(data.get("title")).setBadge(0).build()).build())
                .build();

        if(firebaseMessaging == null){
            logger.info("firebaseMessaging is null") ;
            return;
        }
        logger.info("sendPush Message : " + message.toString()) ;
        logger.info("sendPush firebaseMessaging : " + this.firebaseMessaging.toString()) ;
        BatchResponse response = this.firebaseMessaging.sendMulticast(message) ;

        if( response != null ) {
            logger.debug("sendPush response : " + response) ;
            int i = 0;

            List<Long> successSeqNoList = new ArrayList<Long>() ;
            for (SendResponse res : response.getResponses()) {

                try {
                    String id = res.getMessageId();
                    UserApp ua = targetList.get(i);
                    if (res.isSuccessful()) {
                        logger.error("Success pushKey : " + ua.getPushKey()) ;
                        if(!successSeqNoList.contains(ua.getUser().getNo())){
                            successSeqNoList.add(ua.getUser().getNo());
                        }
                        if( resultHandler != null ){
                            resultHandler.send(handlerArg, ua.getUser());
                        }

                    } else {

                        logger.error("Failed platform : " + ua.getDevice().getPlatform()) ;
                        logger.error("Failed pushKey : " + ua.getPushKey()) ;
                        logger.error("Firebase sendPush Error : " + res.getException().getErrorCode() + " " +res.getException().getMessage()) ;
                        logger.error(AppUtil.excetionToString(res.getException())) ;
                        if( resultHandler != null ){
                            if(res.getException().getErrorCode().equals(ErrorCode.NOT_FOUND)){
                                resultHandler.notFoundPushKey(ua.getPushKey());
                            }

                            if(!successSeqNoList.contains(ua.getUser().getNo())){
                                resultHandler.error(handlerArg, ua.getUser(), "Firebase Send Response Error");
                            }
                        }

                    }
                    i++;
                } catch(Exception e) {
                    logger.error(AppUtil.excetionToString(e)) ;
                }
            }
        }
    }

    public static void main(String argv[]) throws Exception {


        InputStream serviceAccount = getClassLoaderFile(FCM_ORDER_SDK_KEY_JSON);
        if( serviceAccount == null ) {
            throw new Exception("Firebase serviceAccount is null : " + FCM_ORDER_SDK_KEY_JSON) ;
        }
        GoogleCredentials googleCredentials =  GoogleCredentials.fromStream(serviceAccount).createScoped(Arrays.asList(SCOPES));
        serviceAccount.close() ;

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .setDatabaseUrl(FCM_ORDER_DB_URL)
                .build();

        FirebaseApp firebaseApp = FirebaseApp.initializeApp(options, FCM_ORDER_DB_URL);
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp) ;


        Map<String, String> data = new HashMap<>();
        data.put("move_type1", "inner");
        data.put("move_type2", "order");
        data.put("contents", "매장 사정으로 인해 주문접수가 거절되었습니다.");
        data.put("move_target", "461");
        data.put("title", "오리마켓[취소완료]");

        List<String> pushKeyArr = new ArrayList<>();
        pushKeyArr.add("eze6x14SRXuMGTX_VwhagH:APA91bG1Uk83XxXFjyZuDQkHFSyXqupCFUh7hCIGf3rehOaA4nKO8dd_geMcGB0n4oMhLvucmwvXbANgP__Rlf3yd9YrkqJLWUg3xEhWOcXYCRWH9LVi--yaspqu6F-qy51MoBOBrPWv");
        MulticastMessage message = MulticastMessage.builder()
                .putAllData(data)
                .addAllTokens(pushKeyArr)
                .setApnsConfig(ApnsConfig.builder().setAps(Aps.builder().setAlert(data.get("title")).setBadge(0).build()).build())
                .build();

        BatchResponse response = firebaseMessaging.sendMulticast(message) ;
        for(SendResponse res : response.getResponses()){
            if (!res.isSuccessful()) {
                logger.error("Firebase sendPush Error : " + res.getException().getErrorCode() + " " +res.getException().getMessage()) ;
            }

        }
    }

}
