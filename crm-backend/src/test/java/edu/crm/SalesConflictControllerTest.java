package edu.crm;

import edu.crm.adapter.sales.SalesController;
import edu.crm.app.sales.SalesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SalesController.class)
class SalesConflictControllerTest {
    @Autowired MockMvc mvc;
    @MockBean SalesService sales;
    @Test
    void concurrentConstraintFailureReturnsConflict() throws Exception {
        when(sales.convert(anyLong(), anyString(), anyString()))
                .thenThrow(new DataIntegrityViolationException("constraint"));
        mvc.perform(post("/api/leads/1/convert").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"opportunity\",\"operator\":\"demo\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("数据已发生变化，请刷新后重试"));
    }
}
