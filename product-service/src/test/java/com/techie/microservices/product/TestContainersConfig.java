package com.techie.microservices.product;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration
public class TestContainersConfig {

    @Bean
    public MongoDBContainer mongoDBContainer() {
        MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2")
                .withEnv("MONGO_INITDB_ROOT_USERNAME", "root")
                .withEnv("MONGO_INITDB_ROOT_PASSWORD", "password");
        mongoDBContainer.start();
        return mongoDBContainer;
    }
}
