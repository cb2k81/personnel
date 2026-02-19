package de.cocondo.app.domain.personnel.person;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Entity service for the Person aggregate.
 *
 * Encapsulates persistence access for Person entities and operates exclusively on domain objects.
 */
@Service
@RequiredArgsConstructor
public class PersonEntityService {

    private final PersonRepository personRepository;

    public Optional<Person> loadById(String id) {
        return personRepository.findById(id);
    }

    /**
     * Loads a single Person record with an additional specification (e.g. RLS).
     * Used to enforce 404 for non-visible records (ADR 010).
     */
    public Optional<Person> loadOne(Specification<Person> specification) {
        return personRepository.findOne(specification);
    }

    public Person save(Person person) {
        return personRepository.save(person);
    }

    public void delete(Person person) {
        personRepository.delete(person);
    }

    public Page<Person> findAll(Specification<Person> specification, Pageable pageable) {
        return personRepository.findAll(specification, pageable);
    }
}
