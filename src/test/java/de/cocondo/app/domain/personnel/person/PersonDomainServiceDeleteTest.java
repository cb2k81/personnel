package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.assignment.PositionFilling;
import de.cocondo.app.domain.personnel.assignment.PositionFillingRepository;
import de.cocondo.app.domain.personnel.assignment.PositionFillingType;
import de.cocondo.app.domain.personnel.assignment.StaffingAssignmentPlan;
import de.cocondo.app.domain.personnel.organisation.organisationunit.OrganisationUnit;
import de.cocondo.app.domain.personnel.staffing.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PersonDomainServiceDeleteTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonDomainService personDomainService;

    @Autowired
    private PositionFillingRepository positionFillingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @WithMockUser(authorities = PersonPermissions.DELETE)
    @DisplayName("Hard delete without references -> entity removed")
    void delete_withoutReferences_deletes() {

        Person person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setStatus(PersonStatus.ACTIVE);

        personRepository.save(person);
        String id = person.getId();

        personDomainService.deletePerson(id);

        Optional<Person> deleted = personRepository.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    @WithMockUser(authorities = PersonPermissions.DELETE)
    @DisplayName("Delete with references -> IllegalStateException")
    void delete_withReferences_throws() {

        Person person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setStatus(PersonStatus.ACTIVE);
        personRepository.save(person);

        OrganisationUnit ou = new OrganisationUnit();
        ou.setOrgUnitBusinessKey("OU-TEST-001");
        entityManager.persist(ou);

        PositionPost post = new PositionPost();
        post.setPostBusinessKey("POST-TEST-001");
        entityManager.persist(post);

        StaffingPlanSet planSet = new StaffingPlanSet();
        entityManager.persist(planSet);

        StaffingPlan plan = new StaffingPlan();
        plan.setStaffingPlanSet(planSet);
        plan.setVersionNumber(1);
        plan.setPlanVariantType(PlanVariantType.APPROVED);
        plan.setWorkflowStatus(WorkflowStatus.DRAFT);
        plan.setValidFrom(LocalDate.now());
        plan.setSafetyDeductionFactor(new BigDecimal("0.1000"));
        planSet.getStaffingPlans().add(plan);
        entityManager.persist(plan);

        PlannedPost plannedPost = new PlannedPost();
        plannedPost.setStaffingPlan(plan);
        plannedPost.setPositionPost(post);
        plannedPost.setPlannedOrganisationUnit(ou);
        plannedPost.setValidFrom(LocalDate.now());
        plan.getPlannedPosts().add(plannedPost);
        entityManager.persist(plannedPost);

        StaffingAssignmentPlan assignmentPlan = new StaffingAssignmentPlan();
        assignmentPlan.setStaffingPlan(plan);
        assignmentPlan.setWorkflowStatus(WorkflowStatus.DRAFT);
        assignmentPlan.setValidFrom(LocalDate.now());
        entityManager.persist(assignmentPlan);

        PositionFilling filling = new PositionFilling();
        filling.setPerson(person);
        filling.setPlannedPost(plannedPost);
        filling.setStaffingAssignmentPlan(assignmentPlan);
        filling.setFilledFrom(LocalDate.now());
        filling.setContractualPortionPercent(new BigDecimal("100.00"));
        filling.setFillingType(PositionFillingType.BASE_CONTRACT);
        assignmentPlan.getPositionFillings().add(filling);

        entityManager.persist(filling);
        entityManager.flush();

        assertThat(positionFillingRepository.existsByPerson_Id(person.getId())).isTrue();

        assertThatThrownBy(() ->
                personDomainService.deletePerson(person.getId())
        ).isInstanceOf(IllegalStateException.class);

        assertThat(personRepository.findById(person.getId())).isPresent();
    }

    @Test
    @WithMockUser
    @DisplayName("Delete without permission -> AccessDeniedException")
    void delete_withoutPermission_throws() {

        Person person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setStatus(PersonStatus.ACTIVE);
        personRepository.save(person);

        assertThatThrownBy(() ->
                personDomainService.deletePerson(person.getId())
        ).isInstanceOf(AccessDeniedException.class);
    }
}