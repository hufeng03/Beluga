package com.hufeng.filemanager.browser;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;

public class FileSorter {

    private static final String TAG = FileSorter.class.getSimpleName();
    
    public static class SORTER {
        public SORT_FIELD field;
        public SORT_ORDER order;

        public SORTER(SORT_FIELD field, SORT_ORDER order) {
            this.field = field;
            this.order = order;
        }
    }

    public enum SORT_FIELD{
        DATE, NAME, SIZE, EXTENSION;

        public static SORT_FIELD valueOf(int ordinal) {
            if (ordinal >= 0 && ordinal < SORT_FIELD.values().length) {
                return SORT_FIELD.values()[ordinal];
            }
            return null;
        }
    }

    public enum SORT_ORDER {
        ASC, DESC;
        
        public static SORT_ORDER valueOf(int ordinal) {
            if (ordinal >= 0 && ordinal < SORT_ORDER.values().length) {
                return SORT_ORDER.values()[ordinal];
            }
            return null;
        }
    }
    
    private static final String IMAGE_SORT_FIELD_KEY = "image_sort_field_key";
    private static final String AUDIO_SORT_FIELD_KEY = "audio_sort_field_key";
    private static final String VIDEO_SORT_FIELD_KEY = "video_sort_field_key";
    private static final String APK_SORT_FIELD_KEY = "apk_sort_field_key";
    private static final String ZIP_SORT_FIELD_KEY = "zip_sort_field_key";
    private static final String DOCUMENT_SORT_FIELD_KEY = "document_sort_field_key";
    private static final String FILE_SORT_FIELD_KEY = "file_sort_field_key";
    private static final String APP_SORT_FIELD_KEY = "app_sort_field_key";
    private static final String GAME_SORT_FIELD_KEY = "game_sort_field_key";
    private static final String CLOUD_SORT_FIELD_KEY = "cloud_sort_field_key";


    private static final String IMAGE_SORT_ORDER_KEY = "image_sort_order_key";
    private static final String AUDIO_SORT_ORDER_KEY = "audio_sort_order_key";
    private static final String VIDEO_SORT_ORDER_KEY = "video_sort_order_key";
    private static final String APK_SORT_ORDER_KEY = "apk_sort_order_key";
    private static final String ZIP_SORT_ORDER_KEY = "zip_sort_order_key";
    private static final String DOCUMENT_SORT_ORDER_KEY = "document_sort_order_key";
    private static final String FILE_SORT_ORDER_KEY = "file_sort_order_key";
    private static final String APP_SORT_ORDER_KEY = "app_sort_order_key";
    private static final String GAME_SORT_ORDER_KEY = "game_sort_order_key";
    private static final String CLOUD_SORT_ORDER_KEY = "cloud_sort_order_key";
    
    private static final int IMAGE_SORT_FIELD_DEFAULT_VALUE = SORT_FIELD.DATE.ordinal();
    private static final int IMAGE_SORT_ORDER_DEFAULT_VALUE = SORT_ORDER.DESC.ordinal();
    private static final int AUDIO_SORT_FIELD_DEFAULT_VALUE = SORT_FIELD.DATE.ordinal();
    private static final int AUDIO_SORT_ORDER_DEFAULT_VALUE = SORT_ORDER.DESC.ordinal();
    private static final int VIDEO_SORT_FIELD_DEFAULT_VALUE = SORT_FIELD.DATE.ordinal();
    private static final int VIDEO_SORT_ORDER_DEFAULT_VALUE = SORT_ORDER.DESC.ordinal();
    private static final int APK_SORT_FIELD_DEFAULT_VALUE = SORT_FIELD.DATE.ordinal();
    private static final int APK_SORT_ORDER_DEFAULT_VALUE = SORT_ORDER.DESC.ordinal();
    private static final int ZIP_SORT_FIELD_DEFAULT_VALUE = SORT_FIELD.DATE.ordinal();
    private static final int ZIP_SORT_ORDER_DEFAULT_VALUE = SORT_ORDER.DESC.ordinal();
    private static final int DOCUMENT_SORT_FIELD_DEFAULT_VALUE = SORT_FIELD.DATE.ordinal();
    private static final int DOCUMENT_SORT_ORDER_DEFAULT_VALUE = SORT_ORDER.DESC.ordinal();
    private static final int FILE_SORT_FIELD_DEFAULT_VALUE = SORT_FIELD.NAME.ordinal();
    private static final int FILE_SORT_ORDER_DEFAULT_VALUE = SORT_ORDER.ASC.ordinal();
    private static final int APP_SORT_FIELD_DEFAULT_VALUE = SORT_FIELD.DATE.ordinal();
    private static final int APP_SORT_ORDER_DEFAULT_VALUE = SORT_ORDER.DESC.ordinal();
    private static final int GAME_SORT_FIELD_DEFAULT_VALUE = SORT_FIELD.NAME.ordinal();
    private static final int GAME_SORT_ORDER_DEFAULT_VALUE = SORT_ORDER.ASC.ordinal();
    private static final int CLOUD_SORT_FIELD_DEFAULT_VALUE = SORT_FIELD.DATE.ordinal();
    private static final int CLOUD_SORT_ORDER_DEFAULT_VALUE = SORT_ORDER.DESC.ordinal();

