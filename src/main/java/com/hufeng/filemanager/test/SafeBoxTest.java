package com.hufeng.filemanager.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.hufeng.filemanager.safebox.CryptUtil;

import junit.framework.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SafeBoxTest extends AndroidTestCase{

    private static final String TAG = SafeBoxTest.class.getSimpleName();
	
	public void test0SaveBox() throws Throwable{

		File file = new File("/sdcard/test.log");
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			byte[] head = CryptUtil.readHeadBytes(stream);

			byte[] key = CryptUtil.getKey();

			byte[] encrypted = CryptUtil.encrypt(key, head);

            TestUtils.writeBytesToFile(new File("/sdcard/test.data"), encrypted);
            byte[] encrypted_read = TestUtils.readBytesFromFile(new File("/sdcard/test.data"));

            int len1 = encrypted.length;
            int len2 = encrypted_read.length;
            Assert.assertEquals(len1, len2);
            for(int i=0;i<len1; i++){
                Assert.assertEquals(encrypted[i], encrypted_read[i]);
            }
            Log.i(TAG, "test0SaveBox input passed");
			byte[] decrypted = CryptUtil.decrypt(key, encrypted_read);

			len1 = head.length;
			len2 = decrypted.length;
			Assert.assertEquals(len1, len2);
			for(int i=0;i<len1; i++){
				Assert.assertEquals(head[i], decrypted[i]);
			}
            Log.i(TAG, "test0SaveBox output passed");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(stream!=null)
				stream.close();
		}
	}
//
//    public void test1Encrypte() throws Throwable{
//        CryptUtil.encryptOneFile("/sdcard/test.log");
//    }
//
//    public void test2Decrypte() throws Throwable{
//        CryptUtil.decryptOneFile("/sdcard/test.log");
//    }

    public void test1SafeBox() throws Throwable{
        byte[] a = CryptUtil.encryptOneFile("/sdcard/test.log");

//        byte[] encrypted_read = TestUtils.readBytesFromFile(new File("/sdcard/test.data"));

//        Log.i(TAG, "test0SaveBox input passed");
//        byte[] decrypted = CryptUtil.decrypt(CryptUtil.getKey(), encrypted_read);
//
//        int len1 = a.length;
//        int len2 = encrypted_read.length;
//        Assert.assertEquals(len1, len2);
//        for(int i=0;i<len1; i++){
//            Assert.assertEquals(a[i], encrypted_read[i]);
//        }

        byte[] b = CryptUtil.decryptOneFile("/sdcard/test.log");
        int len1 = a.length;
		int len2 = b.length;
		Assert.assertEquals(len1, len2);
		for(int i=0;i<len1; i++){
			Assert.assertEquals(a[i], b[i]);
		}
        Log.i(TAG, "test1SaveBox passed");
    }


}
