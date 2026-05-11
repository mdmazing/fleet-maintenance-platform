package com.fleet.maintenance.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleet.maintenance.domain.AssetType;
import com.fleet.maintenance.domain.FaultSeverity;
import com.fleet.maintenance.dto.request.CreateAssetRequest;
import com.fleet.maintenance.dto.request.CreateFaultLogRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FaultLogControllerIT extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    private Long createAsset(String serial) throws Exception {
        CreateAssetRequest req = CreateAssetRequest.builder()
                .serialNumber(serial)
                .type(AssetType.VEHICLE)
                .location("Fleet HQ")
                .operatingHours(100.0)
                .build();
        String body = mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    @Test
    void createFaultLog_returnsCreated() throws Exception {
        Long assetId = createAsset("SN-FL-001");

        CreateFaultLogRequest req = CreateFaultLogRequest.builder()
                .assetId(assetId)
                .reportedBy("John Doe")
                .severity(FaultSeverity.MEDIUM)
                .description("Engine overheating")
                .build();

        mockMvc.perform(post("/api/fault-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.severity").value("MEDIUM"))
                .andExpect(jsonPath("$.resolved").value(false));
    }

    @Test
    void criticalFault_autoCreatesMaintenanceOrder() throws Exception {
        Long assetId = createAsset("SN-FL-002");

        CreateFaultLogRequest req = CreateFaultLogRequest.builder()
                .assetId(assetId)
                .reportedBy("Jane Smith")
                .severity(FaultSeverity.CRITICAL)
                .description("Hydraulic failure")
                .build();

        mockMvc.perform(post("/api/fault-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        // an open order must now exist for this asset
        mockMvc.perform(get("/api/maintenance-orders/asset/" + assetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].triggerReason").value("FAULT_REPORTED"));
    }

    @Test
    void resolve_marksResolved() throws Exception {
        Long assetId = createAsset("SN-FL-003");

        CreateFaultLogRequest req = CreateFaultLogRequest.builder()
                .assetId(assetId)
                .reportedBy("Operator")
                .severity(FaultSeverity.LOW)
                .description("Minor oil leak")
                .build();

        String body = mockMvc.perform(post("/api/fault-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long faultId = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(put("/api/fault-logs/" + faultId + "/resolve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolved").value(true))
                .andExpect(jsonPath("$.resolvedAt").isNotEmpty());
    }

    @Test
    void getUnresolved_returnsList() throws Exception {
        mockMvc.perform(get("/api/fault-logs/unresolved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
