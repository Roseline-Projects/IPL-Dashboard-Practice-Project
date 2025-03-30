package com.projects.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.projects.dashboard.model.Team;
import com.projects.dashboard.repository.MatchRepository;
import com.projects.dashboard.repository.TeamRepository;

@RestController
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    // public TeamController(TeamRepository teamRepository, MatchRepository matchRepository) { //essentially autowiring
    //     this.teamRepository = teamRepository;
    //     this.matchRepository = matchRepository;
    // }

    @GetMapping("/team/{teamName}") //get mapping to get some team
    public Team getTeam(@PathVariable String teamName) { //teamName comes from the URL
        Team team = this.teamRepository.findByTeamName(teamName);
        team.setMatches(this.matchRepository.findLatestMatchesByTeam(teamName, 4));
        return team;
    }
}
