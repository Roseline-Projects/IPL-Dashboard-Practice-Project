package com.projects.dashboard.data;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.projects.dashboard.model.Match;

@Configuration
public class BatchConfig { //executes a batch job

    private final String[] FIELD_NAMES = new String[] {
        "id", "city", "date", "player_of_match", "venue", "neutral_venue", "team1" ,"team2", "toss_winner",
        "toss_decision", "winner", "result", "result_margin", "eliminator", "method", "umpire1", "umpire2"
    };

    // @Autowired
    // public JobBuilder jobBuilderFactory;

    // @Autowired
    // public StepBuilder stepBuilderFactory;

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    
    @Bean
    public FlatFileItemReader<MatchInput> reader() { //reader - reads from our csv file and outputs MatchInput readers
        System.out.println("Hello!!!!");
        // return new FlatFileItemReaderBuilder<MatchInput>()
        //     .name("MatchItemReader")
        //     .resource(new ClassPathResource("match-data.csv"))
        //     .delimited()
        //     .names(FIELD_NAMES)
        //     .fieldSetMapper(new BeanWrapperFieldSetMapper<MatchInput>() {
        //         {
        //             setTargetType(MatchInput.class);
        //         }
        //     }).build();
        return new FlatFileItemReaderBuilder<MatchInput>()
            .name("matchItemReader")
            .resource(new ClassPathResource("match-data.csv"))
            .delimited()
            .names(FIELD_NAMES)
            .targetType(MatchInput.class)
            .build();
    }

    @Bean 
    public MatchDataProcessor processor() { //processor to process the data
        return new MatchDataProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Match> writer (DataSource dataSource) { //writer - writes value to db
        // return new JdbcBatchItemWriterBuilder<Match>()
        //     .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        //     .sql("INSERT INTO match (id, city, date, player_of_match, venue, team1, team2, toss_winner, toss_decision, match_winner, result, result_margin, umpire1, umpire2) " 
        //     + "VALUES (:id, :city, :date:, :playerOfMatch, :venue, :team1, :team2, :tossWinner, :tossDecision, :matchWinner, :result, :resultMargin, :umpire1, :umpire2) ").dataSource(dataSource)
        //     .build();
        return new JdbcBatchItemWriterBuilder<Match>()
            .sql("INSERT INTO match (id, city, date, player_of_match, venue, team1, team2, toss_winner, toss_decision, match_winner, result, result_margin, umpire1, umpire2) " 
            + "VALUES (:id, :city, :date, :playerOfMatch, :venue, :team1, :team2, :tossWinner, :tossDecision, :matchWinner, :result, :resultMargin, :umpire1, :umpire2) ")
            .dataSource(dataSource)
            .beanMapped()
            .build();
    }

    @Bean
    public Job importUserJob(JobRepository repo, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", repo)
            .listener(listener)
            .start(step1)
            .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Match> writer, 
        DataSourceTransactionManager transactionManager,
        FlatFileItemReader<MatchInput> reader, 
        MatchDataProcessor processor,
        JobRepository jobRepository) {
        return new StepBuilder("step1", jobRepository)
            .<MatchInput, Match> chunk(3, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }
}