package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.*;

/**
 * logging.level.org.springframework.transaction.interceptor=TRACE
 * 위 로그를 추가하면 트랜잭션 시작 시점을 더 확인할 수 있음.
 */

@Slf4j
@SpringBootTest
public class TxApplyBasicTest {

    @Autowired
    BasicService basicService;

    // 트랜잭션 AOP 클래스 생성 확인
    @Test
    void proxyCheck() {
        log.info("aop class = {}", basicService.getClass());
        basicService.tx();
        assertThat(AopUtils.isAopProxy(basicService)).isTrue();
    }

    // 트랜잭션 AOP 로그 확인
    @Test
    void txTest() {
        basicService.tx();
        basicService.noTx();
    }


    @TestConfiguration
    static class TxApplyBasicConfig {

        @Bean
        public BasicService basicService() {
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService {

        // 트랜잭션 AOP 적용
        @Transactional
        public void tx() {
            log.info("call tx");
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active = {}", txActive);
        }

        // 트랜잭션 AOP 미적용
        public void noTx() {
            log.info("call tx");
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active = {}", txActive);
        }




    }

}
