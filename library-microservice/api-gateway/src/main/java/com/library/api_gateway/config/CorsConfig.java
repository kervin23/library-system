package com.library.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Registers CORS handling as a servlet Filter, running at the very front of the
 * filter chain (HIGHEST_PRECEDENCE) - before the gateway's routing logic even sees
 * the request.
 *
 * Why not just use the globalcors.* properties in application.properties?
 * spring-cloud-starter-gateway-server-webmvc routes requests through Spring MVC
 * RouterFunctions rather than DispatcherServlet handler mappings, and the
 * property-based global CORS config isn't reliably applied to preflight OPTIONS
 * requests in that model - the request can get rejected before a route (and its
 * CORS config) is even matched. Registering a CorsFilter bean sidesteps that
 * entirely: Spring's servlet filter chain handles the OPTIONS preflight and adds
 * the Access-Control-Allow-* headers before the gateway routing runs at all.
 */
@Configuration
public class CorsConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1) // must run after DedupeCorsHeaderFilter,
                                            // so that filter's response wrapper is
                                            // already in place when this adds headers
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
