package com.example.jiratestapi.SyncAuth;

import com.example.jiratestapi.Jira.JiraService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/connection")
public class SyncAuthController {
    private SyncAuthService syncAuthService;

    @PostMapping("/set")
    public ResponseEntity<Boolean> Connect(@RequestBody SyncAuth authReq){
        System.out.println("start in auth:---->");
        try{
            Boolean isConnected= syncAuthService.checkIfConnected(authReq);
            if(isConnected){
                syncAuthService.create(authReq.getApiUrl(),authReq.getToken(),authReq.getEmail());
                return new ResponseEntity<>(true, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(false, HttpStatus.OK);

            }

        }catch (Exception e){ e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.NOT_MODIFIED);
        }
    }
    @GetMapping ("/verify")
    public ResponseEntity<VerifySyncResponse> VerifyConnect(){
        try {
            VerifySyncResponse response= syncAuthService.verifyIfConnected();
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(new VerifySyncResponse(false,null),HttpStatus.NO_CONTENT);

        }

    }
}
