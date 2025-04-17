package kr.co.pplus.store;

import kr.co.pplus.store.api.util.UnifiedArgumentResolver;
import kr.co.pplus.store.util.YamlPropertySourceFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.util.ErrorHandler;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@PropertySource(value = {"classpath:/application.yml"}, ignoreResourceNotFound = true, factory = YamlPropertySourceFactory.class)
@ImportResource("classpath:/kr/co/pplus/store/config/app-context.xml")
public class StoreConfig implements WebMvcConfigurer {

    private static final String[] RESOURCE_LOCATIONS = {
         /*   "classpath:/META-INF/resources/",  "classpath:/resources/", */
            "classpath:/static/", "classpath:/templates/"};

    @Autowired
    UnifiedArgumentResolver unifiedArgumentResolver ;

    @Value("${spring.activemq.broker-url}")
    String BROKER_URL = "tcp://localhost:61616" ;

    @Value("${spring.activemq.user}")
    String BROKER_USER = "admin" ;


    @Value("${spring.activemq.password}")
    String BROKER_PASSWORD = "admin" ;


    @Bean(name="messageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean(name="messageSourceAccessor")
    MessageSourceAccessor messageSourceAccessor(@Qualifier("messageSource") MessageSource messageSource){
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource) ;
        return messageSourceAccessor ;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(BROKER_URL);
        connectionFactory.setPassword(BROKER_USER);
        connectionFactory.setUserName(BROKER_PASSWORD);
        connectionFactory.setTrustAllPackages(true);
        return connectionFactory;
    }

    /*
    @Bean(name = {"jmsTemplate"})
    public JmsTemplate jmsTemplate(){
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        return template;
    }
    */

    @Bean
    public JmsListenerContainerFactory<?> myFactory(ActiveMQConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        // anonymous class
        factory.setErrorHandler(new ErrorHandler() {
            @Override
            public void handleError(Throwable t) {
                System.err.println("An error has occurred in the transaction: " + t.getMessage());
                t.printStackTrace();
            }
        });

        // lambda function
        //factory.setErrorHandler(t -> System.out.println("An error has occurred in the transaction: " ));

        configurer.configure(factory, connectionFactory);
        return factory;
    }

    /*
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
    */

    /*
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-1");
        return factory;
    }
    */


    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    }

    /*
    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }
    */


    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>
    webServerFactoryCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>() {
            @Override
            public void customize(ConfigurableServletWebServerFactory factory) {
                factory.setContextPath("/store");
            }
        };
    }

    @Bean
    public FilterRegistrationBean encodingFilterBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean() ;
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter() ;
        characterEncodingFilter.setForceEncoding(true);
        characterEncodingFilter.setEncoding("UTF-8");
        registrationBean.setFilter(characterEncodingFilter) ;
        return registrationBean ;
    }





    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setCache(true);
        resolver.setPrefix("/WEB-INF/view/");
        resolver.setSuffix(".jsp");
        resolver.setContentType("text/html; charset=utf-8");
        return resolver;
    }


    /*
    @Bean
    public FreeMarkerConfigurer freemarkerConfig()throws IOException, TemplateException {

        final FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:/META-INF/resources/WEB-INF/view/");
        Properties settings = new Properties();
        settings.setProperty(freemarker.template.Configuration.TEMPLATE_EXCEPTION_HANDLER_KEY, "rethrow");
        freeMarkerConfigurer.setFreemarkerSettings(settings);
        freeMarkerConfigurer.setDefaultEncoding("UTF-8");
        return freeMarkerConfigurer;
    }
    */


//    @Bean
//    public Docket api() {
//
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("kr.co.pplus.store.api.jpa.controller"))
//                .paths(PathSelectors.ant("/api/**"))
//                .build()
//                .apiInfo(apiInfo())
//                .useDefaultResponseMessages(false);
//    }

//    private ApiInfo apiInfo() {
//        @SuppressWarnings("deprecation")
//        ApiInfo apiInfo = new ApiInfo("API", "PRNUMBER API", "v1.0", "/", "PPlus", "PPlus", "/");
//        return apiInfo;
//    }

    /*
     * spring-boot locale 변경 인터셉터
     *
     * @return LocaleChangeInterceptor
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        // request로 넘어오는 language parameter를 받아서 locale로 설정 한다.
        localeChangeInterceptor.setParamName("language");
        return localeChangeInterceptor;
    }

    @Bean(name = "localeResolver")
    public SessionLocaleResolver sessionLocaleResolver() {
        // 세션 기준으로 로케일을 설정 한다.
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        // 쿠키 기준(세션이 끊겨도 브라우져에 설정된 쿠키 기준으로)
        // CookieLocaleResolver localeResolver = new CookieLocaleResolver();

        // 최초 기본 로케일을 강제로 설정이 가능 하다.
        localeResolver.setDefaultLocale(new Locale("ko"));
        return localeResolver;
    }



//    private Predicate<String> paths() {
//        return Predicates.not(PathSelectors.ant("/api"));
//    }


    @Override
    public void configurePathMatch(PathMatchConfigurer pathMatchConfigurer) {

    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer contentNegotiationConfigurer) {

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer asyncSupportConfigurer) {

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer defaultServletHandlerConfigurer) {

    }

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {

    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");


        registry.addResourceHandler("/**")
                .addResourceLocations(RESOURCE_LOCATIONS);

        /*
        registry.addResourceHandler("/paygate/**")
                .addResourceLocations("classpath:/META-INF/WEB-INF/view/paygate/") ;

        registry.addResourceHandler("/daum/**")
                .addResourceLocations("classpath:/META-INF/WEB-INF/view/daum/") ;
                */

//        if (!registry.hasMappingForPattern("/**")) {
//            registry.addResourceHandler("/**")
//                    .addResourceLocations(RESOURCE_LOCATIONS);
//        }
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        corsRegistry.addMapping("/*")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "OPTIONS", "PUT")
                .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Access-Control-Allow-Origin")
                .exposedHeaders("Access-Control-Allow-Credentials")
                .allowCredentials(true).maxAge(3600);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {

    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry viewResolverRegistry) {
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> list) {
        list.add(unifiedArgumentResolver) ;
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> list) {

    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));

    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> list) {

    }


    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }

}
