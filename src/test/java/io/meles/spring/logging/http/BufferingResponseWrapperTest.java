package io.meles.spring.logging.http;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

public class BufferingResponseWrapperTest {

    private byte[] responseBody;

    private ClientHttpResponse wrappedResponse;
    private BufferingResponseWrapper wrapperUnderTest;

    @Before
    public void givenABufferingResponseWrapper() throws IOException {
        final int size = 3456;
        responseBody = new byte[size];
        for (int b = 0; b < size; b++) {
            responseBody[b] = (byte) b;
        }
        wrappedResponse = mock(ClientHttpResponse.class);
        when(wrappedResponse.getBody()).thenReturn(new ByteArrayInputStream(responseBody));
        wrapperUnderTest = new BufferingResponseWrapper(wrappedResponse);
    }

    @Test
    public void getBodyShouldReturnSameBytes() throws IOException {
        final byte[] bytes = StreamUtils.copyToByteArray(wrapperUnderTest.getBody());
        assertArrayEquals(responseBody, bytes);
    }

    @Test
    public void bodyCanBeReread() throws IOException {
        final byte[] bytesOne = StreamUtils.copyToByteArray(wrapperUnderTest.getBody());
        final byte[] bytesTwo = StreamUtils.copyToByteArray(wrapperUnderTest.getBody());
        assertArrayEquals(responseBody, bytesOne);
        assertArrayEquals(responseBody, bytesTwo);
    }

    @Test
    public void getRawStatusCodeShouldDelegate() throws IOException {
        when(wrappedResponse.getRawStatusCode()).thenReturn(321);
        assertThat(wrapperUnderTest.getRawStatusCode(), is(321));
        verify(wrappedResponse).getRawStatusCode();
    }

    @Test
    public void getStatusTextShouldDelegate() throws IOException {
        when(wrappedResponse.getStatusText()).thenReturn("fizz buzz");
        assertThat(wrapperUnderTest.getStatusText(), is("fizz buzz"));
        verify(wrappedResponse).getStatusText();
    }

    @Test
    public void closeShouldDelegate() {
        wrapperUnderTest.close();
        verify(wrappedResponse).close();
    }

    @Test
    public void getHeadersShouldDelegate() {
        final HttpHeaders headers = new HttpHeaders();
        when(wrappedResponse.getHeaders()).thenReturn(headers);
        assertThat(wrapperUnderTest.getHeaders(), is(headers));
        verify(wrappedResponse).getHeaders();
    }
}