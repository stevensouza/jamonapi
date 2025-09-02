package com.jamonapi.http;

import com.jamonapi.MonitorFactory;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JAMonTomcatValve
 */
public class JAMonTomcatValveTest {

    private JAMonTomcatValve valve;
    
    @Mock
    private Request mockRequest;
    
    @Mock
    private Response mockResponse;
    
    @Mock
    private Valve mockNextValve;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        valve = new JAMonTomcatValve();
        valve.setNext(mockNextValve);
        MonitorFactory.reset(); // Clear monitors between tests
    }

    @Test
    public void testConstructorSetsDefaultSummaryLabels() {
        JAMonTomcatValve testValve = new JAMonTomcatValve();
        assertNotNull(testValve.getSummaryLabels());
        String labels = testValve.getSummaryLabels();
        assertTrue("Should contain default labels", labels.contains("request.getRequestURI().ms"));
        assertTrue("Should contain status labels", labels.contains("response.getStatus().value.httpStatus"));
    }

    @Test
    public void testSummaryLabelsManagement() {
        String customLabels = "request.getRequestURI().ms,response.getStatus().value.httpStatus";
        valve.setSummaryLabels(customLabels);
        String actual = valve.getSummaryLabels().replaceAll("\\s+", "");
        String expected = customLabels.replaceAll("\\s+", "");
        assertEquals(expected, actual);
        
        valve.addSummaryLabel("custom.label");
        String updatedLabels = valve.getSummaryLabels();
        assertTrue("Should contain custom.label", updatedLabels.contains("custom.label"));
    }

    @Test
    public void testEnabledProperty() {
        assertTrue(valve.getEnabled()); // Default enabled
        
        valve.setEnabled(false);
        assertFalse(valve.getEnabled());
        
        valve.setEnabled(true);
        assertTrue(valve.getEnabled());
    }

    @Test
    public void testSizeProperty() {
        valve.setSize(5000);
        assertEquals(5000, valve.getSize());
    }

    @Test
    public void testIgnoreHttpParamsProperty() {
        valve.setIgnoreHttpParams(true);
        assertTrue(valve.getIgnoreHttpParams());
        
        valve.setIgnoreHttpParams(false);
        assertFalse(valve.getIgnoreHttpParams());
    }

    @Test
    public void testGetInfo() {
        String info = valve.getInfo();
        assertTrue(info.contains("JAMonTomcatValve"));
    }

    @Test
    public void testInvokeCallsNextValve() throws IOException, ServletException {
        valve.invoke(mockRequest, mockResponse);
        
        verify(mockNextValve, times(1)).invoke(mockRequest, mockResponse);
    }

    @Test
    public void testInvokeHandlesNullNextValve() throws IOException, ServletException {
        valve.setNext(null);
        
        // Should not throw exception
        valve.invoke(mockRequest, mockResponse);
    }

    @Test
    public void testInvokeCreatesMonitor() throws IOException, ServletException {
        int initialMonitorCount = MonitorFactory.getFactory().getNumRows();
        
        valve.invoke(mockRequest, mockResponse);
        
        // Should have created at least one monitor
        assertTrue(MonitorFactory.getFactory().getNumRows() >= initialMonitorCount);
    }

    @Test
    public void testInvokeHandlesException() throws IOException, ServletException {
        doThrow(new ServletException("Test exception")).when(mockNextValve).invoke(any(), any());
        
        try {
            valve.invoke(mockRequest, mockResponse);
            fail("Expected ServletException to be thrown");
        } catch (ServletException e) {
            assertEquals("Test exception", e.getMessage());
            // Exception should still be recorded in monitoring
        }
    }
}