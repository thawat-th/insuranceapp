
package com.example.insuranceapp.services;

import com.example.insuranceapp.models.Application;
import com.example.insuranceapp.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository repository;

    public Application createApplication(Application application) {
        application.setStatus("pending");
        return repository.save(application);
    }

    public Optional<Application> updateApplication(String id, Application application) {
        Optional<Application> existingApplication = repository.findById(id);
        if (existingApplication.isPresent()) {
            application.setId(id);
            return Optional.of(repository.save(application));
        }
        return Optional.empty();
    }

    public boolean deleteApplication(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Application> approveApplication(String id) {
        Optional<Application> application = repository.findById(id);
        if (application.isPresent()) {
            Application app = application.get();
            app.setStatus("approved");
            return Optional.of(repository.save(app));
        }
        return Optional.empty();
    }

    public Optional<Application> rejectApplication(String id) {
        Optional<Application> application = repository.findById(id);
        if (application.isPresent()) {
            Application app = application.get();
            app.setStatus("rejected");
            return Optional.of(repository.save(app));
        }
        return Optional.empty();
    }
}