    public static void saveFileSorter(Context context, int type, SORTER sorter) {
        String field_key, order_key;
        switch(type) {
            case FileUtils.FILE_TYPE_IMAGE:
                field_key = IMAGE_SORT_FIELD_KEY;
                order_key = IMAGE_SORT_ORDER_KEY;
                break;
            case FileUtils.FILE_TYPE_AUDIO:
                field_key = AUDIO_SORT_FIELD_KEY;
                order_key = AUDIO_SORT_ORDER_KEY;
                break;
            case FileUtils.FILE_TYPE_VIDEO:
                field_key = VIDEO_SORT_FIELD_KEY;
                order_key = VIDEO_SORT_ORDER_KEY;
                break;
            case FileUtils.FILE_TYPE_APK:
                field_key = APK_SORT_FIELD_KEY;
                order_key = APK_SORT_ORDER_KEY;
                break;
            case FileUtils.FILE_TYPE_DOCUMENT:
                field_key = DOCUMENT_SORT_FIELD_KEY;
                order_key = DOCUMENT_SORT_ORDER_KEY;
                break;
            case FileUtils.FILE_TYPE_ZIP:
                field_key = ZIP_SORT_FIELD_KEY;
                order_key = ZIP_SORT_ORDER_KEY;
                break;
            case FileUtils.FILE_TYPE_APP:
                field_key = APP_SORT_FIELD_KEY;
                order_key = APP_SORT_ORDER_KEY;
                break;
            case FileUtils.FILE_TYPE_RESOURCE_GAME:
                field_key = GAME_SORT_FIELD_KEY;
                order_key = GAME_SORT_ORDER_KEY;
                break;
            case FileUtils.FILE_TYPE_CLOUD:
                field_key = CLOUD_SORT_FIELD_KEY;
                order_key = CLOUD_SORT_ORDER_KEY;
                break;
            default:
                field_key = FILE_SORT_FIELD_KEY;
                order_key = FILE_SORT_ORDER_KEY;
                break;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int sort_field = sorter.field.ordinal();
        int sort_order = sorter.order.ordinal();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(field_key, sort_field);
        editor.putInt(order_key, sort_order);
        editor.commit();
    }
    
    public static SORTER getFileSorter(Context context, int type) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int sort_field, sort_order;
        switch(type) {
            case FileUtils.FILE_TYPE_IMAGE:
                sort_field = preferences.getInt(IMAGE_SORT_FIELD_KEY, IMAGE_SORT_FIELD_DEFAULT_VALUE);
                sort_order = preferences.getInt(IMAGE_SORT_ORDER_KEY, IMAGE_SORT_ORDER_DEFAULT_VALUE);
                break;
            case FileUtils.FILE_TYPE_AUDIO:
                sort_field = preferences.getInt(AUDIO_SORT_FIELD_KEY, AUDIO_SORT_FIELD_DEFAULT_VALUE);
                sort_order = preferences.getInt(AUDIO_SORT_ORDER_KEY, AUDIO_SORT_ORDER_DEFAULT_VALUE);
                break;
            case FileUtils.FILE_TYPE_VIDEO:
                sort_field = preferences.getInt(VIDEO_SORT_FIELD_KEY, VIDEO_SORT_FIELD_DEFAULT_VALUE);
                sort_order = preferences.getInt(VIDEO_SORT_ORDER_KEY, VIDEO_SORT_ORDER_DEFAULT_VALUE);
                break;
            case FileUtils.FILE_TYPE_APK:
                sort_field = preferences.getInt(APK_SORT_FIELD_KEY, APK_SORT_FIELD_DEFAULT_VALUE);
                sort_order = preferences.getInt(APK_SORT_ORDER_KEY, APK_SORT_ORDER_DEFAULT_VALUE);
                break;
            case FileUtils.FILE_TYPE_DOCUMENT:
                sort_field = preferences.getInt(DOCUMENT_SORT_FIELD_KEY, DOCUMENT_SORT_FIELD_DEFAULT_VALUE);
                sort_order = preferences.getInt(DOCUMENT_SORT_ORDER_KEY, DOCUMENT_SORT_ORDER_DEFAULT_VALUE);
                break;
            case FileUtils.FILE_TYPE_ZIP:
                sort_field = preferences.getInt(ZIP_SORT_FIELD_KEY, ZIP_SORT_FIELD_DEFAULT_VALUE);
                sort_order = preferences.getInt(ZIP_SORT_ORDER_KEY, ZIP_SORT_ORDER_DEFAULT_VALUE);
                break;
            case FileUtils.FILE_TYPE_APP:
                sort_field = preferences.getInt(APP_SORT_FIELD_KEY, APP_SORT_FIELD_DEFAULT_VALUE);
                sort_order = preferences.getInt(APP_SORT_ORDER_KEY, APP_SORT_ORDER_DEFAULT_VALUE);
                break;
            case FileUtils.FILE_TYPE_RESOURCE_GAME:
                sort_field = preferences.getInt(GAME_SORT_FIELD_KEY, GAME_SORT_FIELD_DEFAULT_VALUE);
                sort_order = preferences.getInt(GAME_SORT_ORDER_KEY, GAME_SORT_ORDER_DEFAULT_VALUE);
                break;
            case FileUtils.FILE_TYPE_CLOUD:
                sort_field = preferences.getInt(CLOUD_SORT_FIELD_KEY, CLOUD_SORT_FIELD_DEFAULT_VALUE);
                sort_order = preferences.getInt(CLOUD_SORT_ORDER_KEY, CLOUD_SORT_ORDER_DEFAULT_VALUE);
                break;
            default:
                sort_field = preferences.getInt(FILE_SORT_FIELD_KEY, FILE_SORT_FIELD_DEFAULT_VALUE);
                sort_order = preferences.getInt(FILE_SORT_ORDER_KEY, FILE_SORT_ORDER_DEFAULT_VALUE);
                break;
        }
        return new SORTER(SORT_FIELD.valueOf(sort_field), SORT_ORDER.valueOf(sort_order));
    }


