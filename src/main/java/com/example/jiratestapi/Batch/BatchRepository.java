package com.example.jiratestapi.Batch;

import com.example.jiratestapi.BatchTicket.BatchTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    @Query("SELECT b FROM Batch b ORDER BY b.startedDate DESC")
    List<Batch> findAllBatchesOrderedByStartedDateDesc();
}
