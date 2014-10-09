package io.meles.spring.logging.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

final class BufferingResponseWrapper extends AbstractClientHttpResponse {

    private final ClientHttpResponse wrapped;
    private final byte[] body;

    BufferingResponseWrapper(final ClientHttpResponse wrapped) throws IOException {
        this.wrapped = wrapped;
        this.body = StreamUtils.copyToByteArray(wrapped.getBody());
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
        return new ByteArrayInputStream(body);
    }

    @Override
    public HttpHeaders getHeaders() {
        return wrapped.getHeaders();
    }
}
