package com.gtuc.troskyMate;
/**
 * Created by guy on 09/03/17.
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication
@org.springframework.context.annotation.Configuration
@EnableAutoConfiguration



public class Application {

    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        config
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
                .setURI("https://app89205062-DQZv67:b.i3LyH7FqZV2T.OyCIVvSXTXvaNon6@hobby-cjkoaoiekhacgbkeoemilpal.dbs.graphenedb.com:24780");
        return config;
    }

    //Main function of troskyMate
    public static void main(String[] args) throws Exception{

        SpringApplication.run(Application.class, args);
    }
}
