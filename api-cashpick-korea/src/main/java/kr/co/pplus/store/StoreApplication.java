package kr.co.pplus.store;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import kr.co.pplus.store.api.interceptor.SecurityInterceptor;
import kr.co.pplus.store.mvc.service.AttachmentService;
import kr.co.pplus.store.mvc.service.PageService;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.queue.Firebase;
import kr.co.pplus.store.type.model.Attachment;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.util.RedisUtil;
import kr.co.pplus.store.util.StoreUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.web.servlet.handler.MappedInterceptor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Slf4j
@EnableJms
public class StoreApplication extends SpringBootServletInitializer {

    private final static Logger logger = LoggerFactory.getLogger(StoreApplication.class);

    @Autowired
    SecurityInterceptor securityInterceptor ;

    static UserService userSvc ;
    static PageService pageSvc ;
    static AttachmentService attachSvc ;

    static ApplicationContext appContext ;
//    static String host ;
//    static Integer port ;
//    static String usePool ;
//    static String password ;

    static String REDIS_PREFIX ;
    public static String bootpayAppId ;
    public static String bootpayPrivateKey ;
    //    public static Boolean SCHEDULER_ACTIVATE ;
    public static String SERVER_NAME ;
    public static String SMS_CLIENT_KEY ;


    @Bean
    public MappedInterceptor myMappedInterceptor() {
        logger.debug("myMappedInterceptor()") ;
        return new MappedInterceptor(new String[]{ "/**"},
                new String[]{ "/resource/**"},
                securityInterceptor);
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("basicScheme",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .info(new Info().title("Store API").version(appVersion).description(
                        "This is a Store API server")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StoreApplication.class);
    }

    public static void main(String[] args) {

        StoreApplication.appContext = SpringApplication.run(StoreApplication.class, args) ;
        initFirebaseApp(StoreApplication.appContext) ;

        StoreApplication.bootpayAppId = StoreApplication.appContext.getEnvironment().getProperty("STORE.BOOTPAY.CASH_APP_ID") ;
        StoreApplication.bootpayPrivateKey = StoreApplication.appContext.getEnvironment().getProperty("STORE.BOOTPAY.CASH_PRIVATE_KEY") ;
        StoreApplication.SERVER_NAME = StoreApplication.appContext.getEnvironment().getProperty("STORE.SERVER_NAME") ;
        StoreApplication.REDIS_PREFIX = StoreApplication.appContext.getEnvironment().getProperty("STORE.REDIS_PREFIX") ;
        StoreApplication.SMS_CLIENT_KEY = StoreApplication.appContext.getEnvironment().getProperty("STORE.SMS_CLIENT_KEY") ;

        logger.info("SERVER_NAME : " + StoreApplication.SERVER_NAME) ;
        if( !StoreApplication.SERVER_NAME.startsWith("PROD") ) {
            logger.info("BootPay.bootpayAppId : " + StoreApplication.bootpayAppId);
            logger.info("BootPay.PrivateKey : " + StoreApplication.bootpayPrivateKey);
            logger.info("Redis.REDIS_PREFIX : " + StoreApplication.REDIS_PREFIX);
        }

        if( args.length == 0 ) {
            init(StoreApplication.appContext);
//            initHashtag(StoreApplication.appContext);
//            initGoodsInfo(StoreApplication.appContext);

        } else if( StoreApplication.SERVER_NAME.equals("PROD2TEST") ) {
//            initHashtag(StoreApplication.appContext);
//            initGoodsInfo(StoreApplication.appContext);
        }
    }

    public static void initFirebaseApp(ApplicationContext context) {
        try {
            Firebase.getLuckyBolInstance(context);
            Firebase.getUserInstance(context);
            Firebase.getBizInstance(context);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public static void init(ApplicationContext context) {
        int page = 1, size = 1000;
        SearchOpt opt = new SearchOpt();

        try {

            if (RedisUtil.getInstance().redisTemplate == null) {
                RedisUtil.getInstance().redisTemplate = (RedisTemplate) context.getBean("redisTemplate");
            }

            userSvc = (UserService) context.getBean("userService");
            pageSvc = (PageService) context.getBean("pageService");
            attachSvc = (AttachmentService) context.getBean("attachmentService");

            logger.info("doAfterStartup()....");

            Attachment attach = new Attachment() ;
            attach.setTargetType("pageBackground");
            List<Attachment> attachList = attachSvc.getDefaultImageList(attach) ;

            RedisUtil.getInstance().deleteObj(REDIS_PREFIX + "pageBackground");
            RedisUtil.getInstance().putOpsHash(REDIS_PREFIX + "pageBackground", "defaultList", attachList);

            RedisUtil.getInstance().deleteObj(REDIS_PREFIX + "loginId");
            do {
                opt.setPg(page);
                opt.setSz(size);
                List<String> idList = userSvc.getUserLoginIdList(opt);
                for (String id : idList) {
                    RedisUtil.getInstance().putOpsHash(REDIS_PREFIX + "loginId", id, "1");
                }

                if (idList == null || idList.size() == 0) {
                    break;
                }
                logger.info(REDIS_PREFIX + "loginId : " + page);
                page++;

            } while (true);

            logger.info("doAfterStartup() is done...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }


    public static void initHashtag(ApplicationContext context)  {
        int page = 1, size = 1000 ;
        SearchOpt opt = new SearchOpt() ;

        try {
            if (RedisUtil.getInstance().redisTemplate == null) {
                RedisUtil.getInstance().redisTemplate = (RedisTemplate) context.getBean("redisTemplate");
            }

            RedisUtil.getInstance().deleteObj(REDIS_PREFIX + "hashtag");
            InputStream is = StoreUtil.getClassLoaderFile("kr/co/pplus/store/hashtag.txt") ;
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8")) ;
            String hashtag = null ;
            while( (hashtag=br.readLine()) != null ) {
                hashtag = hashtag.trim() ;
                logger.info(REDIS_PREFIX + "hashtag : " + hashtag);
                RedisUtil.getInstance().hSet(REDIS_PREFIX + "hashtag", hashtag, "1");
            }
            br.close() ;
            is.close() ;

            is = StoreUtil.getClassLoaderFile("kr/co/pplus/store/hashtag.csv") ;
            br = new BufferedReader(new InputStreamReader(is, "UTF-8")) ;
            String line = null ;
            while( (line=br.readLine()) != null ) {
                String[] fields = line.split(":") ;
                if( fields.length != 2 || line.trim().startsWith("#") )
                    continue ;

                String hashtagCategory = fields[0] ;
                RedisUtil.getInstance().hSet(REDIS_PREFIX + "hashtagCategory", hashtagCategory, fields[1]);
                logger.info(REDIS_PREFIX + "hashtagCategory : " + fields[1]);
                String[] hashtags = fields[1].split(",") ;
                for(String tag : hashtags) {
                    RedisUtil.getInstance().hSet(REDIS_PREFIX + "hashtag", tag, "1");
                }
            }
            br.close() ;
            is.close() ;


            page = 1 ;
            size = 1000 ;
            do {
                opt.setPg(page);
                opt.setSz(size);
                List<String> hashtagList = pageSvc.getPageHashtagList(opt);
                for (String pageHashtag : hashtagList) {
                    if( pageHashtag != null && !pageHashtag.trim().isEmpty() ) {
                        String tags[] = pageHashtag.split(",") ;
                        for( int k=0 ; k<tags.length ; k++ ) {
                            RedisUtil.getInstance().putOpsHash(REDIS_PREFIX + "hashtag", tags[k].trim(), "1");
                            logger.info(REDIS_PREFIX + "hashtag : " + tags[k]);
                        }
                    }
                }

                if (hashtagList == null || hashtagList.size() == 0) {
                    break;
                }

                page++;

            } while (true);

            logger.info("initHashtag() is done...");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
        }

    }

    static final String requiredGoodsInfoFields[] = {
            "품명 및 모델명",
            "크기/중량/치수/수량",
            "제조자 및 판매자",
            "제조국(원산지)",
            "품질보증기준",
            "소비자 상담전화(A/S책임자 전화번호)",
            "취급시 주의사항"
    } ;
    public static Map<String, Boolean> requiredGoodsInfoMap = new HashMap<String, Boolean>() ;
    public static void initGoodsInfo(ApplicationContext context)  {
        int page = 1, size = 1000 ;
        SearchOpt opt = new SearchOpt() ;

        try {

            for(String field : requiredGoodsInfoFields) {
                requiredGoodsInfoMap.put(field, true) ;
            }
            if (RedisUtil.getInstance().redisTemplate == null) {
                RedisUtil.getInstance().redisTemplate = (RedisTemplate) context.getBean("redisTemplate");
            }

            RedisUtil.getInstance().deleteObj(REDIS_PREFIX + "goodsInfoCategory");


            InputStream is = StoreUtil.getClassLoaderFile("kr/co/pplus/store/goodsinfo.csv") ;
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8")) ;
            String line = null ;
            while( (line=br.readLine()) != null ) {
                String[] fields = line.split(":") ;
                if( fields.length != 2 || line.trim().startsWith("#") )
                    continue ;

                String goodsInfoCategory = fields[0] ;
                RedisUtil.getInstance().hSet(REDIS_PREFIX + "goodsInfoCategory", goodsInfoCategory, fields[1]);
                logger.info(REDIS_PREFIX + "goodsInfoCategory : " + goodsInfoCategory + ":" + fields[1]);
            }
            br.close() ;
            is.close() ;

            logger.info("initGoodsInfo() is done...");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
        }

    }
}
