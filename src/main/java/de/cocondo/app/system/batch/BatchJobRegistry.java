package de.cocondo.app.system.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BatchJobRegistry {
    private final List<Job> jobs;

    public Optional<Job> findJobByName(String name) {
        return jobs.stream().filter(j -> j.getName().equals(name)).findFirst();
    }
}
