package com.book_social_network.book_network.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JWT_AuthFilter extends OncePerRequestFilter {

    private final JWT_Service jwtService;
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
          @NotNull HttpServletRequest request,
          @NotNull  HttpServletResponse response,
          @NotNull  FilterChain filterChain) throws ServletException, IOException {

        if(request.getServletPath().contains("api/v1/auth")){
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(token);
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
             if(jwtService.validateToken(token, userDetails)) {
                 UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                         userDetails, null, userDetails.getAuthorities()
                 );
                 authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                 SecurityContextHolder.getContext().setAuthentication(authenticationToken);
             }
        }
        filterChain.doFilter(request, response);

    }

}
