package de.cocondo.app.system.batch;

import lombok.Value;

@Value(staticConstructor = "of")
public class BatchProgressDTO {
    long totalCount;
    long processedCount;
    double percent;

    public static BatchProgressDTO of(BatchJobItem job) {
        long total = job.getTotalCount();
        long processed = job.getProcessedCount();
        double percent = (total > 0) ? ((double) processed / total * 100.0) : 0;
        return new BatchProgressDTO(total, processed, percent);
    }
}
