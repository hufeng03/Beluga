package com.hufeng.playzip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.Deflater;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 压缩或者是解压zip的工具类
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @since 2011-10-12 下午9:51:57
 * @version 1.0.0
 */
public class ZipUtil {
	private ZipFile         zipFile; 
    private ZipOutputStream zipOut;     //压缩Zip 
    private  int      		bufSize;    //size of bytes 
    private byte[]          buf; 

    public ZipUtil(){
    	//要构造函数中去初始化我们的缓冲区
        this.bufSize = 1024*4; 
        this.buf = new byte[this.bufSize]; 
    } 
     
	/**
	 * 对传入的目录或者是文件进行压缩
	 * @param srcFile  需要　压缩的目录或者文件
	 * @param destFile　压缩文件的路径
	 */
	public void doZip(String srcFile, String destFile) {// zipDirectoryPath:需要压缩的文件夹名
		File zipFile = new File(srcFile);
		try {
			//生成ZipOutputStream，会把压缩的内容全都通过这个输出流输出，最后写到压缩文件中去
			this.zipOut = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(destFile)));
			//设置压缩的注释
			zipOut.setComment("comment");
			//设置压缩的编码，如果要压缩的路径中有中文，就用下面的编码
			zipOut.setEncoding("GBK");
			//启用压缩 
			zipOut.setMethod(ZipOutputStream.DEFLATED); 

			//压缩级别为最强压缩，但时间要花得多一点 
			zipOut.setLevel(Deflater.BEST_COMPRESSION); 
			
			handleFile(zipFile, this.zipOut,"");
			//处理完成后关闭我们的输出流
			this.zipOut.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 *  由doZip调用,递归完成目录文件读取
	 * @param zipFile
	 * @param zipOut
	 * @param dirName  这个主要是用来记录压缩文件的一个目录层次结构的
	 * @throws IOException
	 */
	private void handleFile(File zipFile, ZipOutputStream zipOut,String dirName) throws IOException {
		System.out.println("遍历文件："+zipFile.getName());
		//如果是一个目录，则遍历
		if(zipFile.isDirectory())
		{
			File[] files = zipFile.listFiles();

			if (files.length == 0) {// 如果目录为空,则单独创建之.
				//只是放入了空目录的名字
				this.zipOut.putNextEntry(new ZipEntry(dirName+zipFile.getName()+File.separator));
				this.zipOut.closeEntry();
			} else {// 如果目录不为空,则进入递归，处理下一级文件
				for (File file : files) {
					// 进入递归，处理下一级的文件
					handleFile(file, zipOut, dirName+zipFile.getName()+File.separator);
				}
			}
		}
		//如果是文件，则直接压缩
		else
		{
			FileInputStream fileIn  = new FileInputStream(zipFile);
			//放入一个ZipEntry
			this.zipOut.putNextEntry(new ZipEntry(dirName+zipFile.getName()));
			int length = 0;
			//放入压缩文件的流
			while ((length = fileIn.read(this.buf)) > 0) {
				this.zipOut.write(this.buf, 0, length);
			}
			//关闭ZipEntry，完成一个文件的压缩
			this.zipOut.closeEntry();
		}
		
	}

	/**
	 * 解压指定zip文件
	 * @param unZipfile 压缩文件的路径
	 * @param destFile　　　解压到的目录　
	 */
	public void unZip(String unZipfile, String destFile) {// unZipfileName需要解压的zip文件名
		FileOutputStream fileOut;
		File file;
		InputStream inputStream;

		try {
			//生成一个zip的文件
			this.zipFile = new ZipFile(unZipfile);
			//遍历zipFile中所有的实体，并把他们解压出来
			for (@SuppressWarnings("unchecked")
			Enumeration<ZipEntry> entries = this.zipFile.getEntries(); entries
					.hasMoreElements();) {
				ZipEntry entry =  entries.nextElement();
				//生成他们解压后的一个文件
				file = new File(destFile+File.separator+entry.getName());

				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					// 如果指定文件的目录不存在,则创建之.
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
					//获取出该压缩实体的输入流
					inputStream = zipFile.getInputStream(entry);

					fileOut = new FileOutputStream(file);
					int length = 0;
					//将实体写到本地文件中去
					while ((length = inputStream.read(this.buf)) > 0) {
						fileOut.write(this.buf, 0, length);
					}
					fileOut.close();
					inputStream.close();
				}
			}
			this.zipFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
