package com.example.jiratestapi.Projects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ProjectRepository extends JpaRepository<Project , Long>{
    Project findByJira_key(String jira_key);
}
