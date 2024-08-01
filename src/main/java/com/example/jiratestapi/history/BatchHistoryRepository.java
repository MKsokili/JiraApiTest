package com.example.jiratestapi.history;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchHistoryRepository extends JpaRepository<BatchHistory, Long> {
    List<BatchHistory> findByProjectKey(String projectKey);
}

