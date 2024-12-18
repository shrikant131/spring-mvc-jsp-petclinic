package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthCheckController.class)
public class HealthCheckControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClinicService clinicService;

    @BeforeEach
    public void setup() {
        HealthCheckController healthCheckController = new HealthCheckController(clinicService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(healthCheckController).build();
    }

    @Test
    public void testHealthCheck() throws Exception {
        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("database", "UP");
        healthStatus.put("externalAPIs", "UP");
        healthStatus.put("otherComponents", "UP");

        given(clinicService.findOwnerById(1)).willReturn(new Owner());

        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database").value("UP"))
                .andExpect(jsonPath("$.externalAPIs").value("UP"))
                .andExpect(jsonPath("$.otherComponents").value("UP"));
    }
}
