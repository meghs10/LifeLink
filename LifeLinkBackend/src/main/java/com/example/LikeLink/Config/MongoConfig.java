package com.example.LikeLink.Config;


import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.example.LikeLink.Model.AmbulanceDriver;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.LikeLink.Repository")
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoDatabaseFactory mongoDbFactory;
    private MongoTemplate mongoTemplate;

    @Bean
    public MongoTemplate mongoTemplate() {
        if (mongoTemplate == null) {
            mongoTemplate = new MongoTemplate(mongoDbFactory);
        }
        return mongoTemplate;
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system");
    }
   
}