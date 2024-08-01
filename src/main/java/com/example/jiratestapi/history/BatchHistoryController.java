package com.example.jiratestapi.history;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/batch-history")
@AllArgsConstructor
public class BatchHistoryController {
    private final BatchHistoryService batchHistoryService;

    @GetMapping("/project/{projectKey}")
    public List<BatchHistory> getBatchHistoriesByProjectKey(@PathVariable String projectKey) {
        return batchHistoryService.getBatchHistoriesByProjectKey(projectKey);
    }
}
