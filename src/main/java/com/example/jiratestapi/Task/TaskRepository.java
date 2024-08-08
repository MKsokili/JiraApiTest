package com.example.jiratestapi.Task;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByJiraId(String jiraId);
    List<Task> findAllByProjectId(Long projectId);
 }
