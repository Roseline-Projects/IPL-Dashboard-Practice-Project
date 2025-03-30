package com.projects.dashboard.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.projects.dashboard.model.Match;
import com.projects.dashboard.model.Team;
import com.projects.dashboard.repository.TeamRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    //private final JdbcTemplate jdbcTemplate;
    //@PersistenceContext
    // @Autowired
    private final EntityManager em;

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    public JobCompletionNotificationListener(EntityManager em) {
        //this.jdbcTemplate = jdbcTemplate;
        this.em = em;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
      if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
        log.info("!!! JOB FINISHED! Time to verify the results");

        // jdbcTemplate
        //     .query("SELECT team1, team2, date FROM match", new DataClassRowMapper<>(Match.class))
        //     .forEach(match -> log.info("Found <{}> in the database.", match));
            
        Map<String, Team> teamData = new HashMap<>();
        em.createQuery("select m.team1, count(*) from Match m group by m.team1", Object[].class)
          .getResultList() //array of objects
          .stream()
          .map(e -> new Team( (String) e[0], (long) e[1]))
          .forEach(team -> teamData.put(team.getTeamName(), team));

        em.createQuery("select m.team2, count(*) from Match m group by m.team2", Object[].class)
          .getResultList()
          .stream()
          .forEach(e -> {
            Team team = teamData.get((String) e[0]);
            team.setTotalMatches(team.getTotalMatches() + (long) e[1]);
          });

          em.createQuery("select m.matchWinner, count(*) from Match m group by m.matchWinner", Object[].class)
            .getResultList()
            .stream()
            .forEach(e -> {
              Team team = teamData.get((String) e[0]);
              if(team != null) team.setTotalWins((long) e[1]);
              System.out.println("persisted team:" + (String) e[0]);
            });

            //bug: items properly loaded into em database, but not accessible to repository otherwise
            teamData.values().forEach(team -> em.persist(team));
            //teamData.values().forEach(team -> teamRepository.save(team));
            //teamData.values().forEach(team -> System.out.println(em.contains(team)));
            ///teamData.values().forEach(team -> System.out.println(team));
            //em.flush();
            // List<Team> teams = em.createQuery("SELECT t FROM Team t", Team.class).getResultList();
            // log.info("Teams from database: " + teams);
      }
  }
}
