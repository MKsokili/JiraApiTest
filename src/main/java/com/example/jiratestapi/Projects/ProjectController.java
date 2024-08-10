package com.example.jiratestapi.Projects;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/project/{projectid}/jira-conf/")
public class ProjectController {
    ProjectRepository projectRepository;
    ProjectService projectService;

    @PostMapping("{prjctkey}")
    public ResponseEntity<Response> addProjectKey(@PathVariable Long projectid,@PathVariable String prjctkey) throws Exception {

        ResponseWithMsg response=projectService.addProjectKey(projectid,prjctkey);

        return new ResponseEntity(response,HttpStatus.OK);
    }
    @GetMapping("get-key")
    public ResponseEntity<Response> getProjectKey(@PathVariable Long projectid) throws Exception {
        Response res=projectService.getJiraKey(projectid);
        return new ResponseEntity<>(res, HttpStatus.OK);

    }


}
