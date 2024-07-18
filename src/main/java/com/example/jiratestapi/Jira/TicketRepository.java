package com.example.jiratestapi.Jira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByJiraId(String jiraId);
    List<Ticket> findAllByJiraIdIn(List<String> jiraIds);

}
