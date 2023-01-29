package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
@Slf4j
public class InternalCallV2Test {

    @Autowired
    CallService callService;


    @Test
    void externalCall() {
        // AOP 적용됨.
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1Config {
        @Bean
        public CallService callService() {
            return new CallService(internalService());
        }

        @Bean
        public InternalService internalService() {
            return new InternalService();
        }
    }

    @RequiredArgsConstructor
    static class CallService {

        private final InternalService service;

        public void external() {
            log.info("call external");
            printTxInfo();
            service.internal();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active = {}", txActive);
        }
    }

    static class InternalService {

        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active = {}", txActive);
        }
    }

}
