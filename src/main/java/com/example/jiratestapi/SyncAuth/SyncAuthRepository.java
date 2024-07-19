package com.example.jiratestapi.SyncAuth;

import com.example.jiratestapi.Tasks.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncAuthRepository extends JpaRepository<Task, Long> {
}
