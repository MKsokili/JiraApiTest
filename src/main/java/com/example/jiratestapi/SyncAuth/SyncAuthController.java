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
    private SyncAuthRepository syncAuthRepository;

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
            SyncAuth sync=syncAuthService.getSyncAuthInstant();
            VerifySyncResponse response= syncAuthService.verifyIfConnected();
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(new VerifySyncResponse(false,false,null),HttpStatus.NO_CONTENT);

        }

    }
    @PostMapping("/activate")
    public ResponseEntity<Void> activateSync() {
        try {
            SyncAuth sync = syncAuthService.getSyncAuthInstant();
            sync.setIsStopped(false);
            syncAuthRepository.save(sync);
            return new ResponseEntity<>(HttpStatus.OK); // Indicate success with 200 OK
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Indicate failure with 204 No Content
        }
    }
    @PostMapping("/stop")
    public ResponseEntity<Void> stopSync() {
        try {
            SyncAuth sync = syncAuthService.getSyncAuthInstant();
            sync.setIsStopped(true);
            syncAuthRepository.save(sync);
            return new ResponseEntity<>(HttpStatus.OK); // Indicate success with 200 OK
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Indicate failure with 204 No Content
        }
    }
}
