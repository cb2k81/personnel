package de.cocondo.app.support;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract base class for Spring integration tests.
 *
 * Responsibilities:
 * - Boot full Spring context
 * - Activate test profile
 * - Ensure transactional rollback after each test
 * - Keep Security active (no dev filter)
 *
 * All integration tests for domain services must extend this class.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@WithAnonymousUser
public abstract class AbstractSpringIntegrationTest {
}
