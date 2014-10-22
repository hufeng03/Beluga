package com.hufeng.safebox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtil {
	
	private static final int HEAD_BYTE_LENGTH = 4096;
	private static final int TAIL_BYTE_LENGTH = 4096;
	
	public static int bytesToInt(byte[] bytes){
		int num = bytes[3] & 0xFF;

		num |= ((bytes[2] << 8) & 0xFF00);

		num |= ((bytes[1] << 16) & 0xFF0000);

		num |= ((bytes[0] << 24) & 0xFF000000);

        return num;
	}
	
	public static byte[] decryptOneFile(String path) throws Exception {
		InputStream inputStream = new FileInputStream(new File(path));
		byte[] label = new byte[6];
		inputStream.read(label);
		String label_str = new String(label, "UTF-8");
		byte[] int_data = new byte[4];
		inputStream.read(int_data);
		int head_length = bytesToInt(int_data);
		inputStream.read(int_data);
		int encrypted_length = bytesToInt(int_data);
		inputStream.read(int_data);
		int key_length = bytesToInt(int_data);
		byte[] key = new byte[key_length];
		inputStream.read(key);
		inputStream.close();

        byte [] encrypted = null;
        byte [] decrypted = null;
		RandomAccessFile file = null;
		try{
			file = new RandomAccessFile(path, "rw");
			int pos = 6+3*4+key_length;
			file.seek(pos);
			encrypted = new byte[encrypted_length];
			file.read(encrypted, 0, head_length-pos);
			file.seek(file.length()-(encrypted_length-head_length+pos));
			file.read(encrypted, head_length-pos, encrypted_length-head_length+pos);
			
//			byte[] key = CryptUtil.getKey();
			decrypted = CryptUtil.decrypt(key, encrypted);
			file.seek(0);
			file.write(decrypted);
			file.setLength(file.length()-(encrypted_length-head_length+pos));
//			file.read(buffer);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(file!=null)
				file.close();
		}

        return decrypted;
	}
	
	public static byte[] encryptOneFile(String path) throws Exception {
		InputStream inputStream  = null;
        inputStream = new FileInputStream(new File(path));
		byte[] headData = readHeadBytes(inputStream);
		int headData_length = headData.length;
		byte[] key = CryptUtil.getKey();		
		byte[] encrypted = CryptUtil.encrypt(key, headData);

		int encrypted_length = encrypted.length;

//		String ext = "";
//		int dotPosition = path.lastIndexOf('.');
//	    if (dotPosition != -1){
//			ext = path.substring(dotPosition + 1, path.length()).toLowerCase();
//	        if("gz".equals(ext) && path.endsWith(".tar.gz")){
//	        	ext = "tar.gz";
//	        }
//	    }
//	    int ext_length = ext.length();
        int key_length = key.length;

        inputStream.close();
		
		RandomAccessFile file = null;
		try{
			file = new RandomAccessFile(path, "rw");
			file.seek(0);
			file.writeBytes("HUFENG");
			file.writeInt(headData_length);
			file.writeInt(encrypted_length);
			file.writeInt(key_length);
			file.write(key);
			int pos = (int)file.getFilePointer();
			file.write(encrypted, 0, headData_length-pos);
			file.seek(0);
			file.seek(file.length());
			file.write(encrypted,headData_length-pos,encrypted_length-headData_length+pos);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(file!=null)
				file.close();
		}

        return headData;
	}
	
	
	
	public static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }
	
    public static byte[] readHeadBytes(InputStream stream) throws IOException{
    	int available = stream.available();
    	int size = (available/4<HEAD_BYTE_LENGTH)?available/4:HEAD_BYTE_LENGTH;
    	byte[] b = new byte[size];
    	int readCount = 0; // 已经成功读取的字节的个数
    	while (readCount < size) {
    	   readCount += stream.read(b, readCount, size - readCount);
    	}
    	return b;
    }
    
    public static byte[] readTailBytes(InputStream stream) throws IOException{
    	int available = stream.available();
    	int size = (available/4<TAIL_BYTE_LENGTH)?available/4:TAIL_BYTE_LENGTH;
    	byte[] b = new byte[size];
    	int readCount = 0; // 已经成功读取的字节的个数
    	
    	while (readCount < size) {
    	   readCount += stream.read(b, readCount, size - readCount);
    	}
    	return b;
    }
    
    public static byte[] getKey() throws Exception{
    	byte[] keyStart = "this is a key".getBytes();
    	KeyGenerator kgen = KeyGenerator.getInstance("AES");
    	SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    	sr.setSeed(keyStart);
    	kgen.init(128, sr); // 192 and 256 bits may not be available
    	SecretKey skey = kgen.generateKey();
    	byte[] key = skey.getEncoded();
    	return key;
    }

}
