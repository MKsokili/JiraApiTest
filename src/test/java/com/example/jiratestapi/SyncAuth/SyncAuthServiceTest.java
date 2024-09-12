package com.example.jiratestapi.SyncAuth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SyncAuthServiceTest {

    @Mock
    private SyncAuthRepository syncAuthRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SyncAuthService syncAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create() {
        
        SyncAuth mockAuth = mock(SyncAuth.class);
        when(syncAuthRepository.findById(1L)).thenReturn(Optional.of(mockAuth));

        String url = "http://example.com";
        String token = "sampleToken";
        String email = "test@example.com";

        syncAuthService.create(url, token, email);

        verify(mockAuth).setEmail(email);
        verify(mockAuth).setToken(token);
        verify(mockAuth).setApiUrl(url);
        verify(syncAuthRepository).save(mockAuth);
    }

    @Test
    void getSyncAuthInstant() {
        
        SyncAuth mockAuth = mock(SyncAuth.class);
        when(syncAuthRepository.findById(1L)).thenReturn(Optional.of(mockAuth));

        SyncAuth result = syncAuthService.getSyncAuthInstant();

        assertNotNull(result);
        assertEquals(mockAuth, result);
    }

    @Test
    void getSyncAuth_whenNotFound() {
        
        when(syncAuthRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> syncAuthService.getSyncAuthInstant());
    }

    @Test
    void checkIfConnected_success() {
        
        SyncAuth mockAuth = mock(SyncAuth.class);
        when(mockAuth.getEmail()).thenReturn("test@example.com");
        when(mockAuth.getToken()).thenReturn("sampleToken");
        when(mockAuth.getApiUrl()).thenReturn("http://example.com");

        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        Boolean result = syncAuthService.checkIfConnected(mockAuth);

        assertTrue(result);
    }

    @Test
    void checkIfConnected_failure() {
        
        SyncAuth mockAuth = mock(SyncAuth.class);
        when(mockAuth.getEmail()).thenReturn("test@example.com");
        when(mockAuth.getToken()).thenReturn("sampleToken");
        when(mockAuth.getApiUrl()).thenReturn("http://example.com");

        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        Boolean result = syncAuthService.checkIfConnected(mockAuth);

        assertFalse(result);
    }

    @Test
    void checkIfConnected_exception() {
        
        SyncAuth mockAuth = mock(SyncAuth.class);
        when(mockAuth.getEmail()).thenReturn("test@example.com");
        when(mockAuth.getToken()).thenReturn("sampleToken");
        when(mockAuth.getApiUrl()).thenReturn("http://example.com");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Connection error"));

        Boolean result = syncAuthService.checkIfConnected(mockAuth);

        assertFalse(result);
    }


}