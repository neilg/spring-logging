package io.meles.spring.logging.http;

import static io.meles.spring.logging.http.LogEventMatchers.level;
import static io.meles.spring.logging.http.LogEventMatchers.message;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.Level;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

public class LoggingClientHttpRequestInterceptorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final TestLogger testLogger = new TestLogger("message.debug");

    private LoggingClientHttpRequestInterceptor interceptorUnderTest;

    @Before
    public void givenAnInterceptor() {
        interceptorUnderTest = new LoggingClientHttpRequestInterceptor("message.debug", Charset.forName("UTF-8"));
    }

    @Test
    public void messageLoggerIsRequired() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("messageLogger must not be null");
        new LoggingClientHttpRequestInterceptor(null);
    }

    @Test
    public void defaultCharsetIsRequired() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("defaultCharset must not be null");
        new LoggingClientHttpRequestInterceptor("messages", null);
    }

    @Test
    public void httpRequestWithoutCharsetIsLogged() throws IOException {

        final MockClientHttpRequest httpRequest = new MockClientHttpRequest();
        final String body = "here is a message body ³€½";
        final byte[] bodyBytes = body.getBytes("UTF-8");

        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        final MockClientHttpResponse responseToReturn = new MockClientHttpResponse(new byte[0], HttpStatus.OK);
        when(execution.execute(httpRequest, bodyBytes)).thenReturn(responseToReturn);

        interceptorUnderTest.intercept(httpRequest, bodyBytes, execution);
        verify(execution).execute(httpRequest, bodyBytes);

        assertThat(testLogger.loggedEvents(), hasItem(allOf(
                level(Level.DEBUG), message(body)
        )));
    }

    @Test
    public void doesNotBufferIfLoggerIsNotEnabled() throws IOException {
        testLogger.setLevel(Level.INFO);

        final MockClientHttpRequest httpRequest = new MockClientHttpRequest();
        final String body = "here is a message body ³€½";
        final byte[] bodyBytes = body.getBytes("UTF-8");

        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        final MockClientHttpResponse responseToReturn = new MockClientHttpResponse(new byte[0], HttpStatus.OK);
        when(execution.execute(httpRequest, bodyBytes)).thenReturn(responseToReturn);

        final ClientHttpResponse httpResponse = interceptorUnderTest.intercept(httpRequest, bodyBytes, execution);
        verify(execution).execute(httpRequest, bodyBytes);

        assertThat(httpResponse, is(sameInstance((ClientHttpResponse) responseToReturn)));

    }

    @Test
    public void requestWithoutBodyIsHandled() throws IOException {
        final MockClientHttpRequest httpRequest = new MockClientHttpRequest();
        httpRequest.setMethod(HttpMethod.GET);
        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        final MockClientHttpResponse responseToReturn = new MockClientHttpResponse(new byte[0], HttpStatus.OK);
        when(execution.execute(eq(httpRequest), any(byte[].class))).thenReturn(responseToReturn);

//        interceptorUnderTest.intercept(httpRequest, null, execution);
    }

}