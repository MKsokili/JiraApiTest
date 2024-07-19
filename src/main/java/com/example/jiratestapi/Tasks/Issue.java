package com.example.jiratestapi.Tasks;

import com.example.jiratestapi.Batch.Batch;
import jakarta.persistence.*;

import java.util.Date;

public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private ActionType action_type;
    private Date action_date;
    @ManyToOne
    @JoinColumn(name = "batch_id")
    private Batch batch  ;
}
