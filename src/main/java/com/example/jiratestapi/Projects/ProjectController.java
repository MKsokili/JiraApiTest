package com.example.jiratestapi.Projects;

import com.example.jiratestapi.SyncAuth.SyncAuth;
import com.example.jiratestapi.Task.Task;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/projects")

public class ProjectController {
    ProjectRepository projectRepository;
    ProjectService projectService;

    @PostMapping("/{projectid}/jira-conf/{prjctkey}")
    public ResponseEntity<ResponseWithMsg> addProjectKey(@PathVariable Long projectid,@PathVariable String prjctkey) throws Exception {

        ResponseWithMsg response=projectService.addProjectKey(projectid,prjctkey);

        return new ResponseEntity(response,HttpStatus.OK);
    }
    @PostMapping("/{projectid}/reset-key")
    public ResponseEntity<Void> resetKey(@PathVariable Long projectid)  {
        try {
            Project project=projectRepository.findById(projectid).get();
            project.setJiraKey(null);
            project.setIsValid(false);
            projectRepository.save(project);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Indicate failure with 204 No Content
        }
    }
    @GetMapping("/{projectid}/jira-conf/get-key")
    public ResponseEntity<Response> getProjectKey(@PathVariable Long projectid) throws Exception {
        Response res=projectService.getJiraKey(projectid);
        return new ResponseEntity<>(res, HttpStatus.OK);

    }
    @GetMapping("get-all")
    public ResponseEntity<List<Project>> getProjects() throws Exception {
        List<Project> projects=projectRepository.findAll();
        return new ResponseEntity<>(projects, HttpStatus.OK);

    }
    @GetMapping("/{projectId}/tasks")
    public List<Task> getTasksByProjectId(@PathVariable Long projectId){
        Optional<Project> project= projectRepository.findById(projectId);
        List<Task> tasks;
        if (project.isPresent()){
            Project p=project.get();
            tasks=project.get().issues;
        }
        else{
            throw new RuntimeException("Projeect with this ID  don't exist");
        }
        return  tasks;
    }

    @GetMapping("/getfailedbatches")
    public List<String> getFailedBatches() {
        return projectService.getProjectNamesWithIncompleteBatches(); 
    }
}
