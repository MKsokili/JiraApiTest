package com.example.jiratestapi.SyncAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncAuthRepository extends JpaRepository<SyncAuth, Long> {

}
