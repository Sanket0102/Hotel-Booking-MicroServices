package com.hotel_app.config;

import com.hotel_app.filter.JwtAuthenticationFilter;
import com.hotel_app.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class GatewayConfig {

    @Bean(name = "adminOnlyFilter")
    public JwtAuthenticationFilter adminOnlyFilter(JwtUtils jwtUtils){
        return new JwtAuthenticationFilter(jwtUtils, Set.of("ROLE_ADMIN"));
    }

    @Bean(name = "userOnlyFilter")
    public JwtAuthenticationFilter userOnlyFilter(JwtUtils jwtUtils){
        return new JwtAuthenticationFilter(jwtUtils,Set.of("ROLE_USER"));
    }

    @Bean(name = "userAdminFilter")
    public JwtAuthenticationFilter userAdminFilter(JwtUtils jwtUtils){
        return  new JwtAuthenticationFilter(jwtUtils, Set.of("ROLE_USER","ROLE_ADMIN"));
    }

    @Bean(name = "notAuthenticated")
    public JwtAuthenticationFilter notAuthenticated(JwtUtils jwtUtils){
        return new JwtAuthenticationFilter(jwtUtils,null);
    }


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           @Qualifier("adminOnlyFilter") JwtAuthenticationFilter adminOnlyFilter,
                                           @Qualifier("userOnlyFilter") JwtAuthenticationFilter userOnlyFilter,
                                           @Qualifier("notAuthenticated") JwtAuthenticationFilter notAuthenticated,
                                           @Qualifier("userAdminFilter") JwtAuthenticationFilter userAdminFilter){

        return builder.routes()
                // Require authentication (any valid user) for registering users
                .route("auth-service-register-authenticated",
                        r -> r.path("/users/register-user")
                                .filters(f -> f.filter(notAuthenticated))
                                .uri("http://localhost:3000")
                )
                .route("auth-login",
                        r->r.path("/auth/login")
                                .filters(f-> f.filter(notAuthenticated))
                                .uri("http://localhost:3000")
                )
                // Allow ROLE_USER or ROLE_ADMIN to access user detail by email
                .route("auth-service-admin",
                        r -> r.path("/users/3000")
                                .filters(f -> f.filter(adminOnlyFilter))
                                .uri("http://localhost:3000")
                )

                // Allow only ROLE_ADMIN to delete a user
                .route("auth-service-user",
                        r -> r.path("/users/get-user-by-email/**")
                                .filters(f -> f.filter(userOnlyFilter))
                                .uri("http://localhost:3000")
                )
                .route("role-service",
                        r -> r.path("/api/roles/**")
                                .filters(f -> f.filter(adminOnlyFilter))
                                .uri("http://localhost:3000")
                )
                .route("add-room-admin",
                        r -> r.path("/rooms/add/new-room")
                                .filters(f -> f.filter(adminOnlyFilter))
                                .uri("http://localhost:2000")
                )
                .route("delete-room",
                        r ->r.path("/rooms/delete/**")
                                .filters(f -> f.filter(adminOnlyFilter))
                                .uri("http://localhost:2000")
                )
                .route("update-room",
                        r ->r.path("/rooms/update/**")
                                .filters(f -> f.filter(adminOnlyFilter))
                                .uri("http://localhost:2000")
                )
                .route("get-rooms",
                        r -> r.path("/rooms/**")
                                .filters(f -> f.filter(notAuthenticated)
                                        //.circuitBreaker(cb -> cb.setName("roomService")
                                        //.setFallbackUri("forward:/room-fallback")
                                )
                                .uri("http://localhost:2000")
                )
                // Room and booking services: require authentication (any authenticated user)
                .route("get-all-bookings",
                        r -> r.path("/bookings/all-bookings")
                                .filters(f -> f.filter(adminOnlyFilter)
                                        //.circuitBreaker(cb -> cb.setName("bookedRoomService")
                                        //.setFallbackUri("forward:/booked-room-fallback")
                                )
                                .uri("http://localhost:1001")
                )
                .route("get-bookings",
                        r -> r.path("/bookings/user/**")
                                .filters(f -> f.filter(userAdminFilter)
                                        //.circuitBreaker(cb -> cb.setName("bookedRoomService")
                                        //.setFallbackUri("forward:/booked-room-fallback")
                                )
                                .uri("http://localhost:1001")
                )
                .route("get-all-bookings",
                        r -> r.path("/bookings/confirmation/**")
                                .filters(f -> f.filter(userAdminFilter)
                                        //.circuitBreaker(cb -> cb.setName("bookedRoomService")
                                        //.setFallbackUri("forward:/booked-room-fallback")
                                )
                                .uri("http://localhost:1001")
                )
                .route("get-all-bookings-by-room-id",
                        r -> r.path("/bookings/all-bookings-by-room-id/**")
                                .filters(f -> f.filter(adminOnlyFilter)
                                        //.circuitBreaker(cb -> cb.setName("bookedRoomService")
                                        //.setFallbackUri("forward:/booked-room-fallback")
                                )
                                .uri("http://localhost:1001")
                )
                .route("get-all-bookings-by-room-dates",
                        r -> r.path("/bookings/booked-room-by-dates")
                                .filters(f -> f.filter(adminOnlyFilter)
                                        //.circuitBreaker(cb -> cb.setName("bookedRoomService")
                                        //.setFallbackUri("forward:/booked-room-fallback")
                                )
                                .uri("http://localhost:1001")
                )
                .route("add-booking",
                        r -> r.path("/bookings/room/**")
                                .filters(f -> f.filter(userOnlyFilter)
                                        //.circuitBreaker(cb -> cb.setName("bookedRoomService")
                                        //.setFallbackUri("forward:/booked-room-fallback")
                                )
                                .uri("http://localhost:1001")
                )
                .route("delete-bookings",
                        r -> r.path("/bookings/booking/**")
                                .filters(f -> f.filter(userAdminFilter)
                                        //.circuitBreaker(cb -> cb.setName("bookedRoomService")
                                        //.setFallbackUri("forward:/booked-room-fallback")
                                )
                                .uri("http://localhost:1001")
                )
                .build();
    }


//    @Bean
//    WebFluxProperties webFluxProperties(){
//        return new WebFluxProperties();
//    }
}