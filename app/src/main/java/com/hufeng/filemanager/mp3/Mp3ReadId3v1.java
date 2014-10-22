package com.hufeng.filemanager.mp3;

import java.io.InputStream;

/*	
 * mgsform = new Form("Mp3信息");
 * mgsform.append("正在获取Mp3信息");
 * try {
 * fc = (FileConnection)Connector.open("file:///" + mp3path);
 * ips = fc.openInputStream();
 * mp = new Mp3Id3v1(ips);
 * mp.buff = 1024*100;  
 * mp.readId3v1();
 * mgsform.delete(0);
 * mgsform.append("歌曲名："+mp.getName()+"\n"+"艺术家："+mp.getAuthor()+"\n专辑："+mp.getSpecal()+"\n风格："+mp.getStyle());
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 **/
/**
 * 
 * <b>MP3的ID3V1信息解析类</b>
 * 
 * @author 席有芳
 * @QQ QQ:951868171
 * @version 1.0
 * @email xi_yf_001@126.com
 * 
 * */

public class Mp3ReadId3v1 implements Mp3Constants {

	private InputStream mp3ips;
	private String name = "";
	private String author = "";
	private String special = "";
	private String year = "";
	private String mark = "";
	private String style = "";
	private int index = 0;
	private int lg = 0;
	public String charset = "GBK"; // 预设编码为GBK

	public Mp3ReadId3v1(InputStream in) {
		this.mp3ips = in;

	}

