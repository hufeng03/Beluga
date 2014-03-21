package com.hufeng.filemanager.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by feng on 13-10-22.
 */
public class TestUtils {

    public static void writeBytesToFile(File file, byte[] data){
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static byte[] readBytesFromFile(File file){
        FileInputStream stream = null;
        byte[] data = null;
        try {
            stream = new FileInputStream(file);
            int size = stream.available();
            data = new byte[size];
            stream.read(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }
}
