package io.meles.spring.logging.http;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Logger messageLogger;
    private final Charset defaultCharset;

    public LoggingClientHttpRequestInterceptor(final String messageLogger, final Charset defaultCharset) {
        this.messageLogger = LoggerFactory.getLogger(messageLogger);
        this.defaultCharset = defaultCharset;
    }

    public LoggingClientHttpRequestInterceptor(final String messageLogger) {
        this(messageLogger, Charset.defaultCharset());
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException {

        if (messageLogger.isDebugEnabled()) {
            final String requestBody = requestBody(request, body);
            messageLogger.debug(requestBody);
        }

        ClientHttpResponse clientHttpResponse = execution.execute(request, body);

        if (messageLogger.isDebugEnabled()) {
            clientHttpResponse = new BufferingResponseWrapper(clientHttpResponse);
            final String responseBody = responseBody(clientHttpResponse);
            messageLogger.debug(responseBody);
        }
        return clientHttpResponse;
    }

    private String requestBody(final HttpRequest request, final byte[] body) {
        final Charset charset = charset(request);
        return new String(body, charset);
    }

    private String responseBody(final ClientHttpResponse clientHttpResponse) throws IOException {
        final Charset charset = charset(clientHttpResponse);
        return StreamUtils.copyToString(clientHttpResponse.getBody(), charset);
    }

    private Charset charset(final HttpMessage httpMessage) {
        final HttpHeaders headers = httpMessage.getHeaders();
        if (headers == null) {
            return defaultCharset;
        }
        final MediaType contentType = headers.getContentType();
        if (contentType == null) {
            return defaultCharset;
        }
        final Charset charSet = contentType.getCharSet();
        if (charSet == null) {
            return defaultCharset;
        }
        return charSet;
    }
}
