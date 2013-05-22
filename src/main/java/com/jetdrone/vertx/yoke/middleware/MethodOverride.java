/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetdrone.vertx.yoke.middleware;

import com.jetdrone.vertx.yoke.Middleware;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.json.JsonObject;

public class MethodOverride extends Middleware {

    private final String key;

    public MethodOverride(String key) {
        this.key = key;
    }

    public MethodOverride() {
        this("_method");
    }

    @Override
    public void handle(final YokeRequest request, final Handler<Object> next) {
        final MultiMap urlEncoded = request.formAttributes();

        if (urlEncoded != null) {
            String method = urlEncoded.get(key);
            if (method != null) {
                urlEncoded.remove(key);
                request.setMethod(method);
                next.handle(null);
                return;
            }
        }

        final JsonObject json = request.jsonBody();
        if (json != null) {
            String method = json.getString(key);
            if (method != null) {
                json.removeField(key);
                request.setMethod(method);
                next.handle(null);
                return;
            }
        }

        String xHttpMethodOverride = request.getHeader("x-http-setMethod-override");

        if (xHttpMethodOverride != null) {
            request.setMethod(xHttpMethodOverride);
        }

        // if reached the end continue to the next middleware
        next.handle(null);
    }
}
