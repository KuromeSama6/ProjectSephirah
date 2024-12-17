package moe.protasis.sephirah;

import moe.protasis.sephirah.data.cache.CachedEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync
public class ProjectSephirahBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectSephirahBackendApplication.class, args);
    }

}
