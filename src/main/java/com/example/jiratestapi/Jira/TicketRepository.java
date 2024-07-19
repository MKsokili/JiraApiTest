package com.example.jiratestapi.Jira;
import com.example.jiratestapi.Tasks.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByJiraId(String jiraId);
    List<Task> findAllByJiraIdIn(List<String> jiraIds);

}
