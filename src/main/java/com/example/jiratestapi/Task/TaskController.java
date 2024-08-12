package com.example.jiratestapi.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;
    @GetMapping("/{projectId}")
    public List<Task> getTasksByProjectId(@PathVariable Long projectId){
        return  taskRepository.findAllByProjectId(projectId);
    }

}