	/**
	 * 读取Id3v1标签
	 **/
	public void readId3v1() {
		try {
			int reslen = mp3ips.available(); // 获取字节总长
			//跳转到后128字节
			mp3ips.skip(reslen - 128);
			// 读取TAG标签
			byte[] tag = new byte[TAG];
			mp3ips.read(tag);
			String str = new String(tag);
			if (str != "TAG") {
				System.out.println("No tag");
				throw new Exception("未发现ID3V1标签");

			}
			// 读取Mp3名称
			byte[] bytes = new byte[NAME];
			lg = 0;
			for (int i = 0; i < NAME; i++) {
				index = mp3ips.read();
				if (index != 0) {
					lg++; // 如果索引不等于0 ，那么字节长度+1
				}
				bytes[i] = (byte) index;
			}
			name = new String(bytes, 0, lg, charset);
			// 获取艺术家
			lg = 0; // 重设字节数
			for (int i = 0; i < AUTHOR; i++) {
				index = mp3ips.read();
				if (index != 0) {
					lg++;
				}
				bytes[i] = (byte) index;
			}
			author = new String(bytes, 0, lg, charset);
			// 获取专辑名
			lg = 0;
			for (int i = 0; i < SPECIAL; i++) {
				index = mp3ips.read();
				if (index != 0) {
					lg++;
				}
				bytes[i] = (byte) index;
			}
			special = new String(bytes, 0, lg, charset);
			// 获取歌曲年份
			lg = 0;
			for (int i = 0; i < YEAR; i++) {
				index = mp3ips.read();
				if (index != 0) {
					lg++;
				}
				bytes[i] = (byte) index;
			}
			year = new String(bytes, 0, lg, charset);
			// 读取附注信息
			lg = 0;
			for (int i = 0; i < MARK; i++) {
				index = mp3ips.read();
				if (index != 0) {
					lg++;
				}
				bytes[i] = (byte) index;
			}
			mark = new String(bytes, 0, lg, charset);
			// 读取Mp3风格
			for (int i = 0; i < STYLE; i++) {
				index = mp3ips.read();
			}
			mp3Style(index);

			mp3ips.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	/****** 获取歌曲名称 *****/
	public String getName() {

		return (name == null || name == "" || name.equals("")) ? "未知歌曲" : name;
	}

	/****** 获取艺术家 *******/
	public String getAuthor() {

		return (author == null || author == "" || author.equals("")) ? "未知艺术家"
				: author;
	}

	/****** 获取专辑名称 *****/
	public String getSpecial() {

		return (special == null || special == "" || special.equals("")) ? "未知专辑"
				: special;
	}

	/****** 获取年份信息 *****/
	public String getYear() {

		return (year == null || year == "" || year.equals("")) ? "未知年份" : year;
	}

	/****** 获取备注信息 *****/
	public String getMark() {

		return mark;
	}

	/****** 获取MP3风格 *****/
	public String getStyle() {

		return style;
	}

	// Mp3风格
	private void mp3Style(int index) {
		switch (index) {
		case 0:
			style = "Blues";
			break;
		case 1:
			style = "Rock";
			break;
		case 2:
			style = "Country";
			break;
		case 3:
			style = "Dance";
			break;
		case 4:
			style = "Disco";
			break;
		case 5:
			style = "Funk";
			break;
		case 6:
			style = "Grunge";
			break;
		case 7:
			style = "Hip-Hop";
			break;
		case 8:
			style = "Jazz";
			break;
		case 9:
			style = "Metal";
			break;
		case 10:
			style = "NewAge";
			break;
		case 11:
			style = "Oldies";
			break;
		case 12:
			style = "Other";
			break;
		case 13:
			style = "Pop";
			break;
		case 14:
			style = "R&B";
			break;
		case 15:
			style = "Rap";
			break;
		case 16:
			style = "Reggae";
			break;
		case 17:
			style = "Rock";
			break;
		case 18:
			style = "Techno";
			break;
		case 19:
			style = "Industrial";
			break;
		case 20:
			style = "Alternative";
			break;
		case 21:
			style = "Ska";
			break;
		case 22:
			style = "DeathMetal";
			break;
		case 23:
			style = "Pranks";
			break;
		case 24:
			style = "Soundtrack";
			break;
		case 25:
			style = "Euro-Techno";
			break;
		case 26:
			style = "Ambient";
			break;
		case 27:
			style = "Trip-Hop";
			break;
		case 28:
			style = "Vocal";
			break;
		case 29:
			style = "Jazz+Funk";
			break;
		case 30:
			style = "Fusion";
			break;
		case 31:
			style = "Trance";
			break;
		case 32:
			style = "Classical";
			break;
		case 33:
			style = "Instrumental";
			break;
		case 34:
			style = "Acid";
			break;
		case 35:
			style = "House";
			break;
		case 36:
			style = "Game";
			break;
		case 37:
			style = "SoundClip";
			break;
		case 38:
			style = "Gospel";
			break;
		case 39:
			style = "Noise";
			break;
		case 40:
			style = "AlternRock";
			break;
		case 41:
			style = "Bass";
			break;
		case 42:
			style = "Soul";
			break;
		case 43:
			style = "Punk";
			break;
		case 44:
			style = "Space";
			break;
		case 45:
			style = "Meditative";
			break;
		case 46:
			style = "InstrumentalPop";
			break;
		case 47:
			style = "InstrumentalRock";
			break;
		case 48:
			style = "Ethnic";
			break;
		case 49:
			style = "Gothic";
			break;
		case 50:
			style = "Darkwave";
			break;
		case 51:
			style = "Techno-Industrial";
			break;
		case 52:
			style = "Electronic";
			break;
		case 53:
			style = "Pop-Folk";
			break;
		case 54:
			style = "Eurodance";
			break;
		case 55:
			style = "Dream";
			break;
		case 56:
			style = "SouthernRock";
			break;
		case 57:
			style = "Cult";
			break;
		case 58:
			style = "Cult";
			break;
		case 59:
			style = "Gangsta";
			break;
		case 60:
			style = "Top40";
			break;
		case 61:
			style = "ChristianRap";
			break;
		case 62:
			style = "Pop/Funk";
			break;
		case 63:
			style = "Jungle";
			break;
		case 64:
			style = "NativeAmerican";
			break;
		case 65:
			style = "Cabaret";
			break;
		case 66:
			style = "NewWave";
			break;
		case 67:
			style = "Psychadelic";
			break;
		case 68:
			style = "Rave";
			break;
		case 69:
			style = "Showtunes";
			break;
		case 70:
			style = "Trailer";
			break;
		case 71:
			style = "Lo-Fi";
			break;
		case 72:
			style = "Tribal";
			break;
		case 73:
			style = "AcidPunk";
			break;
		case 74:
			style = "AcidJazz";
			break;
		case 75:
			style = "Polka";
			break;
		case 76:
			style = "Retro";
			break;
		case 77:
			style = "Musical";
			break;
		case 78:
			style = "Rock&Roll";
			break;
		case 79:
			style = "HardRock";
			break;
		case 80:
			style = "Folk";
			break;
		case 81:
			style = "Folk-Rock";
			break;
		case 82:
			style = "NationalFolk";
			break;
		case 83:
			style = "Swing";
			break;
		case 84:
			style = "FastFusion";
			break;
		case 85:
			style = "Bebob";
			break;
		case 86:
			style = "Latin";
			break;
		case 87:
			style = "Revival";
			break;
		case 88:
			style = "Celtic";
			break;
		case 89:
			style = "Bluegrass";
			break;
		case 90:
			style = "Avantgarde";
			break;
		case 91:
			style = "GothicRock";
			break;
		case 92:
			style = "ProgessiveRock";
			break;
		case 93:
			style = "PsychedelicRock";
			break;
		case 94:
			style = "SymphonicRock";
			break;
		case 95:
			style = "SlowRock";
			break;
		case 96:
			style = "BigBand";
			break;
		case 97:
			style = "Chorus";
			break;
		case 98:
			style = "EasyListening";
			break;
		case 99:
			style = "Acoustic";
			break;
		case 100:
			style = "Humour";
			break;
		case 101:
			style = "Speech";
			break;
		case 102:
			style = "Chanson";
			break;
		case 103:
			style = "Opera";
			break;
		case 104:
			style = "ChamberMusic";
			break;
		case 105:
			style = "Sonata";
			break;
		case 106:
			style = "Symphony";
			break;
		case 107:
			style = "BootyBass";
			break;
		case 108:
			style = "Primus";
			break;
		case 109:
			style = "PornGroove";
			break;
		case 110:
			style = "Satire";
			break;
		case 111:
			style = "SlowJam";
			break;
		case 112:
			style = "Club";
			break;
		case 113:
			style = "Tango";
			break;
		case 114:
			style = "Samba";
			break;
		case 115:
			style = "Folklore";
			break;
		case 116:
			style = "Ballad";
			break;
		case 117:
			style = "PowerBallad";
			break;
		case 118:
			style = "RhythmicSoul";
			break;
		case 119:
			style = "Freestyle";
			break;
		case 120:
			style = "Duet";
			break;
		case 121:
			style = "PunkRock";
			break;
		case 122:
			style = "DrumSolo";
			break;
		case 123:
			style = "Acapella";
			break;
		case 124:
			style = "Euro-House";
			break;
		case 125:
			style = "DanceHall";
			break;
		case 126:
			style = "Goa";
			break;
		case 127:
			style = "Drum&Bass";
			break;
		case 128:
			style = "Club-House";
			break;
		case 129:
			style = "Hardcore";
			break;
		case 130:
			style = "Terror";
			break;
		case 131:
			style = "Indie";
			break;
		case 132:
			style = "BritPop";
			break;
		case 133:
			style = "Negerpunk";
			break;
		case 134:
			style = "PolskPunk";
			break;
		case 135:
			style = "Beat";
			break;
		case 136:
			style = "ChristianGangstaRap";
			break;
		case 137:
			style = "HeavyMetal";
			break;
		case 138:
			style = "BlackMetal";
			break;
		case 139:
			style = "Crossover";
			break;
		case 140:
			style = "ContemporaryChristian";
			break;
		case 141:
			style = "ChristianRock";
			break;
		case 142:
			style = "Merengue";
			break;
		case 143:
			style = "Salsa";
			break;
		case 144:
			style = "TrashMetal";
			break;
		case 145:
			style = "Anime";
			break;
		case 146:
			style = "JPop";
			break;
		case 147:
			style = "Synthpop";
		default:
			style = "unknow";
		}
	}
}
