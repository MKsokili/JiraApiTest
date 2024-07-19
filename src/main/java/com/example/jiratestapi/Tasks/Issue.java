package com.example.jiratestapi.Tasks;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Projects.Project;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Entity
@Data
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private ActionType action_type;
    private Date action_date;
    @ManyToOne
    private Project project  ;
}
