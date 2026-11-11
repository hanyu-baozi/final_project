package edu.crm.infrastructure.customer;

import edu.crm.domain.customer.CustomerCenterGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class HttpCustomerCenterGateway implements CustomerCenterGateway {
    private static final Logger LOG = LoggerFactory.getLogger(HttpCustomerCenterGateway.class);
    private final RestClient client;
    public HttpCustomerCenterGateway(@Value("${customer-center.base-url:http://localhost:8081}") String baseUrl) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }
    @Override
    @Retryable(retryFor = RestClientException.class, maxAttempts = 4, backoff = @Backoff(delay = 1000, multiplier = 2))
    public CustomerData findByIdNumber(String idNumber) {
        CustomerData result = client.get().uri(builder -> builder.path("/customers/by-id-number").queryParam("idNumber", idNumber).build())
                .retrieve().body(CustomerData.class);
        if (result == null) throw new IllegalStateException("客户中心返回空数据");
        return result;
    }
    @Recover
    public CustomerData recover(RestClientException error, String idNumber) {
        LOG.error("客户中心调用失败，已重试三次", error);
        throw new IllegalStateException("客户中心调用失败", error);
    }
}
