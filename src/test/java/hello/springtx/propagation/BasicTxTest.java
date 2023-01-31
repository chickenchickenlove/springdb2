package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class MyConfig{
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("트랜잭션 시작");
        TransactionStatus tx = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        txManager.commit(tx);
        log.info("트랜잭션 커밋 완료");
    }

    @Test
    void rollback() {
        log.info("트랜잭션 시작");
        TransactionStatus tx = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 롤백 시작");
        txManager.rollback(tx);
        log.info("트랜잭션 롤백 완료");
    }

    @Test
    void double_commit() {
        log.info("트랜잭션 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        txManager.commit(tx1);
        log.info("트랜잭션 커밋 완료");

        log.info("트랜잭션 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        txManager.commit(tx2);
        log.info("트랜잭션 커밋 완료");
    }

    @Test
    void double_commit_rollback() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션1 커밋 시작");
        txManager.commit(tx1);
        log.info("트랜잭션1 커밋 완료");

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션2 롤백 시작");
        txManager.rollback(tx2);
        log.info("트랜잭션2 롤백 완료");
    }

    @Test
    void inner_commit() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outerTx = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outerTx.isNewTransaction = {}", outerTx.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus innerTx = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("innerTx.isNewTransaction = {}", innerTx.isNewTransaction());

        log.info("내부 트랜잭션 커밋");
        txManager.commit(innerTx);

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outerTx);
    }

    @Test
    void outer_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outerTx = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outerTx.isNewTransaction = {}", outerTx.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus innerTx = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("innerTx.isNewTransaction = {}", innerTx.isNewTransaction());

        log.info("내부 트랜잭션 커밋");
        txManager.commit(innerTx);

        log.info("외부 트랜잭션 롤백");
        txManager.rollback(outerTx);
    }

    @Test
    void inner_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outerTx = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outerTx.isNewTransaction = {}", outerTx.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus innerTx = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("innerTx.isNewTransaction = {}", innerTx.isNewTransaction());

        log.info("내부 트랜잭션 롤백");
        txManager.rollback(innerTx);

        log.info("외부 트랜잭션 커밋");
        assertThatThrownBy(() -> txManager.commit(outerTx)).isInstanceOf(UnexpectedRollbackException.class);
    }

    @Test
    void inner_rollback_requires_new() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outerTx = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outerTx.isNewTransaction = {}", outerTx.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        DefaultTransactionAttribute defaultTransactionAttribute = new DefaultTransactionAttribute();
        defaultTransactionAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus innerTx = txManager.getTransaction(defaultTransactionAttribute);
        log.info("innerTx.isNewTransaction = {}", innerTx.isNewTransaction());

        log.info("내부 트랜잭션 롤백");
        txManager.rollback(innerTx);

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outerTx);
    }


}
