package org.example.analysisservice;

import org.example.analysisservice.infrastracture.MessageRelay;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;


@SpringBootApplication
public class AnalysisServiceApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("./document-analysis-service") // Wpisz tu nazwę folderu Twojego modułu
                .ignoreIfMissing()
                .load();
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
        SpringApplication.run(AnalysisServiceApplication.class, args);

        System.out.println(">>> ANALYSIS SERVICE IS READY <<<");
    }
}