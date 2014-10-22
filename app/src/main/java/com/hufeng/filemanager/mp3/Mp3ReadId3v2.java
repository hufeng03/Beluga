package com.hufeng.filemanager.mp3;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * <b>MP3的ID3V2信息解析类</b>
 * 
 * @author 席有芳
 * @QQ QQ:951868171
 * @version 1.0
 * @email xi_yf_001@126.com
 * */
public class Mp3ReadId3v2 {

	private InputStream mp3ips;
	public String encoding; // 预设编码为GBK
	private Id3v2Info info;

	public Mp3ReadId3v2(InputStream in) {
		this.mp3ips = in;
		info = new Id3v2Info("未知", "未知", "未知", null);
	}

	public void readId3v2() throws Exception {
		try {
			readId3v2(1024*100);		//读取前100KB
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * */
	public void readId3v2(int buffSize) throws Exception {
		try {
			if(buffSize > mp3ips.available()){
				buffSize = mp3ips.available();
			}
			byte[] buff = new byte[buffSize];
			mp3ips.read(buff, 0, buffSize);

			if (ByteUtil.indexOf("ID3".getBytes(), buff, 1, 512) == -1)
			{
				return;
				//throw new Exception("未发现ID3V2");
			}
			//获取头像
			if (ByteUtil.indexOf("APIC".getBytes(), buff, 1, 512) != -1) {
				int searLen = ByteUtil.indexOf(new byte[] { (byte) 0xFF,
						(byte) 0xFB }, buff);
				int imgStart = ByteUtil.indexOf(new byte[] { (byte) 0xFF,
						(byte) 0xD8 }, buff);
				int imgEnd = ByteUtil.lastIndexOf(new byte[] { (byte) 0xFF,
						(byte) 0xD9 }, buff, 1, searLen) + 2;
                if (imgEnd > imgStart && imgStart >= 0 && imgEnd <= buff.length) {
                    byte[] imgb = ByteUtil.cutBytes(imgStart, imgEnd, buff);
                    info.setApic(imgb);
                }
			}
			encoding = null;
//            encoding = "GB18030";//"UTF-8","GBK";
//            encoding = "GBK";
            byte[] tit2_buf = null, tpe1_buf = null, talb_buf = null;
            int tit2_len = 0, tpe1_len = 0, talb_len = 0;
			if (ByteUtil.indexOf("TIT2".getBytes(), buff, 1, 512) != -1) {
                tit2_buf = readInfo(buff, "TIT2");
                tit2_len = tit2_buf.length;
            }

			if (ByteUtil.indexOf("TPE1".getBytes(), buff, 1, 512) != -1) {
                tpe1_buf = readInfo(buff, "TPE1");
                tpe1_len = tpe1_buf.length;
            }

			if (ByteUtil.indexOf("TALB".getBytes(), buff, 1, 512) != -1) {
                talb_buf = readInfo(buff, "TALB");
                talb_len = talb_buf.length;
            }

            if(encoding==null)
            {
                byte[] data3 = new byte[tit2_len + tpe1_len + talb_len];
                if (tit2_len != 0) System.arraycopy(tit2_buf,0,data3,0,tit2_len);
                if (tpe1_len != 0) System.arraycopy(tpe1_buf,0,data3,tit2_len,tpe1_len);
                if (talb_len != 0) System.arraycopy(talb_buf,0,data3,tit2_len+tpe1_len,talb_len);

                UniversalDetector detector = new UniversalDetector(null);
                detector.handleData(data3, 0, data3.length);
                detector.dataEnd();
                encoding = detector.getDetectedCharset();
//                Log.i("Mp3ReadId3v2", "encoding is " + ((encoding == null)?"null":encoding));
                if (encoding == null) {
                    encoding = "GBK";
                }
            }

            String locale = Locale.getDefault().getLanguage();
            if (locale.equals(Locale.CHINESE.getLanguage()) /*&& Locale.getDefault().getCountry().equals(Locale.CHINA)*/) {
                if (!encoding.startsWith("UTF") && !encoding.startsWith("GB")) {
//                    Log.i("Mp3ReadId3v2", "change encoding from " + encoding +" to GBK");
                    encoding = "GBK";
                }
            }


            if (tpe1_buf != null) {
                info.setTpe1(new String(tpe1_buf, encoding));
                System.out.println("info:" + info.getTpe1());

            }
            if (tit2_buf != null) {
                info.setTit2(new String(tit2_buf, encoding));
                System.out.println("info:" + info.getTit2());
            }
            if (talb_buf != null) {
				info.setTalb(new String(talb_buf, encoding));
				System.out.println("info:" + info.getTalb());
			}



		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
			mp3ips.close();
		}

	}

	/**
	 *读取文本标签
	 **/
	private byte[] readInfo(byte[] buff, String tag) {
		int len = 0;
		int offset = ByteUtil.indexOf(tag.getBytes(), buff);
		len = buff[offset + 4] & 0xFF;
		len = (len << 8) + (buff[offset + 5] & 0xFF);
		len = (len << 8) + (buff[offset + 6] & 0xFF);
		len = (len << 8) + (buff[offset + 7] & 0xFF);
		len = len - 1;
		return ByteUtil.cutBytes(ByteUtil.indexOf(tag.getBytes(), buff) + 11,
				ByteUtil.indexOf(tag.getBytes(), buff) + 11 + len, buff);

	}

	public void setInfo(Id3v2Info info) {
		this.info = info;
	}

	public Id3v2Info getInfo() {
		return info;
	}

	public String getName() {
		return getInfo().getTit2();

	}

	public String getAuthor() {

		return getInfo().getTpe1();

	}

	public String getSpecial() {
		return getInfo().getTalb();
	}

	public byte[] getImg() {
		return getInfo().getApic();
	}
}
