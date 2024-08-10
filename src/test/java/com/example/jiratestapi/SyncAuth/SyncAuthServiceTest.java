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
        // Arrange
        SyncAuth mockAuth = mock(SyncAuth.class);
        when(syncAuthRepository.findById(1L)).thenReturn(Optional.of(mockAuth));

        String url = "http://example.com";
        String token = "sampleToken";
        String email = "test@example.com";

        // Act
        syncAuthService.create(url, token, email);

        // Assert
        verify(mockAuth).setEmail(email);
        verify(mockAuth).setToken(token);
        verify(mockAuth).setApiUrl(url);
        verify(syncAuthRepository).save(mockAuth);
    }

    @Test
    void getSyncAuthInstant() {
        // Arrange
        SyncAuth mockAuth = mock(SyncAuth.class);
        when(syncAuthRepository.findById(1L)).thenReturn(Optional.of(mockAuth));

        // Act
        SyncAuth result = syncAuthService.getSyncAuthInstant();

        // Assert
        assertNotNull(result);
        assertEquals(mockAuth, result);
    }

    @Test
    void getSyncAuth_whenNotFound() {
        // Arrange
        when(syncAuthRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> syncAuthService.getSyncAuthInstant());
    }

    @Test
    void checkIfConnected_success() {
        // Arrange
        SyncAuth mockAuth = mock(SyncAuth.class);
        when(mockAuth.getEmail()).thenReturn("test@example.com");
        when(mockAuth.getToken()).thenReturn("sampleToken");
        when(mockAuth.getApiUrl()).thenReturn("http://example.com");

        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        Boolean result = syncAuthService.checkIfConnected(mockAuth);

        // Assert
        assertTrue(result);
    }

    @Test
    void checkIfConnected_failure() {
        // Arrange
        SyncAuth mockAuth = mock(SyncAuth.class);
        when(mockAuth.getEmail()).thenReturn("test@example.com");
        when(mockAuth.getToken()).thenReturn("sampleToken");
        when(mockAuth.getApiUrl()).thenReturn("http://example.com");

        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        Boolean result = syncAuthService.checkIfConnected(mockAuth);

        // Assert
        assertFalse(result);
    }

    @Test
    void checkIfConnected_exception() {
        // Arrange
        SyncAuth mockAuth = mock(SyncAuth.class);
        when(mockAuth.getEmail()).thenReturn("test@example.com");
        when(mockAuth.getToken()).thenReturn("sampleToken");
        when(mockAuth.getApiUrl()).thenReturn("http://example.com");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Connection error"));

        // Act
        Boolean result = syncAuthService.checkIfConnected(mockAuth);

        // Assert
        assertFalse(result);
    }

//    @Test
//    void verifyIfConnected() {
//        // Arrange
//        SyncAuth mockAuth = mock(SyncAuth.class);
//        when(syncAuthRepository.findById(1L)).thenReturn(Optional.of(mockAuth));
//
//        // Mock the RestTemplate exchange method
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Basic encodedAuth");
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<String> response = new ResponseEntity<>("body", HttpStatus.OK);
//        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(entity), eq(String.class)))
//                .thenReturn(response);
//
//        // Act
//        VerifySyncResponse responseEntity = syncAuthService.verifyIfConnected();
//
//        // Assert
//        assertNotNull(responseEntity);
//        assertTrue(responseEntity.getIsConnected());
//        assertEquals(Optional.of(mockAuth), responseEntity.getSyncAuth());
//    }
}