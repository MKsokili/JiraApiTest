package com.example.jiratestapi;

import com.example.jiratestapi.SyncAuth.SyncAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
class JiratestapiApplicationTests {
	@Autowired
	private SyncAuthService syncAuthService;

	@Test
	void contextLoads() {
		// Verifies that the SyncAuthService bean is loaded correctly
		assertNotNull(syncAuthService, "SyncAuthService should be loaded");
	}
}