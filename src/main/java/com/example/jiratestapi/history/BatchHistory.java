package com.example.jiratestapi.history;

import com.example.jiratestapi.SyncAuth.SyncAuth;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class BatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String projectKey;
    private LocalDateTime syncTime;
    private String status;
    private String message;

    @ManyToOne
    @JoinColumn(name = "sync_auth_id")
    @JsonBackReference
    private SyncAuth syncAuth;
}
