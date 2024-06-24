package com.example.insuranceapp.controllers;

import com.example.insuranceapp.models.Application;
import com.example.insuranceapp.services.ApplicationService;
import com.example.insuranceapp.services.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private AwsS3Service awsS3Service;

  @PostMapping
  public Application createApplication(@RequestPart("application") Application application, @RequestPart("file") MultipartFile file) {
      String fileUrl = awsS3Service.uploadFile(file);
      application.setPhotoUrl(fileUrl);
      return applicationService.createApplication(application);
  }

  @PutMapping("/{id}")
  public Optional<Application> updateApplication(@PathVariable String id, @RequestBody Application application) {
      return applicationService.updateApplication(id, application);
  }

  @DeleteMapping("/{id}")
  public boolean deleteApplication(@PathVariable String id) {
      return applicationService.deleteApplication(id);
  }

  @PostMapping("/{id}/approve")
  public Optional<Application> approveApplication(@PathVariable String id) {
      return applicationService.approveApplication(id);
  }

  @PostMapping("/{id}/reject")
  public Optional<Application> rejectApplication(@PathVariable String id) {
      return applicationService.rejectApplication(id);
  }

  @GetMapping("/view-image")
  public String viewImage(@RequestParam String fileName) {
      return awsS3Service.getPresignedUrl(fileName);
  }

  @DeleteMapping("/delete-image")
  public void deleteImage(@RequestParam String fileName) {
      awsS3Service.deleteFile(fileName);
  }
  
}
