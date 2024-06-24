package com.example.insuranceapp.controllers;

import com.example.insuranceapp.models.Application;
import com.example.insuranceapp.services.ApplicationService;
import com.example.insuranceapp.services.AwsS3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private AwsS3Service awsS3Service;

    @InjectMocks
    private ApplicationController applicationController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
    }

    @Test
    public void testCreateApplication() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());
        Application application = new Application();
        application.setId("123");
        String applicationJson = objectMapper.writeValueAsString(application);
        MockMultipartFile applicationPart = new MockMultipartFile("application", "application.json", MediaType.APPLICATION_JSON_VALUE, applicationJson.getBytes());

        when(awsS3Service.uploadFile(any(MultipartFile.class))).thenReturn("http://aws.s3/test.jpg");
        when(applicationService.createApplication(any(Application.class))).thenReturn(application);

        mockMvc.perform(multipart("/api/applications")
                        .file(file)
                        .file(applicationPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"));

        ArgumentCaptor<Application> applicationCaptor = ArgumentCaptor.forClass(Application.class);
        verify(applicationService).createApplication(applicationCaptor.capture());
        assertThat(applicationCaptor.getValue().getPhotoUrl()).isEqualTo("http://aws.s3/test.jpg");
    }

    @Test
    public void testUpdateApplication() throws Exception {
        Application application = new Application();
        application.setId("123");
        when(applicationService.updateApplication(eq("123"), any(Application.class))).thenReturn(Optional.of(application));

        mockMvc.perform(put("/api/applications/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"));

        verify(applicationService).updateApplication(eq("123"), any(Application.class));
    }

    @Test
    public void testDeleteApplication() throws Exception {
        when(applicationService.deleteApplication("123")).thenReturn(true);

        mockMvc.perform(delete("/api/applications/123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(applicationService).deleteApplication("123");
    }

    @Test
    public void testApproveApplication() throws Exception {
        Application application = new Application();
        application.setId("123");
        when(applicationService.approveApplication("123")).thenReturn(Optional.of(application));

        mockMvc.perform(post("/api/applications/123/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"));

        verify(applicationService).approveApplication("123");
    }

    @Test
    public void testRejectApplication() throws Exception {
        Application application = new Application();
        application.setId("123");
        when(applicationService.rejectApplication("123")).thenReturn(Optional.of(application));

        mockMvc.perform(post("/api/applications/123/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"));

        verify(applicationService).rejectApplication("123");
    }

    @Test
    public void testViewImage() throws Exception {
        when(awsS3Service.getPresignedUrl("test.jpg")).thenReturn("http://aws.s3/test.jpg");

        mockMvc.perform(get("/api/applications/view-image").param("fileName", "test.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().string("http://aws.s3/test.jpg"));

        verify(awsS3Service).getPresignedUrl("test.jpg");
    }

    @Test
    public void testDeleteImage() throws Exception {
        doNothing().when(awsS3Service).deleteFile("test.jpg");

        mockMvc.perform(delete("/api/applications/delete-image").param("fileName", "test.jpg"))
                .andExpect(status().isOk());

        verify(awsS3Service).deleteFile("test.jpg");
    }

}