    public static Comparator<File> getFileComparator(final SORT_FIELD field, final SORT_ORDER order) {
        switch(field){
            case NAME:
                if(SORT_ORDER.ASC == order){
                    return FILE_COMPARATOR_NAME_ASC;
                }else{
                    return FILE_COMPARATOR_NAME_DESC;
                }
            case EXTENSION:
                if(SORT_ORDER.ASC == order){
                    return FILE_COMPARATOR_EXTENSION_ASC;
                }else{
                    return FILE_COMPARATOR_EXTENSION_DESC;
                }
            case SIZE:
                if(SORT_ORDER.ASC == order){
                    return FILE_COMPARATOR_SIZE_ASC;
                }else{
                    return FILE_COMPARATOR_SIZE_DESC;
                }
            case DATE:
            default:
                if(SORT_ORDER.ASC == order){
                    return FILE_COMPARATOR_DATE_ASC;
                }else{
                    return FILE_COMPARATOR_DATE_DESC;
                }
        }
    }

	public static Comparator<FileEntry> getComparator(final SORT_FIELD field, final SORT_ORDER order){
		switch(field){
		case NAME:
			if(SORT_ORDER.ASC == order){
				return COMPARATOR_NAME_ASC;
			}else{
				return COMPARATOR_NAME_DESC;
			}
		case EXTENSION:
			if(SORT_ORDER.ASC == order){
				return COMPARATOR_EXTENSION_ASC;
			}else{
				return COMPARATOR_EXTENSION_DESC;
			}
		case SIZE:
			if(SORT_ORDER.ASC == order){
				return COMPARATOR_SIZE_ASC;
			}else{
				return COMPARATOR_SIZE_DESC;
			}
		case DATE:
		default:
			if(SORT_ORDER.ASC == order){
				return COMPARATOR_DATE_ASC;
			}else{
				return COMPARATOR_DATE_DESC;
			}
		}
	}

