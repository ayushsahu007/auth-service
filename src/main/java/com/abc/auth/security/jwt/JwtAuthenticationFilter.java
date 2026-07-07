package com.abc.auth.security.jwt;

import com.abc.auth.security.constants.SecurityConstants;
import com.abc.auth.security.user.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER =
            LogManager.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!hasBearerToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String token = extractToken(request);
            authenticateUser(token, request);

        } catch (ExpiredJwtException ex) {

            LOGGER.warn("JWT has expired.");
            SecurityContextHolder.clearContext();

        } catch (MalformedJwtException ex) {

            LOGGER.warn("Malformed JWT received.");
            SecurityContextHolder.clearContext();

        } catch (SignatureException ex) {

            LOGGER.warn("Invalid JWT signature.");
            SecurityContextHolder.clearContext();

        } catch (UnsupportedJwtException ex) {

            LOGGER.warn("Unsupported JWT.");
            SecurityContextHolder.clearContext();

        } catch (IllegalArgumentException ex) {

            LOGGER.warn("JWT token is empty.");
            SecurityContextHolder.clearContext();

        }

        filterChain.doFilter(request, response);
    }

    private boolean hasBearerToken(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        return authHeader != null
                && authHeader.startsWith(SecurityConstants.BEARER_PREFIX);
    }

    private String extractToken(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        return authHeader.substring(SecurityConstants.BEARER_PREFIX.length());
    }

    private void authenticateUser(String token,
                                  HttpServletRequest request) {

        String email = jwtService.extractEmail(token);

        if (email == null ||
                SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(email);

        if (!jwtService.isTokenValid(token, userDetails.getUsername())) {
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request));

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        LOGGER.debug("User authenticated successfully.");
    }
}