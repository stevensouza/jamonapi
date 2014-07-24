package com.jamonapi.utils;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/** Reusable Utilities used for File manipulations such as reading a file as a String.  **/
public class FileUtils extends java.lang.Object {
    /**
     * Read text files contents in as a String.
     * 
     * <p>Sample Call:
     *  String contents=FileUtils.getFileContents("autoexec.bat");
     **/
    public static String getFileContents(String fileName)throws FileNotFoundException,IOException {
        Monitor mon=MonitorFactory.start("com.jamonapi.utils.FileUtils-getFileContents()");
        final int EOF=-1;
        StringBuffer fileContents = new StringBuffer();
        BufferedReader inputStream=null;

        // Loop through text file storing contents of the file in a string buffer and return the files
        // contents to the caller.
        try {

            inputStream   = new BufferedReader(new FileReader(fileName));
            int inputChar = inputStream.read();

            while (inputChar!=EOF) {
                fileContents.append((char) inputChar);
                inputChar = inputStream.read();
            }
        }
        finally {
            if (inputStream!=null) {
                inputStream.close();
            }
            mon.stop();
        }

        return fileContents.toString();

    }

    /** Replace all invalid file characters with valid ones.  example: himom(*).txt becomes himom---.txt
     * Note some characters that will be replaced wouldn't really be invalid (' ' for example) but a
     * conservative approach is taken.
     *
     * @param fileName
     * @return
     */
    public static String makeValidFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9_\\-\\.]", "-");
    }
}

