package de.cocondo.app.system.core.security.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {

    Logger logger = LoggerFactory.getLogger(AccountRepository.class);

    @Query("SELECT a FROM Account a WHERE a.loginName = :loginName")
    Optional<Account> findByLoginName(@Param("loginName") String loginName);

    /*
    default Optional<Account> findByLoginNameWithLogging(String loginName) {
        System.out.println("Executing findByLoginName query with loginName: " + loginName);
        return findByLoginName(loginName);
    }
    */

}
