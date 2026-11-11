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
class CustomerNoteControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Test
    void createsNoteUpdatesFollowUpAndFiltersByCustomer() throws Exception {
        String customer = mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content("{\"idNumber\":\"ID-NOTE-TEST\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(customer).get("id").asLong();
        String note = "{\"customerId\":" + id + ",\"content\":\"已联系\",\"category\":\"CALL\"}";
        mvc.perform(post("/api/notes").contentType(MediaType.APPLICATION_JSON).content(note)).andExpect(status().isOk());
        mvc.perform(get("/api/customers/{id}", id)).andExpect(jsonPath("$.lastFollowUp").exists());
        mvc.perform(get("/api/notes").param("customerId", Long.toString(id))).andExpect(jsonPath("$.totalElements").value(1));
    }
    @Test
    void rejectsMissingCustomer() throws Exception {
        mvc.perform(post("/api/notes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":999999,\"content\":\"记录\",\"category\":\"CALL\"}"))
                .andExpect(status().isBadRequest());
    }
}
