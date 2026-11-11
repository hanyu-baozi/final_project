package edu.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.crm.domain.customer.CustomerCenterGateway;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class Customer360ControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean CustomerCenterGateway center;
    @Test
    void usesIdNumberAndReturnsMasterDataAndNotes() throws Exception {
        String body = mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idNumber\":\"110101199001010011\"}"))
                .andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(body).get("id").asLong();
        when(center.findByIdNumber("110101199001010011")).thenReturn(new CustomerCenterGateway.CustomerData(
                "110101199001010011", "主数据姓名", "ACTIVE", "VIP", new BigDecimal("500"), List.of()));
        String lead = mvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"关联线索\",\"customerId\":" + id + "}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long leadId = mapper.readTree(lead).get("id").asLong();
        String opportunity = mvc.perform(post("/api/leads/{id}/convert", leadId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"关联商机\",\"operator\":\"测试\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        long opportunityId = mapper.readTree(opportunity).get("id").asLong();
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"opportunityId\":" + opportunityId + ",\"amount\":100}"))
                .andExpect(status().isCreated());
        String other = mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idNumber\":\"ID-OTHER-CUSTOMER\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long otherId = mapper.readTree(other).get("id").asLong();
        mvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"其他客户线索\",\"customerId\":" + otherId + "}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/customers/{id}/360", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.master.name").value("主数据姓名"))
                .andExpect(jsonPath("$.master.availableBalance").value(500))
                .andExpect(jsonPath("$.notes.totalElements").value(0))
                .andExpect(jsonPath("$.leads[0].id").value(leadId))
                .andExpect(jsonPath("$.leads.length()").value(1))
                .andExpect(jsonPath("$.opportunities[0].id").value(opportunityId))
                .andExpect(jsonPath("$.orders[0].amount").value(100));
        verify(center).findByIdNumber("110101199001010011");
    }
    @Test
    void rejectsMasterDataForDifferentIdentity() throws Exception {
        String body = mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idNumber\":\"ID-MISMATCH-TEST\"}"))
                .andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(body).get("id").asLong();
        when(center.findByIdNumber("ID-MISMATCH-TEST")).thenReturn(new CustomerCenterGateway.CustomerData(
                "OTHER", "错误客户", "ACTIVE", "VIP", BigDecimal.ZERO, List.of()));
        mvc.perform(get("/api/customers/{id}/360", id)).andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("客户中心返回的身份证号不匹配"));
    }
}
