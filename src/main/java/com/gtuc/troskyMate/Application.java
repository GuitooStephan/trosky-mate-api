package com.gtuc.troskyMate;
/**
 * Created by guy on 09/03/17.
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@org.springframework.context.annotation.Configuration
@EnableAutoConfiguration
@EnableCaching
@EnableSwagger2
public class Application {

    @Bean
    public Docket troskyMateApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.gtuc.troskyMate.controllers")).build();
    }

    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration("ogm.properties");
        return configuration;
    }

    //Main function of troskyMate
    public static void main(String[] args) throws Exception{

        SpringApplication.run(Application.class, args);
    }
}
