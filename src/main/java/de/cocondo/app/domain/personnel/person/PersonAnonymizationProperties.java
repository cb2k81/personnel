package de.cocondo.app.domain.personnel.person;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "person.anonymization")
public class PersonAnonymizationProperties {

    /**
     * Default value written into string fields during anonymization.
     * Can be overridden via application.yml.
     */
    private String defaultValue = "anonym";

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
