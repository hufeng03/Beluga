package com.hufeng.filemanager.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by feng on 14-1-23.
 */
public class ZipUtil {

    public static boolean unpackSingleZip(String zipfile, String singlefile)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            is = new FileInputStream(new File(zipfile));
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                String filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
//                    File fmd = new File(path + filename);
//                    fmd.mkdirs();
                    continue;
                }


                FileOutputStream fout = new FileOutputStream(new File(singlefile));

                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
                break;
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
