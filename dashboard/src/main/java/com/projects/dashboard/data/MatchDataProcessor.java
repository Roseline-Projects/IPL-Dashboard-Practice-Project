package com.projects.dashboard.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.projects.dashboard.model.Match;

public class MatchDataProcessor implements ItemProcessor<MatchInput, Match> { //input type and output type of processor

    private static final Logger log = LoggerFactory.getLogger(MatchDataProcessor.class);

    //this is a Spring batch processor. It'll map the CSV file inputs to the output model for the database.
    //process() is called on each line in the CSV, which is mapped as a MatchInput
    @Override
    public Match process(final MatchInput matchInput) throws Exception {
         Match match = new Match(); //create a new match
         match.setId(Long.parseLong(matchInput.getId()));

         match.setCity(matchInput.getCity());
         match.setDate(LocalDate.parse(matchInput.getDate()));

         match.setPlayerOfMatch(matchInput.getPlayer_of_match());
         match.setVenue(matchInput.getVenue());

         //set team 1 and team 2 depending on inning order
         String firstInningTeam, secondInningTeam;

         if("bat".equals(matchInput.getToss_decision())) {
            firstInningTeam = matchInput.getToss_winner();
            secondInningTeam = matchInput.getToss_winner().equals(matchInput.getTeam1()) 
                ? matchInput.getTeam2() : matchInput.getTeam1();
         } else {
            secondInningTeam = matchInput.getToss_winner();
            firstInningTeam = matchInput.getToss_winner().equals(matchInput.getTeam1()) 
                ? matchInput.getTeam2() : matchInput.getTeam1();
         }

         match.setTeam1(firstInningTeam);
         match.setTeam2(secondInningTeam);

         match.setTossWinner(matchInput.getToss_winner());
         match.setTossDecision(matchInput.getToss_decision());
         match.setMatchWinner(matchInput.getWinner());
         match.setResult(matchInput.getResult());
         match.setResultMargin(matchInput.getResult_margin());
         match.setUmpire1(matchInput.getUmpire1());
         match.setUmpire2(matchInput.getUmpire2());

         System.out.println("Created match:" + match);

         return match;
    }
}