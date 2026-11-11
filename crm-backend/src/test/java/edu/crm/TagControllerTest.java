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
class TagControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Test
    void filtersByAllOrAnyAndSoftDeletes() throws Exception {
        long customer = createId("/api/customers", "{\"idNumber\":\"ID-TAG-TEST\"}");
        long a = createId("/api/tags", "{\"name\":\"A标签\"}");
        long b = createId("/api/tags", "{\"name\":\"B标签\"}");
        mvc.perform(post("/api/tags/{id}/customers/{customer}", a, customer)).andExpect(status().isOk());
        mvc.perform(get("/api/tags/customers").param("ids", a + "," + b).param("all", "false"))
                .andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get("/api/tags/customers").param("ids", a + "," + b).param("all", "true"))
                .andExpect(jsonPath("$.totalElements").value(0));
        long second = createId("/api/customers", "{\"idNumber\":\"ID-TAG-SECOND\"}");
        mvc.perform(post("/api/tags/{id}/customers/{customer}", a, second)).andExpect(status().isOk());
        mvc.perform(get("/api/tags/customers").param("ids", Long.toString(a)).param("size", "1").param("page", "1"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.numberOfElements").value(1));
        mvc.perform(delete("/api/tags/{id}", a)).andExpect(status().isOk());
        mvc.perform(get("/api/tags/customers").param("ids", Long.toString(a)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
    @Test
    void rejectsAttachToMissingCustomer() throws Exception {
        long tag = createId("/api/tags", "{\"name\":\"异常标签\"}");
        mvc.perform(post("/api/tags/{id}/customers/{customer}", tag, 999999)).andExpect(status().isBadRequest());
    }
    private long createId(String uri, String json) throws Exception {
        String body = mvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(json))
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("id").asLong();
    }
}
