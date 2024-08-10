package com.example.jiratestapi.users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User , Long> {
    User findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);    
}
