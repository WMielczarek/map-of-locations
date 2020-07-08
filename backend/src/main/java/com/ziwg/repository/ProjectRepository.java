package com.ziwg.repository;

import com.ziwg.model.db.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    Optional<Project> findByUserAndProjectName(String user, String projectName);
}
