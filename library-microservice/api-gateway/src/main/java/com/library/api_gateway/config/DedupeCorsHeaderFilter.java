package com.library.api_gateway.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * Guarantees a single value for CORS-related response headers, no matter how many
 * times something in the chain (gateway CORS logic, downstream service, router
 * function internals, etc.) tries to write one.
 *
 * Why this exists: DedupeResponseHeader (Spring's documented, "correct" fix for
 * this exact scenario) did not remove the duplicate in testing - curl showed two
 * full sets of CORS headers even with DedupeResponseHeader configured on every
 * route. That points to something writing a second header block after the
 * gateway's route-level filters have already run, late enough in the request
 * lifecycle that DedupeResponseHeader never sees it.
 *
 * Rather than keep chasing exactly where in gateway-server-webmvc's internals
 * that second write happens, this wraps the ServletResponse itself and redirects
 * every addHeader(...) call for these header names into setHeader(...), which
 * REPLACES the existing value instead of appending to it. It doesn't matter how
 * many times anything downstream calls addHeader - only the last value survives,
 * and there is structurally no way for two copies to reach the browser.
 *
 * Registered at HIGHEST_PRECEDENCE so it wraps the response before anything else
 * (including the CorsFilter below) touches it, guaranteeing the wrapped response
 * is what every later filter and handler in the chain writes to.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DedupeCorsHeaderFilter implements Filter {

    private static final Set<String> SINGLE_VALUE_HEADERS = Set.of(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpResponse) {
            chain.doFilter(request, new SingleValueHeaderResponse(httpResponse));
        } else {
            chain.doFilter(request, response);
        }
    }

    private static class SingleValueHeaderResponse extends HttpServletResponseWrapper {
        SingleValueHeaderResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void addHeader(String name, String value) {
            if (SINGLE_VALUE_HEADERS.contains(name)) {
                setHeader(name, value); // replace, never append
            } else {
                super.addHeader(name, value);
            }
        }
    }
}
