package com.projects.dashboard.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchInput { //used to parse the input coming from the csv file
    //this isn't really the model - this class is formatted 
    //based on the csv file

    //technically, this is a business class
    //it preceeds the processor

    private String id;
    private String city;
    private String date;
    private String player_of_match;
    private String venue;
    private String neutral_venue;
    private String team1;
    private String team2;
    private String toss_winner;
    private String toss_decision;
    private String winner;
    private String result;
    private String result_margin;
    private String eliminator;
    private String method;
    private String umpire1;
    private String umpire2;
    
}
