package com.example.jiratestapi.SyncAuth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SyncAuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SyncAuthService syncAuthService;

    @InjectMocks
    private SyncAuthController syncAuthController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(syncAuthController)
                .build();
    }
    @Test
    void testConnectSuccess() throws Exception {
        SyncAuth authReq = new SyncAuth();
        authReq.setApiUrl("http://example.com");
        authReq.setToken("sampleToken");
        authReq.setEmail("test@example.com");

        when(syncAuthService.checkIfConnected(any(SyncAuth.class))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/connection/set")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authReq)))
                .andExpect(status().isOk())
                .andExpect(content().string("true")); 
    }

    @Test
    void testConnectFailure() throws Exception {
        SyncAuth authReq = new SyncAuth();
        authReq.setApiUrl("http://example.com");
        authReq.setToken("sampleToken");
        authReq.setEmail("test@example.com");

        when(syncAuthService.checkIfConnected(any(SyncAuth.class))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/connection/set")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authReq)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testVerifyConnectSuccess() throws Exception {
        VerifySyncResponse response = new VerifySyncResponse(true,true, null);
        when(syncAuthService.verifyIfConnected()).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/connection/verify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isConnected").value(true));
    }

    @Test
    void testVerifyConnectFailure() throws Exception {
        when(syncAuthService.verifyIfConnected()).thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(MockMvcRequestBuilders.get("/connection/verify"))
                .andExpect(status().isNoContent());
    }
}
