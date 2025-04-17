package kr.co.pplus.store;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Configuration
//@EnableAutoConfiguration(
//        exclude = {DataSourceAutoConfiguration.class,
//                DataSourceTransactionManagerAutoConfiguration.class,
//                HibernateJpaAutoConfiguration.class})
@EnableTransactionManagement
@EntityScan(basePackages = {"kr.co.pplus.store.api.jpa.model"})
@EnableJpaRepositories(
        entityManagerFactoryRef = "jpaEntityManagerFactory",
        transactionManagerRef = "jpaTransactionManager",
        basePackages = {"kr.co.pplus.store.api.jpa.repository"})
public class JpaConfig {

    final String  dialect = "org.hibernate.dialect.MySQL57Dialect" ;
    String ddlAuto = "validate" ;
    String showSql = "true" ;
    String useNewIdGeneratorMappings = "false" ;
    String implicitStrategy = "org.hibernate.cfg.ImprovedNamingStrategy" ;
    String physicalStrategy = "org.hibernate.cfg.ImprovedNamingStrategy" ;

    @Autowired(required = false)
    private PersistenceUnitManager persistenceUnitManager;

    @Bean(name = "jpaDataSource")
    @ConfigurationProperties(prefix = "spring.jpa-datasource.hikari")
    public DataSource dataSource() {

        HikariDataSource dataSource =  DataSourceBuilder.create().type(HikariDataSource.class).build() ;
        dataSource.setConnectionInitSql("set @@session.time_zone = '$TIMEZONE'".replace("$TIMEZONE", Calendar.getInstance().getTimeZone().getID()));
//        return new LazyConnectionDataSourceProxy(dataSource);
        return dataSource;
    }


    /*
    @Bean(name = "jpaProperties")
    Map<String,String> jpaProperties() {

        HashMap<String, String> properties = new HashMap<String, String>() ;
        properties.put("hibernate.ddl-auto", ddlAuto);
        properties.put("show-sql", showSql);
        properties.put("hibernate.naming.implicit-strategy}", implicitStrategy);
        properties.put("hibernate.naming.physical-strategy}", physicalStrategy);
        properties.put("hibernate.use-new-id-generator-mappings", useNewIdGeneratorMappings);
        return properties ;
    }
    */


    @Bean(name = "jpaEntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder jpaEntityManagerFactoryBuilder(){
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setPrepareConnection(true);
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform(dialect);
        adapter.setGenerateDdl(false);

        HashMap<String, String> properties = new HashMap<String, String>() ;
        properties.put("hibernate.ddl-auto", ddlAuto);
        properties.put("show-sql", showSql);
        properties.put("hibernate.naming.implicit-strategy}", implicitStrategy);
        properties.put("hibernate.naming.physical-strategy}", physicalStrategy);
        properties.put("hibernate.use-new-id-generator-mappings", useNewIdGeneratorMappings);

        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(
                adapter, properties , this.persistenceUnitManager);
        //builder.setCallback(getVendorCallback());
        return builder;
    }

    @Primary
    @Bean(name = "jpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory(
            @Qualifier("jpaEntityManagerFactoryBuilder") EntityManagerFactoryBuilder builder,
            @Qualifier("jpaDataSource") DataSource jpaDataSource) {
        return builder
                .dataSource(jpaDataSource)
                .packages("kr.co.pplus.store.api.jpa.model")
                .persistenceUnit("store")
                .build();
    }


    @Bean(name = "jpaTransactionManager")
    public PlatformTransactionManager jpaTransactionManager(
            @Qualifier("jpaEntityManagerFactory") EntityManagerFactory jpaEntityManagerFactory) {
        return new JpaTransactionManager(jpaEntityManagerFactory);
    }
}