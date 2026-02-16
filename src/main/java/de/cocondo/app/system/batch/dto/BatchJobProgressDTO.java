package de.cocondo.app.system.batch.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class BatchJobProgressDTO {

    private Long jobItemId;
    private String jobName;
    private Long springJobInstanceId;
    private String status;

    // Phase 5.2 – Restart-Transparenz (aus JobParameters)
    private String restartMode;

    private Long total;
    private Long pending;
    private Long inProgress;
    private Long done;
    private Long failed;

    private Long processed;
    private Integer progressPercent;

    private Instant startedAt;
    private Instant lastUpdatedAt;
    private Instant finishedAt;
}