    private static final Collator sCollator = Collator.getInstance();

    public static final Comparator<File> FILE_COMPARATOR_NAME_ASC = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            return sCollator.compare(lhs.getName(), rhs.getName());
        }
    };


    public static final Comparator<File> FILE_COMPARATOR_NAME_DESC = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            return sCollator.compare(rhs.getName(), lhs.getName());
        }
    };
	
	public static final Comparator<FileEntry> COMPARATOR_NAME_ASC = new Comparator<FileEntry>() {
		@Override
		public int compare(FileEntry lhs, FileEntry rhs) {
            Log.i(TAG, "comparing " + lhs + ", " + rhs);
            return sCollator.compare(lhs.getName(), rhs.getName());
	    }
	};

    public static final Comparator<File> FILE_COMPARATOR_EXTENSION_ASC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            String lhs_name = lhs.getName().toLowerCase();
            String rhs_name = rhs.getName().toLowerCase();
            return compare_extension_asc(lhs_name, rhs_name, lhs.isDirectory(), rhs.isDirectory());
        }

    };

    public static Comparator<File> FILE_COMPARATOR_EXTENSION_DESC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            // TODO Auto-generated method stub
            String lhs_name = lhs.getName().toLowerCase();
            String rhs_name = rhs.getName().toLowerCase();
            return compare_extension_desc (lhs_name, rhs_name, lhs.isDirectory(), rhs.isDirectory());
        }

    };


    public static Comparator<File> FILE_COMPARATOR_SIZE_ASC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            // TODO Auto-generated method stub
            long lhs_size = lhs.length();
            long rhs_size = rhs.length();

            return lhs_size>rhs_size?1:(lhs_size<rhs_size?-1:0);
        }

    };


    public static Comparator<File> FILE_COMPARATOR_SIZE_DESC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            // TODO Auto-generated method stub
            long lhs_size = lhs.length();
            long rhs_size = rhs.length();

            return rhs_size>lhs_size?1:(rhs_size<lhs_size?-1:0);
        }

    };


    public static Comparator<File> FILE_COMPARATOR_DATE_ASC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            // TODO Auto-generated method stub
            long lhs_date = lhs.lastModified();
            long rhs_date = rhs.lastModified();

            return lhs_date>rhs_date?1:(lhs_date<rhs_date?-1:0);
        }

    };

    public static Comparator<File> FILE_COMPARATOR_DATE_DESC = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            // TODO Auto-generated method stub
            long lhs_date = lhs.lastModified();
            long rhs_date = rhs.lastModified();

            return rhs_date>lhs_date?1:(rhs_date<lhs_date?-1:0);
        }

    };


    public static final Comparator<FileEntry> COMPARATOR_NAME_DESC = new Comparator<FileEntry>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(FileEntry lhs, FileEntry rhs) {
            return sCollator.compare(rhs.getName(), lhs.getName());
        }
    };
	

	public static final Comparator<FileEntry> COMPARATOR_EXTENSION_ASC = new Comparator<FileEntry>() {
			
			@Override
			public int compare(FileEntry lhs, FileEntry rhs) {
				String lhs_name = lhs.getName().toLowerCase();
				String rhs_name = rhs.getName().toLowerCase();
                return compare_extension_asc(lhs_name, rhs_name, lhs.is_directory, rhs.is_directory);
			}
        	
    };

    public static Comparator<FileEntry> COMPARATOR_EXTENSION_DESC = new Comparator<FileEntry>() {

        @Override
        public int compare(FileEntry lhs, FileEntry rhs) {
            // TODO Auto-generated method stub
            String lhs_name = lhs.getName().toLowerCase();
            String rhs_name = rhs.getName().toLowerCase();
            return compare_extension_desc (lhs_name, rhs_name, lhs.is_directory, rhs.is_directory);
        }

    };


    public static Comparator<FileEntry> COMPARATOR_SIZE_ASC = new Comparator<FileEntry>() {

        @Override
        public int compare(FileEntry lhs, FileEntry rhs) {
            // TODO Auto-generated method stub
            long lhs_size = lhs.length();
            long rhs_size = rhs.length();

            return lhs_size>rhs_size?1:(lhs_size<rhs_size?-1:0);
        }

    };


    public static Comparator<FileEntry> COMPARATOR_SIZE_DESC = new Comparator<FileEntry>() {

        @Override
        public int compare(FileEntry lhs, FileEntry rhs) {
            // TODO Auto-generated method stub
            long lhs_size = lhs.length();
            long rhs_size = rhs.length();

            return rhs_size>lhs_size?1:(rhs_size<lhs_size?-1:0);
        }

    };


    public static Comparator<FileEntry> COMPARATOR_DATE_ASC = new Comparator<FileEntry>() {

        @Override
        public int compare(FileEntry lhs, FileEntry rhs) {
            // TODO Auto-generated method stub
            long lhs_date = lhs.lastModified();
            long rhs_date = rhs.lastModified();

            return lhs_date>rhs_date?1:(lhs_date<rhs_date?-1:0);
        }

    };

    public static Comparator<FileEntry> COMPARATOR_DATE_DESC = new Comparator<FileEntry>() {

        @Override
        public int compare(FileEntry lhs, FileEntry rhs) {
            // TODO Auto-generated method stub
            long lhs_date = lhs.lastModified();
            long rhs_date = rhs.lastModified();

            return rhs_date>lhs_date?1:(rhs_date<lhs_date?-1:0);
        }

    };

    public static int compare_extension_asc (String lhs_name, String rhs_name, boolean lhs_directory, boolean rhs_directory) {
        int lhs_idx = lhs_name.lastIndexOf(".");
        int rhs_idx = rhs_name.lastIndexOf(".");
        String lhs_extension = (lhs_idx==-1)?"":lhs_name.substring(lhs_idx+1);
        String rhs_extension = (rhs_idx==-1)?"":rhs_name.substring(rhs_idx+1);
        int result = 0;
        if(rhs_directory && lhs_directory){
            if(lhs_name.startsWith(".") && !rhs_name.startsWith(".")){
                result = 1;
            }else if(rhs_name.startsWith(".") && !lhs_name.startsWith(".")){
                result = -1;
            }else{
                result = lhs_name.compareTo(rhs_name);
            }
        } else if(lhs_directory){
            result =  1;
        } else if(rhs_directory){
            result = -1;
        } else {
            if(TextUtils.isEmpty(lhs_extension) && !TextUtils.isEmpty(rhs_extension)){
                result = -1;
            }else if(TextUtils.isEmpty(rhs_extension) && !TextUtils.isEmpty(lhs_extension)){
                result = 1;
            }else{
                result = lhs_extension.compareTo(rhs_extension);
            }
        }
        return result;
    }

    public static int compare_extension_desc (String lhs_name, String rhs_name, boolean lhs_directory, boolean rhs_directory) {
        int lhs_idx = lhs_name.lastIndexOf(".");
        int rhs_idx = rhs_name.lastIndexOf(".");
        String lhs_extension = (lhs_idx==-1)?"":lhs_name.substring(lhs_idx+1);
        String rhs_extension = (rhs_idx==-1)?"":rhs_name.substring(rhs_idx+1);
        if(rhs_directory && lhs_directory){
            if(lhs_name.startsWith(".") && !rhs_name.startsWith(".")){
                return 1;
            }else if(rhs_name.startsWith(".") && !lhs_name.startsWith(".")){
                return -1;
            }else{
                return lhs_name.compareTo(rhs_name);
            }
        } else if(lhs_directory){
            return 1;
        } else if(rhs_directory){
            return -1;
        } else {
            if(TextUtils.isEmpty(lhs_extension) && !TextUtils.isEmpty(rhs_extension)){
                return 1;
            }else if(TextUtils.isEmpty(rhs_extension) && !TextUtils.isEmpty(lhs_extension)){
                return -1;
            }else{
                return (0-lhs_extension.compareTo(rhs_extension));
            }
        }
    }


	



}
