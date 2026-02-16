package de.cocondo.app.system.core.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Custom wrapper for HttpServletRequest to capture and provide access to the request body.
 */
public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final String requestBody;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);

        try {
            // Lese den Request Body und speichere ihn als String
            BufferedReader reader = request.getReader();
            this.requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException("Error reading the request body", e);
        }
    }

    public String getRequestBody() {
        return this.requestBody;
    }
}
