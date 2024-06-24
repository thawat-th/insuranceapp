package com.example.insuranceapp.services;

import com.example.insuranceapp.models.Application;
import com.example.insuranceapp.repositories.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository repository;

    @InjectMocks
    private ApplicationService applicationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateApplication() {
        Application application = new Application();
        application.setId("123");
        when(repository.save(any(Application.class))).thenReturn(application);

        Application result = applicationService.createApplication(application);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("pending");
        verify(repository, times(1)).save(application);
    }

    @Test
    public void testUpdateApplication() {
        Application application = new Application();
        application.setId("123");
        when(repository.findById("123")).thenReturn(Optional.of(application));
        when(repository.save(any(Application.class))).thenReturn(application);

        Optional<Application> result = applicationService.updateApplication("123", application);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("123");
        verify(repository, times(1)).findById("123");
        verify(repository, times(1)).save(application);
    }

    @Test
    public void testUpdateApplicationNotFound() {
        Application application = new Application();
        application.setId("123");
        when(repository.findById("123")).thenReturn(Optional.empty());

        Optional<Application> result = applicationService.updateApplication("123", application);

        assertThat(result).isNotPresent();
        verify(repository, times(1)).findById("123");
        verify(repository, never()).save(any(Application.class));
    }

    @Test
    public void testDeleteApplication() {
        when(repository.existsById("123")).thenReturn(true);

        boolean result = applicationService.deleteApplication("123");

        assertThat(result).isTrue();
        verify(repository, times(1)).existsById("123");
        verify(repository, times(1)).deleteById("123");
    }

    @Test
    public void testDeleteApplicationNotFound() {
        when(repository.existsById("123")).thenReturn(false);

        boolean result = applicationService.deleteApplication("123");

        assertThat(result).isFalse();
        verify(repository, times(1)).existsById("123");
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    public void testApproveApplication() {
        Application application = new Application();
        application.setId("123");
        when(repository.findById("123")).thenReturn(Optional.of(application));
        when(repository.save(any(Application.class))).thenReturn(application);

        Optional<Application> result = applicationService.approveApplication("123");

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo("approved");
        verify(repository, times(1)).findById("123");
        verify(repository, times(1)).save(application);
    }

    @Test
    public void testApproveApplicationNotFound() {
        when(repository.findById("123")).thenReturn(Optional.empty());

        Optional<Application> result = applicationService.approveApplication("123");

        assertThat(result).isNotPresent();
        verify(repository, times(1)).findById("123");
        verify(repository, never()).save(any(Application.class));
    }

    @Test
    public void testRejectApplication() {
        Application application = new Application();
        application.setId("123");
        when(repository.findById("123")).thenReturn(Optional.of(application));
        when(repository.save(any(Application.class))).thenReturn(application);

        Optional<Application> result = applicationService.rejectApplication("123");

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo("rejected");
        verify(repository, times(1)).findById("123");
        verify(repository, times(1)).save(application);
    }

    @Test
    public void testRejectApplicationNotFound() {
        when(repository.findById("123")).thenReturn(Optional.empty());

        Optional<Application> result = applicationService.rejectApplication("123");

        assertThat(result).isNotPresent();
        verify(repository, times(1)).findById("123");
        verify(repository, never()).save(any(Application.class));
    }
}
