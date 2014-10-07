/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.middleware;

import com.jetdrone.vertx.yoke.Middleware;
import org.jetbrains.annotations.NotNull;
import org.vertx.java.core.Handler;

import java.util.UUID;

/**
 * # Csrf
 *
 * This middleware adds a CSRF token to requests which mutate state. You should put the result within a hidden form
 * field, query-string etc. This token should be validated against the visitor's session.
 * The default value handler checks request body generated by the BodyParser middleware, request query generated and
 * the "X-CSRF-Token" header field.
 *
 * This middleware requires session support, thus should be added somewhere below Session.
 */
public class Csrf implements Middleware {

    /**
     * Handler that validates the CRSF token
     */
    private final ValueHandler valueHandler;

    /**
     * Name of the property where the token is found/stored in the request context
     */
    private final String key;

    /**
     * Instantiate a new Csrf with a user defined key
     *
     * <pre>
     * new Csrf("_crsf")
     * </pre>
     *
     * @param key name of the context variable to store the token.
     */
    public Csrf(@NotNull final String key) {
        this.key = key;
        valueHandler = new ValueHandler() {
            @Override
            public String handle(YokeRequest request) {
                String token = request.formAttributes().get(key);
                if (token == null) {
                    token = request.params().get(key);
                    if (token == null) {
                        token = request.headers().get("x-csrf-token");
                    }
                }

                return token;
            }
        };
    }

    /**
     * Instantiate a new Csrf with the default key "_crsf"
     *
     * <pre>
     * new Csrf()
     * </pre>
     */
    public Csrf() {
        this("_csrf");
    }

    /**
     * Instantiate a new Csrf with custom Handler and key
     *
     * <pre>
     * new Csrf("_crsf", new ValueHandler() {...})
     * </pre>
     *
     * @param key          name of the context variable to store the token.
     * @param valueHandler the handler for the token validation.
     */
    public Csrf(@NotNull String key, @NotNull ValueHandler valueHandler) {
        this.key = key;
        this.valueHandler = valueHandler;
    }

    /**
     * Instantiate a new Csrf with custom Handler
     *
     * <pre>
     * new Csrf(new ValueHandler() {...})
     * </pre>
     *
     * @param valueHandler the handler for the token validation.
     */
    public Csrf(@NotNull final ValueHandler valueHandler) {
        this("_csrf", valueHandler);
    }

    @FunctionalInterface
    public static interface ValueHandler {
        String handle(YokeRequest request);
    }

    @Override
    public void handle(@NotNull final YokeRequest request, @NotNull final Handler<Object> next) {

        String token = request.get(key);
        // generate CSRF token
        if (token == null) {
            token = UUID.randomUUID().toString();
            request.put(key, token);
        }

        // ignore these methods
        if ("GET".equals(request.method()) || "HEAD".equals(request.method()) || "OPTIONS".equals(request.method())) {
            next.handle(null);
            return;
        }

        // expect multipart
        request.expectMultiPart(true);

        // determine value
        String val = valueHandler.handle(request);

        // check
        if (!token.equals(val)) {
            next.handle(403);
            return;
        }

        // OK
        next.handle(null);
    }

}
