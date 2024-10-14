package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AbstractControllerTest {

    protected static final String METHOD = "POST";
    protected static final String URI = "http://somehost/home";
    protected static final String QUERY_STRING = "value=none";
    protected static final String HEADER = "X-Sharer-User-Id";
    protected static final String HEADER_VALUE = "42";

    protected AutoCloseable openMocks;

    @Mock
    protected HttpServletRequest mockHttpRequest;

    @BeforeEach
    protected void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        Mockito.when(mockHttpRequest.getMethod()).thenReturn(METHOD);
        Mockito.when(mockHttpRequest.getRequestURI()).thenReturn(URI);
        Mockito.when(mockHttpRequest.getQueryString()).thenReturn(QUERY_STRING);
        Mockito.when(mockHttpRequest.getHeader(HEADER)).thenReturn(HEADER_VALUE);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getMethod();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getRequestURI();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getQueryString();
        Mockito.verify(mockHttpRequest).getHeader(HEADER);
        Mockito.verifyNoMoreInteractions(mockHttpRequest);
        openMocks.close();
    }
}
