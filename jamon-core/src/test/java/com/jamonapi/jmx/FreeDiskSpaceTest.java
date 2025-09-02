package com.jamonapi.jmx;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FreeDiskSpaceTest {
    private static final long FREE_SPACE_GB =10;
    private static final long TOTAL_SPACE_GB =20;
    private static final long FREE_SPACE_BYTES =(long)FreeDiskSpace.GB*10;
    private static final long TOTAL_SPACE_BYTES =(long)FreeDiskSpace.GB*20;


    private FreeDiskSpace freeDiskSpace;

    @Before
    public void setUp() throws Exception {
        File file = mock(File.class);
        when(file.getAbsolutePath()).thenReturn("/");
        when(file.getFreeSpace()).thenReturn(FREE_SPACE_BYTES);
        when(file.getTotalSpace()).thenReturn(TOTAL_SPACE_BYTES);

        freeDiskSpace = new FreeDiskSpace(file);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetFreeSpace() throws Exception {
        assertThat(freeDiskSpace.getFreeSpace()).isEqualTo(FREE_SPACE_GB);
    }

    @Test
    public void testGetTotalSpace() throws Exception {
        assertThat(freeDiskSpace.getTotalSpace()).isEqualTo(TOTAL_SPACE_GB);
    }

    @Test
    public void testGetFreeSpacePercent() throws Exception {
        assertThat(freeDiskSpace.getFreeSpacePercent()).isEqualTo(.5);

    }

    @Test
    public void testToString() throws Exception {
        assertThat(freeDiskSpace.toString()).isEqualTo("path=/, freeSpace=10.0 GB, totalSpace=20.0 GB, freeSpacePercent=0.5");
    }

    @Test
    public void testWithRealFile() throws Exception {
        FreeDiskSpace diskSpace = new FreeDiskSpace();
        assertThat(diskSpace.getFreeSpace()).isNotNegative();
        assertThat(diskSpace.getTotalSpace()).isNotNegative();
        assertThat(diskSpace.getFreeSpacePercent()).isNotNegative();
    }
}