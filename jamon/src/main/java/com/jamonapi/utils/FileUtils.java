package com.jamonapi.utils;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

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

    public static boolean mkdirs(String directoryName) {
        return new File(directoryName).mkdirs();
    }

    public static boolean exists(String fileOrDirectoryName) {
        File fileOrDirectory = new File(fileOrDirectoryName);
        return fileOrDirectory.exists();
    }

    public static boolean delete(String fileOrDirectoryName) {
        File file = new File(fileOrDirectoryName);
        if (file.exists()) {
          return file.delete();
        }
        return false;
    }


    public static File[] listFiles(String directory, final String filterRegex) {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String fileName) {
                return fileName.matches(filterRegex);
            }
        };

        return new File(directory).listFiles(filter);
    }


    public static OutputStream getOutputStream(String fileName) throws IOException {
        OutputStream file = new FileOutputStream(fileName);
        file = new BufferedOutputStream(file);
        return file;
    }

    public static InputStream getInputStream(String fileName) throws IOException  {
        InputStream file = new FileInputStream(fileName);
        file = new BufferedInputStream(file);
        return file;
    }
}

