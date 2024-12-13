package moe.protasis.sephirah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ProjectSephirahBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectSephirahBackendApplication.class, args);
    }

}
