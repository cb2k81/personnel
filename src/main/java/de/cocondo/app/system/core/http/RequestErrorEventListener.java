package de.cocondo.app.system.core.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.cocondo.app.system.core.context.RequestContextDataContainer;
import de.cocondo.app.system.core.security.principal.Principal;
import de.cocondo.app.system.core.util.serialize.PayloadSerializer;
import de.cocondo.app.system.event.ErrorEntity;
import de.cocondo.app.system.event.ErrorEntityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RequestErrorEventListener {

    @Value("${error-handling.error-event.max-stack-trace-length:4000}")
    private int maxStackTraceLength;

    @Value("${error-handling.error-event.max-payload-length:4000}")
    private int maxPayloadLength;

    @Value("${error-handling.error-event.log-payloads:true}")
    private boolean logPayloads;

    private final ErrorEntityRepository errorEntityRepository;
    private final RequestContextDataContainer requestContextDataContainer;
    private final PayloadSerializer payloadSerializer;

    public RequestErrorEventListener(
            ErrorEntityRepository errorEntityRepository,
            RequestContextDataContainer requestContextDataContainer,
            PayloadSerializer payloadSerializer
    ) {
        this.errorEntityRepository = errorEntityRepository;
        this.requestContextDataContainer = requestContextDataContainer;
        this.payloadSerializer = payloadSerializer;
    }

    @EventListener
    public void handleRequestErrorEvent(RequestErrorEvent requestErrorEvent) {
        String errorType = requestErrorEvent.getErrorType();
        String errorMessage = requestErrorEvent.getErrorMessage();
        Long errorId = requestErrorEvent.getErrorId();
        String stackTrace = requestErrorEvent.getStackTrace();
        String requestMethod = requestErrorEvent.getRequestMethod();
        String requestUri = requestErrorEvent.getRequestUri();
        String remoteAddress = requestErrorEvent.getRemoteAddress();
        String principalId = getPrincipalId();

        // Verwende den konfigurierbaren maxStackTraceLength-Wert aus application.yml
        if (stackTrace.length() > this.maxStackTraceLength) {
            stackTrace = stackTrace.substring(0, this.maxStackTraceLength);
        }

        ErrorEntity errorEntity = createErrorEntity(errorType, errorMessage, errorId, stackTrace, requestMethod, requestUri, remoteAddress, principalId);
        handlePayloadLogging(errorEntity);

        errorEntityRepository.save(errorEntity);
    }

    private ErrorEntity createErrorEntity(String errorType, String errorMessage, Long errorId, String stackTrace, String requestMethod, String requestUri, String remoteAddress, String principalId) {
        ErrorEntity errorEntity = new ErrorEntity();
        errorEntity.setErrorType(errorType);
        errorEntity.setErrorMessage(errorMessage);
        errorEntity.setErrorId(errorId);
        errorEntity.setStackTrace(stackTrace);
        errorEntity.setRequestMethod(requestMethod);
        errorEntity.setRequestUri(requestUri);
        errorEntity.setRemoteAddress(remoteAddress);
        errorEntity.setPrincipalId(principalId);
        errorEntity.setMappedObjectClassName(getMappedObjectClassName());

        return errorEntity;
    }

    private void handlePayloadLogging(ErrorEntity errorEntity) {
        if (logPayloads) {
            Object mappedObject = requestContextDataContainer.getMappedObject();
            String serializedMappedObject = serializeMappedObject(mappedObject);

            // Begrenze die Payload-Länge entsprechend der Konfiguration
            if (serializedMappedObject.length() > this.maxPayloadLength) {
                serializedMappedObject = serializedMappedObject.substring(0, this.maxPayloadLength);
            }

            errorEntity.setPayload(serializedMappedObject);
        }
    }

    private String serializeMappedObject(Object mappedObject) {
        try {
            return payloadSerializer.serializePayload(mappedObject);
        } catch (JsonProcessingException e) {
            return "Failed to serialize mappedObject. " + e.getMessage();
        }
    }

    private String getPrincipalId() {
        Principal principal = requestContextDataContainer.getCurrentPrincipal();
        if (principal != null) {
            return principal.getId();
        }
        return null;
    }

    private String getMappedObjectClassName() {
        Object mappedObject = requestContextDataContainer.getMappedObject();
        return mappedObject != null ? mappedObject.getClass().getName() : null;
    }
}
