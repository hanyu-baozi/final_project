package edu.crm;

import edu.crm.app.sales.SalesService;
import edu.crm.domain.sales.Lead;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class LeadConcurrentConversionTest {
    @Autowired SalesService sales;
    @Autowired JdbcTemplate jdbc;

    @Test
    void concurrentConversionsCreateOnlyOneOpportunity() throws Exception {
        Lead lead = sales.createLead("并发测试线索");
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (var pool = Executors.newFixedThreadPool(2)) {
            var attempt = (java.util.concurrent.Callable<Boolean>) () -> {
                ready.countDown();
                if (!start.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("等待启动超时");
                try {
                    sales.convert(lead.getId(), "并发商机", "测试人员");
                    return true;
                } catch (RuntimeException expected) {
                    return false;
                }
            };
            Future<Boolean> first = pool.submit(attempt);
            Future<Boolean> second = pool.submit(attempt);
            if (!ready.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("等待线程就绪超时");
            start.countDown();
            int successes = (first.get(30, TimeUnit.SECONDS) ? 1 : 0) + (second.get(30, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, successes);
        }
        Long count = jdbc.queryForObject("select count(*) from opportunity where lead_id = ?", Long.class, lead.getId());
        assertEquals(1L, count.longValue());
        assertEquals("CONVERTED", sales.findLead(lead.getId()).getStatus());
    }
}
