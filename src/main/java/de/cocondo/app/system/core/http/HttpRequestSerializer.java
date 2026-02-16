package de.cocondo.app.system.core.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.cocondo.app.system.core.context.RequestContextDataContainer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This component provides methods to serialize an HTTP request, including URI, headers, and body.
 * It is used in conjunction with the RequestBodyFilter to capture and store the request body in the
 * RequestContextDataContainer. This serializer allows converting the request details into a JSON payload
 * for logging and debugging purposes.
 */
@Component
public class HttpRequestSerializer {

    private final ObjectMapper objectMapper;
    private final RequestContextDataContainer requestContextDataContainer;

    public HttpRequestSerializer(ObjectMapper objectMapper, RequestContextDataContainer requestContextDataContainer) {
        this.objectMapper = objectMapper;
        this.requestContextDataContainer = requestContextDataContainer;
    }

    public String serializePayload(HttpServletRequest request) throws JsonProcessingException {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("uri", request.getRequestURI());
        payloadMap.put("headers", getHeadersMap(request));
        payloadMap.put("body", getRequestBody());

        return objectMapper.writeValueAsString(payloadMap);
    }

    private Map<String, String> getHeadersMap(HttpServletRequest request) {
        Map<String, String> headersMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headersMap.put(headerName, headerValue);
        }
        return headersMap;
    }

    private String getRequestBody() {
        return requestContextDataContainer.getRequestBody();
    }

    public <T> T deserializePayload(String payload, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(payload, valueType);
    }
}
