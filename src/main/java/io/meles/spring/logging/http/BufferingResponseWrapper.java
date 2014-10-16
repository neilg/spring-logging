package io.meles.spring.logging.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.http.client.ClientHttpResponse;

final class BufferingResponseWrapper extends AbstractClientHttpResponse {

    private final ClientHttpResponse wrapped;
    private final BufferedInputStream body;

    BufferingResponseWrapper(final ClientHttpResponse wrapped) throws IOException {


        // TODO consider using a BufferedInputStream and exposing the means to mark and reset rather than slurping the whole response
        this.wrapped = wrapped;
        this.body = new BufferedInputStream(wrapped.getBody());
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return wrapped.getRawStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return wrapped.getStatusText();
    }

    @Override
    public void close() {
        wrapped.close();
    }

    @Override
    public InputStream getBody() throws IOException {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return wrapped.getHeaders();
    }

    void markBody() {
        body.mark(bytesToBuffer());
    }

    void resetBody() throws IOException {
        body.reset();
    }

    private int bytesToBuffer() {
        return 1024 * 1024; // 1MB
    }
}
