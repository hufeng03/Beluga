package com.hufeng.playzip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;

/**
 * 压缩或者是解压tar的工具类
 *
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @since  2012-4-19  上午9:21:21
 * @version 1.0.0
 */
public class TarUtil {
    private TarOutputStream tarOut;     //压缩Zip 
    private  int      		bufSize;    //size of bytes 
    private byte[]          buf; 

    public TarUtil(){
    	//要构造函数中去初始化我们的缓冲区
    	this.bufSize = 1024*4; 
        this.buf = new byte[this.bufSize]; 
    }

	/**
	 *  由doTar调用,递归完成目录文件读取
	 * @param zipFile
	 * @param zipOut
	 * @param dirName  这个主要是用来记录压缩文件的一个目录层次结构的
	 * @throws IOException
	 */
	private void handleFile(File zipFile, TarOutputStream zipOut,String dirName) throws IOException {
		System.out.println("遍历文件："+zipFile.getName());
		//如果是一个目录，则遍历
		if(zipFile.isDirectory())
		{
			File[] files = zipFile.listFiles();

			if (files.length == 0) {// 如果目录为空,则单独创建之.
				// ZipEntry的isDirectory()方法中,目录以"/"结尾.
				System.out.println("压缩的空目录 :"+dirName+zipFile.getName()+File.separator);
				this.tarOut.putNextEntry(new TarEntry(dirName+zipFile.getName()+File.separator));
				this.tarOut.closeEntry();
			} else {// 如果目录不为空,则进入递归，处理下一级文件
				for (File file : files) {
					// System.out.println(fileName);
					handleFile(file, zipOut, dirName+zipFile.getName()+File.separator);
				}
			}
		}
		//如果是文件，则直接压缩
		else
		{
			FileInputStream fileIn  = new FileInputStream(zipFile);
			TarEntry tarEntity = new TarEntry(dirName+zipFile.getName());
			tarEntity.setSize(zipFile.length());
			//放入一个ZipEntry
			this.tarOut.putNextEntry(tarEntity);
			int length = 0;  
			//放入压缩文件的流
			while ((length = fileIn.read(this.buf)) > 0) {
				this.tarOut.write(this.buf, 0, length);
			}

			this.tarOut.closeEntry();
		}
		
	}
	/**
	 * 
	 * @param srcFile  需要　压缩的目录或者文件
	 * @param destFile　压缩文件的路径
	 */
	public void doTar(String srcFile, String destFile) {// destFile:需要压缩的文件夹名
		File tarFile = new File(srcFile);
		try {
			//生成TarOutputStream，会把压缩的内容全都通过这个输出流输出，最后写到压缩文件中去
			this.tarOut = new TarOutputStream(new BufferedOutputStream(
					new FileOutputStream(destFile)));
			//TarOutputStream就不能像ZipOutputStream一样可以设置一些注释之类的信息了
			//开始进入递归
			handleFile(tarFile, this.tarOut,"");
			//最后压缩完成后，关闭输出流
			this.tarOut.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	/**
	 * 解压指定tar文件
	 * @param unTarfile 压缩文件的路径
	 * @param destFile　　　解压到的目录　
	 */
	public void unTar(String unTarfile, String destSrc) {// unZipfileName需要解压的zip文件名
		FileOutputStream fileOut = null;
		TarInputStream inputStream = null;

		try {

			inputStream= new TarInputStream(new FileInputStream(new File(unTarfile)),this.bufSize);  
            
            createDirectory(destSrc,null);//创建输出目录  
              
            TarEntry entry = null;  
             System.out.println("tarIn.getRecordSize():"+inputStream.getRecordSize());
            while( (entry = inputStream.getNextEntry()) != null ){  
            	System.out.println("entry.getName():"+entry.getName());
                if(entry.isDirectory()){//是目录  
                      
                    createDirectory(destSrc,entry.getName());//创建空目录  
                      
                }else{//是文件  
                      
                    File tmpFile = new File(destSrc + File.separator + entry.getName());  
                      
                    createDirectory(tmpFile.getParent() + File.separator,null);//创建输出目录  
                      
	            	fileOut = new FileOutputStream(tmpFile);
	            	int length = 0;  
					while ((length = inputStream.read(this.buf)) != -1) {
						fileOut.write(this.buf, 0, length);
					}
					if(fileOut!=null)
					{
						try {
							fileOut.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
                } 
            }  
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally
		{
			if(inputStream!=null)
			{
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void createDirectory(String outputDir,String subDir){  
        
        File file = new File(outputDir);
          
        if(!(subDir == null || subDir.trim().equals(""))){//子目录不为空  
              
            file = new File(outputDir + "/" + subDir);  
        }  
          
        if(!file.exists()){  
              
            file.mkdirs();  
        }  
          
    }   
}
