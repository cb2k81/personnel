package de.cocondo.app.domain.personnel.staffing;

/**
 * Einheitlicher Workflow-Status für StaffingPlan und StaffingAssignmentPlan.
 *
 * Gemäß Sprint-1-Doku und UML:
 * DRAFT / IN_REVIEW / APPROVED / ARCHIVED
 */
public enum WorkflowStatus {
    DRAFT,
    IN_REVIEW,
    APPROVED,
    ARCHIVED
}
