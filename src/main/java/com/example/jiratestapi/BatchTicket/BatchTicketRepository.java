package com.example.jiratestapi.BatchTicket;
import com.example.jiratestapi.Task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchTicketRepository extends JpaRepository<BatchTicket, Long> {
    Optional<BatchTicket> findByJiraId(String jiraId);
    List<BatchTicket> findAllByJiraIdIn(List<String> jiraIds);

}
