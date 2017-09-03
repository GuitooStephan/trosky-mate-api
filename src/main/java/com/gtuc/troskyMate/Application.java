package com.gtuc.troskyMate;
/**
 * Created by guy on 09/03/17.
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration

public class Application {

    //Main function of troskyMate
    public static void main(String[] args) throws Exception{

        SpringApplication.run(Application.class, args);
    }
}
