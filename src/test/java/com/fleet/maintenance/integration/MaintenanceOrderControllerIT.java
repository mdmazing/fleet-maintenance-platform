package com.fleet.maintenance.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleet.maintenance.domain.AssetType;
import com.fleet.maintenance.domain.OrderStatus;
import com.fleet.maintenance.domain.TriggerReason;
import com.fleet.maintenance.dto.request.CreateAssetRequest;
import com.fleet.maintenance.dto.request.CreateMaintenanceOrderRequest;
import com.fleet.maintenance.dto.request.UpdateOrderStatusRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MaintenanceOrderControllerIT extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    private Long createAsset(String serial) throws Exception {
        CreateAssetRequest req = CreateAssetRequest.builder()
                .serialNumber(serial)
                .type(AssetType.VEHICLE)
                .location("Depot")
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
    void createOrder_returnsPending() throws Exception {
        Long assetId = createAsset("SN-MO-001");

        CreateMaintenanceOrderRequest req = CreateMaintenanceOrderRequest.builder()
                .assetId(assetId)
                .triggerReason(TriggerReason.MANUAL)
                .description("Scheduled maintenance")
                .scheduledDate(LocalDate.now().plusDays(7))
                .build();

        mockMvc.perform(post("/api/maintenance-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.triggerReason").value("MANUAL"));
    }

    @Test
    void updateStatus_toInProgress() throws Exception {
        Long assetId = createAsset("SN-MO-002");

        CreateMaintenanceOrderRequest create = CreateMaintenanceOrderRequest.builder()
                .assetId(assetId)
                .triggerReason(TriggerReason.MANUAL)
                .description("Test order")
                .scheduledDate(LocalDate.now().plusDays(3))
                .build();

        String body = mockMvc.perform(post("/api/maintenance-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(body).get("id").asLong();

        UpdateOrderStatusRequest update = UpdateOrderStatusRequest.builder()
                .status(OrderStatus.IN_PROGRESS)
                .notes("Work started")
                .build();

        mockMvc.perform(put("/api/maintenance-orders/" + orderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void completeOrder_setsAssetOperational() throws Exception {
        Long assetId = createAsset("SN-MO-003");

        CreateMaintenanceOrderRequest create = CreateMaintenanceOrderRequest.builder()
                .assetId(assetId)
                .triggerReason(TriggerReason.MANUAL)
                .description("Full service")
                .scheduledDate(LocalDate.now())
                .build();

        String body = mockMvc.perform(post("/api/maintenance-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(body).get("id").asLong();

        UpdateOrderStatusRequest complete = UpdateOrderStatusRequest.builder()
                .status(OrderStatus.COMPLETED)
                .notes("All done")
                .build();

        mockMvc.perform(put("/api/maintenance-orders/" + orderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(complete)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.completedDate").isNotEmpty());

        // asset should be OPERATIONAL again
        mockMvc.perform(get("/api/assets/" + assetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPERATIONAL"));
    }

    @Test
    void getByStatus_returnsList() throws Exception {
        mockMvc.perform(get("/api/maintenance-orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
