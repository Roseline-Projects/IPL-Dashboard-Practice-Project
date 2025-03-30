package com.projects.dashboard.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.projects.dashboard.model.Match;
import com.projects.dashboard.model.Team;

import jakarta.transaction.Transactional;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {

    Team findByTeamName(String teamName); //derived query to find team
    
}
