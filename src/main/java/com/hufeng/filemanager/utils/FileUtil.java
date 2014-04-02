package com.hufeng.filemanager.utils;

import java.io.File;

public class FileUtil {
	
	private static final String LOG_TAG = FileUtil.class.getName();
	
    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = M * K;

    public static String normalize(long data) {
        if (data > G)
            return String.format("%.2fG", data / (double)G);
        else if (data > M)
            return String.format("%.2fM", data / (double)M);
        else if (data > K)
            return String.format("%.2fK", data / (double)K);
        else
            return String.valueOf(data + "B");
    }

    /* 判断文件MimeType的method */
    /* 判断文件MimeType的method */
    public static String getMIMEType(File f) 
    { 
        String type="";
        String fName=f.getName();
        /* 取得扩展名 */
        String end=fName.substring(fName.lastIndexOf(".")
                +1,fName.length()).toLowerCase(); 

        /* 依扩展名的类型决定MimeType */
        if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                end.equals("xmf")||end.equals("ogg")||end.equals("wav") ||
                end.equals("m3u") || end.equals("m4b") || end.equals("m4p") || 
                end.equals("mp2") || end.equals("mp3") || end.equals("mpga") ||
                end.equals("ogg") || end.equals("wma") || end.equals("ape") ||
                end.equals("flac") || end.equals("amr") || end.equals("aac") ||
                end.equals("imy") || end.equals("mmf") || end.equals("3gpp") || end.equals("awb")
        		)
        {
            type = "audio/*"; 
        }
        else if(end.equals("3gp")||end.equals("mp4") || end.equals("avi") || end.equals("asf") ||
        		end.equals("m4u") || end.equals("m4v") || end.equals("mov") || end.equals("mpe") ||
        		end.equals("mpeg") || end.equals("mpg") || end.equals("mpg4") || end.equals("rmvb") || 
        		end.equals("wmv") || end.equals("xv") || end.equals("rm")
        		)
        {
            type = "video/*";
        }
        else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
                end.equals("jpeg")||end.equals("bmp") || end.equals("tif") || end.equals("mpo") || end.equals("wbmp")
        		)
        {
            type = "image/*";
        }
        else if(end.equals("apk")) 
        { 
            /* android.permission.INSTALL_PACKAGES */ 
            type = "application/vnd.android.package-archive"; 
        } 
        else if(end.equals("txt") || end.equals("log"))
        {
        	type = "text/plain";
        }
        else if(end.equals("html") || end.equals("htm") || end.equals("xml") || end.equals("xhtml"))
        {
        	type = "text/html";
        }
        else if(end.equals("pdf"))
        {
        	type = "application/pdf";
        }
        else if(end.equals("epub"))
        {
        	type = "application/epub+zip";
        }
        else if(end.equals("chm"))
        {
        	type = "application/x-chm";
        }
        else if(end.equals("doc") || end.equals("docx"))
        {
        	type = "application/msword";
        }
        else if(end.equals("xls") || end.equals("xlsx"))
        {
        	type = "application/vnd.ms-excel";
        }
        else if(end.endsWith("ppt") || end.equals("pptx"))
        {
        	type = "application/vnd.ms-powerpoint";
        }
        else if(end.equals("zip") || end.equals("tar") || end.equals("gz") ||
        		end.equals("rar") || end.equals("7z") || end.equals("z"))
        {
        	type = "application/zip";
        }
        else
        {
            type="*/*";
        }

        return type;  
    }




}
