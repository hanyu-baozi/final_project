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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Test
    void createsCustomerInDatabase() throws Exception {
        String body = mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content("{\"idNumber\":\"ID-CUSTOMER-TEST\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.idNumber").value("ID-CUSTOMER-TEST"))
                .andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(body).get("id").asLong();
        mvc.perform(get("/api/customers/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.idNumber").value("ID-CUSTOMER-TEST"));
    }
    @Test
    void rejectsBlankName() throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content("{\"idNumber\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
