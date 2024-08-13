package com.example.jiratestapi.Projects;

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
@CrossOrigin(origins = "http://localhost:3000")

public class ProjectController {
    ProjectRepository projectRepository;
    ProjectService projectService;

    @PostMapping("/{projectid}/jira-conf/{prjctkey}")
    public ResponseEntity<Response> addProjectKey(@PathVariable Long projectid,@PathVariable String prjctkey) throws Exception {

        ResponseWithMsg response=projectService.addProjectKey(projectid,prjctkey);

        return new ResponseEntity(response,HttpStatus.OK);
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


}
