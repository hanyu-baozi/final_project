package edu.crm;

import com.sun.net.httpserver.HttpServer;
import edu.crm.domain.customer.CustomerCenterGateway;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @ActiveProfiles("test")
class CustomerCenterContractTest {
    private static final AtomicInteger calls = new AtomicInteger();
    private static final AtomicBoolean failAlways = new AtomicBoolean();
    private static final HttpServer server = createServer();
    @Autowired CustomerCenterGateway gateway;
    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("customer-center.base-url", () -> "http://localhost:" + server.getAddress().getPort());
    }
    private static HttpServer createServer() {
        try {
            HttpServer result = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            result.createContext("/customers/by-id-number", exchange -> {
                int n = calls.incrementAndGet();
                String json = "{\"idNumber\":\"110101199001010011\",\"name\":\"客户中心\",\"accountStatus\":\"ACTIVE\",\"memberLevel\":\"VIP\",\"availableBalance\":100,\"holdings\":[]}";
                boolean failure = failAlways.get() || n <= 3;
                byte[] bytes = (failure ? "temporary failure" : json).getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(failure ? 503 : 200, bytes.length);
                try (var output = exchange.getResponseBody()) { output.write(bytes); }
            });
            result.start();
            return result;
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
    @Test
    void retriesThreeTimesThenReadsContractResponse() {
        calls.set(0);
        failAlways.set(false);
        long start = System.nanoTime();
        CustomerCenterGateway.CustomerData data = gateway.findByIdNumber("110101199001010011");
        assertEquals(4, calls.get());
        assertTrue((System.nanoTime() - start) / 1_000_000 >= 7000);
        assertEquals("客户中心", data.name());
        assertEquals("VIP", data.memberLevel());
    }
    @Test
    void propagatesFailureAfterThreeRetries() {
        calls.set(0);
        failAlways.set(true);
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> gateway.findByIdNumber("110101199001010011"));
        assertEquals("客户中心调用失败", error.getMessage());
        assertEquals(4, calls.get());
    }
    @AfterAll static void stopServer() { server.stop(0); }
}
