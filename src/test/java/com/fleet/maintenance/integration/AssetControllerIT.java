package com.fleet.maintenance.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleet.maintenance.domain.AssetStatus;
import com.fleet.maintenance.domain.AssetType;
import com.fleet.maintenance.dto.request.CreateAssetRequest;
import com.fleet.maintenance.dto.request.UpdateAssetRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AssetControllerIT extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAsset_returnsCreated() throws Exception {
        CreateAssetRequest req = CreateAssetRequest.builder()
                .serialNumber("SN-ASSET-001")
                .type(AssetType.VEHICLE)
                .location("Warehouse A")
                .operatingHours(100.0)
                .build();

        mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serialNumber").value("SN-ASSET-001"))
                .andExpect(jsonPath("$.status").value("OPERATIONAL"));
    }

    @Test
    void getById_returnsAsset() throws Exception {
        CreateAssetRequest req = CreateAssetRequest.builder()
                .serialNumber("SN-ASSET-002")
                .type(AssetType.CRANE)
                .location("Site B")
                .operatingHours(50.0)
                .build();

        String body = mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(get("/api/assets/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serialNumber").value("SN-ASSET-002"));
    }

    @Test
    void getAll_returnsArray() throws Exception {
        CreateAssetRequest req = CreateAssetRequest.builder()
                .serialNumber("SN-ASSET-003")
                .type(AssetType.PUMP)
                .location("Plant C")
                .operatingHours(200.0)
                .build();

        mockMvc.perform(post("/api/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void update_changesLocationAndHours() throws Exception {
        CreateAssetRequest create = CreateAssetRequest.builder()
                .serialNumber("SN-ASSET-004")
                .type(AssetType.GENERATOR)
                .location("Backup Room")
                .operatingHours(75.0)
                .build();

        String body = mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(body).get("id").asLong();

        UpdateAssetRequest update = UpdateAssetRequest.builder()
                .location("New Location")
                .operatingHours(150.0)
                .build();

        mockMvc.perform(put("/api/assets/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("New Location"))
                .andExpect(jsonPath("$.operatingHours").value(150.0));
    }

    @Test
    void delete_thenGetReturns404() throws Exception {
        CreateAssetRequest req = CreateAssetRequest.builder()
                .serialNumber("SN-ASSET-005")
                .type(AssetType.OTHER)
                .location("Storage")
                .operatingHours(0.0)
                .build();

        String body = mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(delete("/api/assets/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/assets/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createDuplicate_returns409() throws Exception {
        CreateAssetRequest req = CreateAssetRequest.builder()
                .serialNumber("SN-ASSET-DUP")
                .type(AssetType.VEHICLE)
                .location("Depot")
                .operatingHours(0.0)
                .build();

        mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void getByStatus_returnsFilteredList() throws Exception {
        mockMvc.perform(get("/api/assets/status/OPERATIONAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
