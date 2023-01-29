package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
public class RollbackTest {

    @Autowired
    RollbackService service;

    @Test
    void runtimeException() {
        assertThatThrownBy(() -> service.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void checkedException() {
        assertThatThrownBy(() -> service.checkedException())
                .isInstanceOf(Exception.class);
    }

    @Test
    void rollbackFor() {
        assertThatThrownBy(() -> service.rollbackFor())
                .isInstanceOf(Exception.class);
    }

    @TestConfiguration
    static class RollbackTestConfig {
        @Bean
        public RollbackService rollbackService() {
            return new RollbackService();
        }
    }


    static class MyException extends Exception {
    }

    @Slf4j
    static class RollbackService {

        @Transactional
        public void runtimeException() {
            // 런타임 예외 발생: 롤백
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        @Transactional
        public void checkedException() throws MyException {
            // 체크 예외 발생: 커밋
            log.info("call checkedException");
            throw new MyException();
        }

        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            // 체크 예외 rollbackFor 지정: 롤백
            log.info("call rollbackFor");
            throw new MyException();
        }
    }


}
