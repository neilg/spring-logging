package io.meles.spring.logging.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.http.client.ClientHttpResponse;

public final class BufferingResponseWrapper extends AbstractClientHttpResponse {

    public static final int MAX_BYTES_TO_BUFFER = 10 * 1024 * 1024;

    private final int maxBytesToBuffer;

    private final ClientHttpResponse wrapped;
    private final BufferedInputStream body;

    public BufferingResponseWrapper(final ClientHttpResponse wrapped) throws IOException {
        this(wrapped, MAX_BYTES_TO_BUFFER);
    }

    public BufferingResponseWrapper(final ClientHttpResponse wrapped, final int maxBytesToBuffer) throws IOException {
        this.wrapped = wrapped;
        this.body = new BufferedInputStream(wrapped.getBody());
        this.maxBytesToBuffer = maxBytesToBuffer;
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
        final long contentLength = wrapped.getHeaders().getContentLength();
        return isInRange(contentLength)
                ? (int) contentLength
                : maxBytesToBuffer;
    }

    private boolean isInRange(long contentLength) {
        return contentLength >= 0L && contentLength < (long) maxBytesToBuffer;
    }
}
