package com.example.jiratestapi.users;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jiratestapi.Task.Task;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/mytasks/{userId}")
    public List<Task> getMyTickets(@PathVariable Long userId) {
        Optional<User> optionaluser = userRepository.findById(userId);
        if (optionaluser.isPresent()) {
            User user = optionaluser.get();
            List<Task> tasks = user.getTasks();
            return tasks;
        }else{
            return null;
        }
    }
    
    
}
