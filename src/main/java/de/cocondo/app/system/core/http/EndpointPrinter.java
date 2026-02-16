package de.cocondo.app.system.core.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Component
public class EndpointPrinter implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(EndpointPrinter.class);

    private final RequestMappingHandlerMapping handlerMapping;

    @Value("${springdoc.api-docs.path}")
    private String apiDocsPath;

    @Value("${springdoc.swagger-ui.path}")
    private String swaggerUiPath;

    @Value("de.cocondo.app.system")
    private String systemPackagePrefix;

    @Value("de.cocondo.app.domain")
    private String domainPackagePrefix;

    @Autowired
    public EndpointPrinter(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        List<String> systemEndpoints = new ArrayList<>();
        List<String> domainEndpoints = new ArrayList<>();

        systemEndpoints.add("API Docs: " + apiDocsPath);
        systemEndpoints.add("Swagger UI: " + swaggerUiPath + "/index.html");

        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (HandlerMethod handlerMethod : handlerMethods.values()) {
            Class<?> beanType = handlerMethod.getBeanType();
            RequestMapping requestMapping = beanType.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                String[] paths = requestMapping.value();
                for (String path : paths) {
                    String endpoint = beanType.getSimpleName() + ": " + path;
                    if (beanType.getPackageName().startsWith(systemPackagePrefix)) {
                        if (!systemEndpoints.contains(endpoint)) {
                            systemEndpoints.add(endpoint);
                        }
                    } else {
                        if (beanType.getPackageName().startsWith(domainPackagePrefix)) {
                            if (!domainEndpoints.contains(endpoint)) {
                                domainEndpoints.add(endpoint);
                            }
                        }
                    }
                }
            }
        }

        logger.info("[{}] System Endpoints", systemEndpoints.size());
        for (String endpoint : systemEndpoints) {
            logger.debug(endpoint);
        }

        logger.info("[{}] Domain Endpoints", domainEndpoints.size());
        for (String endpoint : domainEndpoints) {
            logger.debug(endpoint);
        }
    }
}
