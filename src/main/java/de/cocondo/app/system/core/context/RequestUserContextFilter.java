package de.cocondo.app.system.core.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestUserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String user = request.getUserPrincipal() != null
                    ? request.getUserPrincipal().getName()
                    : "system";

            UserContextHolder.setCurrentUser(user);
            filterChain.doFilter(request, response);

        } finally {
            UserContextHolder.clear();
        }
    }
}
