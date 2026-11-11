package edu.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class SalesControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Test
    void convertsOnceAndCreatesMultipleOrders() throws Exception {
        String lead = mvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"线索\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(lead).get("id").asLong();
        String body = "{\"title\":\"商机\",\"operator\":\"张三\"}";
        String opportunity = mvc.perform(post("/api/leads/{id}/convert", id).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        long oppId = mapper.readTree(opportunity).get("id").asLong();
        mvc.perform(get("/api/leads/{id}", id)).andExpect(jsonPath("$.status").value("CONVERTED"))
                .andExpect(jsonPath("$.convertedBy").value("张三")).andExpect(jsonPath("$.convertedAt").exists());
        String order = "{\"opportunityId\":" + oppId + ",\"amount\":100.00}";
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(order)).andExpect(status().isCreated());
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(order)).andExpect(status().isCreated());
        mvc.perform(post("/api/leads/{id}/convert", id).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
    @Test
    void invalidOpportunityDoesNotConvertLead() throws Exception {
        String lead = mvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"待转换\"}"))
                .andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(lead).get("id").asLong();
        mvc.perform(post("/api/leads/{id}/convert", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"operator\":\"张三\"}")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/leads/{id}", id)).andExpect(jsonPath("$.status").value("NEW"));
    }
    @Test
    void listsLeadsWithPagination() throws Exception {
        mvc.perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"看板线索\"}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/leads").param("page", "0").param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.content[0].id").isNumber());
    }
}
