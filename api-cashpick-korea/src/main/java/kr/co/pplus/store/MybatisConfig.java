package kr.co.pplus.store;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.PlatformTransactionManager ;
import javax.sql.DataSource;
import java.util.Calendar;


@Configuration
@EnableTransactionManagement
@MapperScan(basePackages="kr.co.pplus.store.mvc.service",
            sqlSessionFactoryRef="sqlSessionFactory")
public class MybatisConfig {


    @Value("${mybatis.config-location}")
    private String configLocation;

    @Value("${mybatis.mapper-locations}")
    private String mapperLocation;

    @Value("${mybatis.type-handlers-package}")
    private String typeHandlersPackage ;

    @Value("${mybatis.type-aliases-package}")
    private String typeAliasesPackage;

    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource() {

        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        dataSource.setConnectionInitSql("set @@session.time_zone = '$TIMEZONE'".replace("$TIMEZONE", Calendar.getInstance().getTimeZone().getID()));
//        return new LazyConnectionDataSourceProxy(dataSource);
        return dataSource;
    }


    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("transactionManager") PlatformTransactionManager transactionManager,
                                               ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean= new SqlSessionFactoryBean();

        sqlSessionFactoryBean. setDataSource(dataSource());
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource(configLocation)) ;
        sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
        sqlSessionFactoryBean.setTypeHandlersPackage(typeHandlersPackage);
        //sqlSessionFactoryBean.setMapperLocations(applicationContext.getResource(mapperLocation));
        //sessionFactory.setTypeAliasesPackage(typeAliasesPackage);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "sqlSession")
    public SqlSession sqlSession(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
