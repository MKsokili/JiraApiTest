package com.example.jiratestapi.history;

import com.example.jiratestapi.SyncAuth.SyncAuth;
import com.example.jiratestapi.SyncAuth.SyncAuthRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BatchHistoryService {
    private final BatchHistoryRepository batchHistoryRepository;
    private final SyncAuthRepository syncAuthRepository;

    public void recordHistory(String projectKey, String status, String message) {
        SyncAuth syncAuth = syncAuthRepository.findById(1L).orElseThrow(() -> new RuntimeException("SyncAuth not found"));
        BatchHistory history = new BatchHistory();
        history.setProjectKey(projectKey);
        history.setSyncTime(LocalDateTime.now());
        history.setStatus(status);
        history.setMessage(message);
        history.setSyncAuth(syncAuth);

        batchHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<BatchHistory> getBatchHistoriesByProjectKey(String projectKey) {
        return batchHistoryRepository.findByProjectKey(projectKey);
    }
}
