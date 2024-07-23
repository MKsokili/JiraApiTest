package com.example.jiratestapi.SyncAuth;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SyncAuthService {
    private SyncAuthRepository syncAuthRepository;

    public void create(String url,String token,String email) {
        SyncAuth auth = getSyncAuth();
        auth.setEmail(email);
        auth.setToken(token);
        auth.setApi_url(url+"/rest/api/2");
        syncAuthRepository.save(auth);
    }

    public SyncAuth getSyncAuth() {
        Optional<SyncAuth> list =syncAuthRepository.findById(1L);
        return list.get();
    }
}
