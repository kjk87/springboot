package kr.co.pplus.store;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ChainedTxConfig {

    @Bean
    @Primary
    public PlatformTransactionManager chainedTransactionManager(PlatformTransactionManager jpaTransactionManager, PlatformTransactionManager transactionManager) {
        return new ChainedTransactionManager(jpaTransactionManager, transactionManager);
    }
}
