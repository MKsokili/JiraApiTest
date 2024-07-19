package com.example.jiratestapi.Batch;

import com.example.jiratestapi.Tasks.Issue;
import com.example.jiratestapi.Tasks.Task;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date start_date;
    private Boolean  isCompleted;
    //add errors fields and statistiques and so
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Task> tasks ;

}
