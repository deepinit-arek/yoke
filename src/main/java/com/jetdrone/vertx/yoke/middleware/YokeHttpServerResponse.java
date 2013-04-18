package com.jetdrone.vertx.yoke.middleware;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerResponse;

import java.util.Map;

public class YokeHttpServerResponse implements HttpServerResponse {
    // the original response
    private final HttpServerResponse response;
    // extra handlers
    private Handler<Void> headersHandler;
    private boolean headersHandlerTriggered;
    private Handler<Void> endHandler;

    public YokeHttpServerResponse(HttpServerResponse response) {
        this.response = response;
    }

    void headersHandler(Handler<Void> handler) {
        this.headersHandler = handler;
        headersHandlerTriggered = false;
    }

    void endHandler(Handler<Void> handler) {
        this.endHandler = handler;
    }

    private void triggerHeadersHandler() {
        if (headersHandler != null && !headersHandlerTriggered) {
            headersHandlerTriggered = true;
            headersHandler.handle(null);
        }
    }

    @Override
    public int getStatusCode() {
        return response.getStatusCode();
    }

    @Override
    public HttpServerResponse setStatusCode(int statusCode) {
        return response.setStatusCode(statusCode);
    }

    @Override
    public String getStatusMessage() {
        return response.getStatusMessage();
    }

    @Override
    public HttpServerResponse setStatusMessage(String statusMessage) {
        return response.setStatusMessage(statusMessage);
    }

    @Override
    public HttpServerResponse setChunked(boolean chunked) {
        return response.setChunked(chunked);
    }

    @Override
    public boolean isChunked() {
        return response.isChunked();
    }

    @Override
    public Map<String, Object> headers() {
        return response.headers();
    }

    @Override
    public HttpServerResponse putHeader(String name, Object value) {
        return response.putHeader(name, value);
    }

    @Override
    public Map<String, Object> trailers() {
        return response.trailers();
    }

    @Override
    public HttpServerResponse putTrailer(String name, Object value) {
        return response.putTrailer(name, value);
    }

    @Override
    public HttpServerResponse closeHandler(Handler<Void> handler) {
        return response.closeHandler(handler);
    }

    @Override
    public HttpServerResponse write(Buffer chunk) {
        triggerHeadersHandler();
        return response.write(chunk);
    }

    @Override
    public HttpServerResponse setWriteQueueMaxSize(int maxSize) {
        return response.setWriteQueueMaxSize(maxSize);
    }

    @Override
    public boolean writeQueueFull() {
        return response.writeQueueFull();
    }

    @Override
    public HttpServerResponse drainHandler(Handler<Void> handler) {
        return response.drainHandler(handler);
    }

    @Override
    public HttpServerResponse write(String chunk, String enc) {
        triggerHeadersHandler();
        return response.write(chunk, enc);
    }

    @Override
    public HttpServerResponse write(String chunk) {
        triggerHeadersHandler();
        return response.write(chunk);
    }

    @Override
    public HttpServerResponse write(Buffer chunk, Handler<AsyncResult<Void>> doneHandler) {
        triggerHeadersHandler();
        return response.write(chunk, doneHandler);
    }

    @Override
    public HttpServerResponse write(String chunk, String enc, Handler<AsyncResult<Void>> doneHandler) {
        triggerHeadersHandler();
        return response.write(chunk, enc, doneHandler);
    }

    @Override
    public HttpServerResponse write(String chunk, Handler<AsyncResult<Void>> doneHandler) {
        triggerHeadersHandler();
        return response.write(chunk, doneHandler);
    }

    @Override
    public void end(String chunk) {
        triggerHeadersHandler();
        response.end(chunk);
        if (endHandler != null) {
            endHandler.handle(null);
        }
    }

    @Override
    public void end(String chunk, String enc) {
        triggerHeadersHandler();
        response.end(chunk, enc);
        if (endHandler != null) {
            endHandler.handle(null);
        }
    }

    @Override
    public void end(Buffer chunk) {
        triggerHeadersHandler();
        response.end(chunk);
        if (endHandler != null) {
            endHandler.handle(null);
        }
    }

    @Override
    public void end() {
        triggerHeadersHandler();
        response.end();
        if (endHandler != null) {
            endHandler.handle(null);
        }
    }

    @Override
    public HttpServerResponse sendFile(String filename) {
        triggerHeadersHandler();
        return response.sendFile(filename);
    }

    @Override
    public void close() {
        response.close();
    }

    @Override
    public HttpServerResponse exceptionHandler(Handler<Exception> handler) {
        return response.exceptionHandler(handler);
    }
}
