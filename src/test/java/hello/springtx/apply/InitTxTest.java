package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;

@SpringBootTest
@Slf4j
public class InitTxTest {

    @Autowired
    Hello hello;

    @Test
    void go() {
        // @PostConstruct, @EventListener는 각각 알아서 시작됨.
    }

    @TestConfiguration
    static class InitTxTestConfig {
        @Bean
        public Hello hello() {
            return new Hello();
        }
    }

    @Slf4j
    static class Hello {

        @PostConstruct
        @Transactional
        public void initV1() {
            boolean activeTx = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active = {}", activeTx);
        }

        @EventListener(value = ApplicationReadyEvent.class)
        @Transactional
        public void initV2() {
            boolean activeTx = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @EventListener tx active = {}", activeTx);
        }


    }


}
