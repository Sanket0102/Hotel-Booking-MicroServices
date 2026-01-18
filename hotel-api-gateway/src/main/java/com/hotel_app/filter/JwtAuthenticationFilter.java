package com.hotel_app.filter;

import com.hotel_app.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.List;

import java.io.IOException;
import java.util.stream.Collectors;


public class JwtAuthenticationFilter implements GatewayFilter {


    private final JwtUtils jwtUtils;
    private final Set<String> requiredRole;

    public JwtAuthenticationFilter(JwtUtils jwtUtils,Set<String> requiredRole){
        this.jwtUtils = jwtUtils;
        this.requiredRole = requiredRole;
    }

    private String parseJwt(ServerWebExchange exchange) {
        String headerAuth = exchange.getRequest()
                .getHeaders().getFirst("Authorization");
        if(headerAuth == null){
            throw new RuntimeException("Bearer token is missing");
        }
        if(StringUtils.hasText("headerAuth") && headerAuth.startsWith("Bearer")){
            return headerAuth.substring(7);
        }
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain filterChain) {

        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/users/register-user") || path.startsWith("/rooms/all-rooms") || path.startsWith("/auth/login") || path.startsWith("/rooms/room")) {
            return filterChain.filter(exchange);
        }

        String jwt = parseJwt(exchange);
        List<String> roles = jwtUtils.getRolesFromToken(jwt);
        if (jwt != null && jwtUtils.validateToken(jwt)) {
            String email = jwtUtils.getUsernameFromToken(jwt);
            if (requiredRole != null) {
                for(String role : roles){
                    if(requiredRole.contains(role)){
                        ServerHttpRequest newRequest = exchange.getRequest()
                                .mutate()
                                .header("X-User-Email", email)
                                .header("X-User-Roles", String.join(",", roles))
                                .build();
                        return filterChain.filter(exchange.mutate().request(newRequest).build());
                    }
                }
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();

    }
}