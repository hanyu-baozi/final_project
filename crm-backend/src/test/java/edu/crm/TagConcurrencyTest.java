package edu.crm;

import edu.crm.app.customer.CustomerService;
import edu.crm.app.customer.TagService;
import edu.crm.domain.customer.Customer;
import edu.crm.domain.customer.Tag;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class TagConcurrencyTest {
    @Autowired CustomerService customers;
    @Autowired TagService tags;
    @Autowired JdbcTemplate jdbc;

    @Test
    void concurrentAttachDoesNotCreateDuplicateActiveLinksAndHistoryRemains() throws Exception {
        Customer customer = new Customer();
        customer.setIdNumber("ID-TAG-CONCURRENT");
        customer = customers.create(customer);
        Tag tag = tags.create("并发标签");
        Long customerId = customer.getId();
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (var pool = Executors.newFixedThreadPool(2)) {
            Runnable attach = () -> {
                ready.countDown();
                try {
                    if (!start.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("等待启动超时");
                    tags.attach(customerId, tag.getId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(e);
                } catch (org.springframework.dao.DataIntegrityViolationException expected) {
                    // 唯一约束拒绝并发重复关系。
                }
            };
            var first = pool.submit(attach);
            var second = pool.submit(attach);
            if (!ready.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("等待线程就绪超时");
            start.countDown();
            first.get(30, TimeUnit.SECONDS);
            second.get(30, TimeUnit.SECONDS);
        }
        tags.detach(customerId, tag.getId());
        tags.attach(customerId, tag.getId());
        Long active = jdbc.queryForObject("select count(*) from customer_tag where customer_id = ? and tag_id = ? and active = true", Long.class, customerId, tag.getId());
        Long historical = jdbc.queryForObject("select count(*) from customer_tag where customer_id = ? and tag_id = ? and active = false", Long.class, customerId, tag.getId());
        assertEquals(1L, active.longValue());
        assertEquals(1L, historical.longValue());
    }
}
