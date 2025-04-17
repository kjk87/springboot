package kr.co.pplus.store;

import org.apache.catalina.valves.AccessLogValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
/**
 * Created by kwonsungyang on 18/07/2018
 */
@Configuration
public class AccessLogConfig implements WebServerFactoryCustomizer {
    @Override
    public void customize(final WebServerFactory factory) {
        final TomcatServletWebServerFactory containerFactory = (TomcatServletWebServerFactory) factory;
        final AccessLogValve accessLogValve = new AccessLogValve();
        accessLogValve.setRenameOnRotate(true);
        accessLogValve.setPattern("%{yyyy-MM-dd HH:mm:ss}t\t%s\t%r\t%{User-Agent}i\t%{Referer}i\t%{X-Forwarded-For}i\t%b");
        accessLogValve.setDirectory(".");
        accessLogValve.setSuffix(".log");
        accessLogValve.setCondition("ignoreLogging");
        containerFactory.addContextValves(accessLogValve);
    }
}
