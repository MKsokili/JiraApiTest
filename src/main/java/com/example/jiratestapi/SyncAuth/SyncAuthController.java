package com.example.jiratestapi.SyncAuth;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/connection")
public class SyncAuthController {
    private SyncAuthService syncAuthService;

    @PostMapping("/")
    public ResponseEntity<String> Connect(@RequestBody SyncAuth authReq){
        try{
            syncAuthService.create(authReq.getApi_url(),authReq.getToken(),authReq.getEmail());
            return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
        }catch (Exception e)
        { e.printStackTrace();
            return new ResponseEntity<>("Created Successfully", HttpStatus.BAD_REQUEST);
        }
    }
}
