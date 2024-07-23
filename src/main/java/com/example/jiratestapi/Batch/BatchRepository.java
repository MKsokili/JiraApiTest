package com.example.jiratestapi.Batch;

import com.example.jiratestapi.BatchTicket.BatchTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {


}
