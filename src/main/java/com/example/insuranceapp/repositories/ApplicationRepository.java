package com.example.insuranceapp.repositories;

import com.example.insuranceapp.models.Application;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ApplicationRepository extends ElasticsearchRepository<Application, String> {
}