package com.jamonapi.utils;


import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class BufferListTest {

    private Object[][] getData() {
        Object[][] data={{"steven","thomas","souza"},{"mindy","darci","souza"}};
        return data;
    }

    @Test
    public void testData_WithExtraData() {
        BufferList bl=new BufferList(new String[]{"fname","mname"});
        Object[][] data = getData();
        bl.addRow(data[0]);
        bl.addRow(data[1]);

        DetailData detailData=bl.getDetailData();
        data=detailData.getData();

        // header
        assertThat(detailData.getHeader().length).isEqualTo(3);
        assertThat(Arrays.toString(detailData.getHeader())).isEqualTo("[fname, mname, col2]");

        // data
        assertThat(data.length).isEqualTo(2);
        assertThat(Arrays.toString(data[0])).isEqualTo("[steven, thomas, souza]");
        assertThat(Arrays.toString(data[1])).isEqualTo("[mindy, darci, souza]");
    }


    @Test
    public void testData_WithExtraHeader() {
        BufferList bl=new BufferList(new String[]{"fname","mname","lname","salary"});
        Object[][] data = getData();
        bl.addRow(data[0]);
        bl.addRow(data[1]);

        DetailData detailData=bl.getDetailData();
        data=detailData.getData();

        // header
        assertThat(detailData.getHeader().length).isEqualTo(4);
        assertThat(Arrays.toString(detailData.getHeader())).isEqualTo("[fname, mname, lname, salary]");

        // data
        assertThat(data.length).isEqualTo(2);
        assertThat(Arrays.toString(data[0])).isEqualTo("[steven, thomas, souza, null]");
        assertThat(Arrays.toString(data[1])).isEqualTo("[mindy, darci, souza, null]");
    }

}
