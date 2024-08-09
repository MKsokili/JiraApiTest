package com.example.jiratestapi.Batch;

import com.example.jiratestapi.BatchTicket.BatchTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.example.jiratestapi.Projects.Project;


@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

        List<Batch> findByProject(Project project);

    Optional<Batch> findByProjectAndStartedDate(Project project, LocalDate batchDate);
}
