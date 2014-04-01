package com.hufeng.filemanager.provider;



import android.net.Uri;
import android.provider.BaseColumns;

public class DataStructures {
	
	public static final String AUTHORITY = "com.hufeng.filemanager";

	public static class CategoryColumns implements BaseColumns {
		
		public static final String TABLE = "category";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/" +TABLE;
		
		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String CATEGORY_FIELD = "category";
		
		public static final String SIZE_FIELD = "size";
		
		public static final String NUMBER_FIELD = "number";
		
		public static final String STORAGE_FIELD = "storage";
		
		public static final String DEFAULT_SORT_ORDER = "size asc";
		
		public static final String[] PROJECTION = new String[] {
			_ID, CATEGORY_FIELD, SIZE_FIELD, NUMBER_FIELD, STORAGE_FIELD};
		
		public static final int CATEGORY_FIELD_INDEX = 1;
		public static final int SIZE_FIELD_INDEX = 2;
		public static final int NUMBER_FIELD_INDEX = 3;
		public static final int STORAGE_FIELD_INDEX = 4;

		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +CATEGORY_FIELD + " INTEGER, "
                +SIZE_FIELD + " LONG, "
                +STORAGE_FIELD + " STRING, "
                +NUMBER_FIELD + " LONG);";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static class FileColumns implements BaseColumns {
		
        public static final String TABLE = "file";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String FILE_TYPE_FIELD = "type";
		
		public static final String FILE_PATH_FIELD = "path";
		
		public static final String FILE_NAME_FIELD = "name";
		
		public static final String FILE_SIZE_FIELD = "size";
		
		public static final String FILE_DATE_FIELD = "date";
		
		public static final String FILE_SYNC_FIELD = "sync";
		
		public static final String FILE_EXTENSION_FIELD = "extension";
		
		public static final String FILE_STORAGE_FIELD = "storage";
		
		public static final String DEFAULT_SORT_ORDER = "date desc";

		public static final String[] FILE_PROJECTION = new String[] {
			_ID, FILE_TYPE_FIELD, FILE_PATH_FIELD, FILE_NAME_FIELD, FILE_SIZE_FIELD, FILE_EXTENSION_FIELD, FILE_DATE_FIELD, FILE_STORAGE_FIELD, FILE_SYNC_FIELD};

		public static final int ID_FIELD_INDEX = 0;
		public static final int FILE_TYPE_FIELD_INDEX = 1;
		public static final int FILE_PATH_FIELD_INDEX = 2;
		public static final int FILE_NAME_FIELD_INDEX = 3;
		public static final int FILE_SIZE_FIELD_INDEX = 4;
		public static final int FILE_EXTENSION_FIELD_INDEX = 5;
		public static final int FILE_DATE_FIELD_INDEX = 6;
		public static final int FILE_STORAGE_FIELD_INDEX = 7;
		public static final int FILE_SYNC_FILED_INDEX = 8;
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +FILE_PATH_FIELD + " TEXT, "
                +FILE_NAME_FIELD + " TEXT, "
                +FILE_TYPE_FIELD + " TEXT, "
                +FILE_SIZE_FIELD + " LONG, "
                +FILE_DATE_FIELD + " LONG, "
                +FILE_EXTENSION_FIELD + " TEXT, "
                +FILE_STORAGE_FIELD + " TEXT, "
            	+FILE_SYNC_FIELD + " INTEGER, UNIQUE("+FileColumns.FILE_PATH_FIELD+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class ImageColumns extends FileColumns {
		
		public static String TABLE = "image";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

		public static final String IMAGE_WIDTH_FIELD = "image_width";
		
		public static final String IMAGE_HEIGHT_FIELD = "image_height";
		
		public static final int IMAGE_WIDTH_FIELD_INDEX = 9;
		public static final int IMAGE_HEIGHT_FIELD_INDEX = 10;
		
		public static final String[] IMAGE_PROJECTION =  new String[] {
			_ID, FILE_TYPE_FIELD, FILE_PATH_FIELD, FILE_NAME_FIELD, FILE_SIZE_FIELD, FILE_EXTENSION_FIELD, FILE_DATE_FIELD, FILE_STORAGE_FIELD, FILE_SYNC_FIELD, IMAGE_WIDTH_FIELD, IMAGE_HEIGHT_FIELD};
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +FILE_PATH_FIELD + " TEXT, "
                +FILE_TYPE_FIELD + " TEXT, "
                +FILE_NAME_FIELD + " TEXT, "
                +FILE_SIZE_FIELD + " LONG, "
                +FILE_DATE_FIELD + " LONG, "
                +FILE_STORAGE_FIELD + " TEXT, "
                +FILE_SYNC_FIELD + " INTEGER, "
                +IMAGE_WIDTH_FIELD + " INTEGER, "
                +IMAGE_HEIGHT_FIELD + " INTEGER, "
                +FILE_EXTENSION_FIELD + " TEXT, UNIQUE("+ImageColumns.FILE_PATH_FIELD+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class AudioColumns extends FileColumns {
		
        public static final String TABLE = "audio";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

		public static final String PLAY_DURATION_FIELD = "play_duration";
		
		public static final String ALBUM_FIELD = "album";
		
		public static final String SINGER_FIELD = "singer";
		
		public static final String TITLE_FIELD = "title";
		
		public static final String DEFAULT_SORT_ORDER = "name desc";
		
		public static final String[] AUDIO_PROJECTION =  new String[] {
			_ID, FILE_TYPE_FIELD, FILE_PATH_FIELD, FILE_NAME_FIELD, FILE_SIZE_FIELD, FILE_EXTENSION_FIELD, FILE_DATE_FIELD, FILE_STORAGE_FIELD, FILE_SYNC_FIELD, PLAY_DURATION_FIELD, SINGER_FIELD, ALBUM_FIELD, TITLE_FIELD };
	
		
		public static final int DURATION_FIELD_INDEX = 9;
		public static final int SINGER_FIELD_INDEX = 10;
		public static final int ALBUM_FIELD_INDEX = 11;
		public static final int TITLE_FIELD_INDEX = 12;
		public static final int NAME_FIELD_INDEX = 3;
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +FILE_PATH_FIELD + " TEXT, "
                +FILE_TYPE_FIELD + " TEXT, "
                +FILE_NAME_FIELD + " TEXT, "
                +FILE_SIZE_FIELD + " LONG, "
                +FILE_DATE_FIELD + " LONG, "
                +FILE_STORAGE_FIELD + " TEXT, "
                +FILE_SYNC_FIELD + " INTEGER, "
                +PLAY_DURATION_FIELD + " LONG, "
                +SINGER_FIELD + " TEXT, "
                +ALBUM_FIELD + " TEXT, "
                +TITLE_FIELD + " TEXT, "
                +FILE_EXTENSION_FIELD + " TEXT, UNIQUE("+AudioColumns.FILE_PATH_FIELD+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class VideoColumns extends FileColumns {
		
		public static final String TABLE = "video";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

		public static final String PLAY_DURATION_FIELD = "play_duration";
		
		public static final String[] VIDEO_PROJECTION =  new String[] {
			_ID, FILE_TYPE_FIELD, FILE_PATH_FIELD, FILE_NAME_FIELD, FILE_SIZE_FIELD, FILE_EXTENSION_FIELD, FILE_DATE_FIELD, FILE_SYNC_FIELD, FILE_STORAGE_FIELD, PLAY_DURATION_FIELD};
	
		public static final int DURATION_FIELD_INDEX = 9;
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +FILE_PATH_FIELD + " TEXT, "
                +FILE_TYPE_FIELD + " TEXT, "
                +FILE_NAME_FIELD + " TEXT, "
                +FILE_SIZE_FIELD + " LONG, "
                +FILE_DATE_FIELD + " LONG, "
                +FILE_STORAGE_FIELD + " TEXT, "
                +FILE_SYNC_FIELD + " INTEGER, "
                +PLAY_DURATION_FIELD + " LONG, "
                +FILE_EXTENSION_FIELD + " TEXT, UNIQUE("+VideoColumns.FILE_PATH_FIELD+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class ApkColumns extends FileColumns {
		
        public static final String TABLE = "apk";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String[] APK_PROJECTION =  new String[] {
			_ID, FILE_TYPE_FIELD, FILE_PATH_FIELD, FILE_NAME_FIELD, FILE_SIZE_FIELD, FILE_EXTENSION_FIELD, FILE_DATE_FIELD, FILE_STORAGE_FIELD, FILE_SYNC_FIELD};
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +FILE_PATH_FIELD + " TEXT, "
                +FILE_TYPE_FIELD + " TEXT, "
                +FILE_NAME_FIELD + " TEXT, "
                +FILE_SIZE_FIELD + " LONG, "
                +FILE_DATE_FIELD + " LONG, "
                +FILE_EXTENSION_FIELD + " TEXT, "
                +FILE_STORAGE_FIELD + " TEXT, "
                +FILE_SYNC_FIELD + " INTEGER, UNIQUE("+ApkColumns.FILE_PATH_FIELD+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}

    public static final class CloudBoxColumns extends FileColumns {

        public static final String TABLE = "cloudbox";

        public static String CONTENT_BOX = "content://" + AUTHORITY + "/" + TABLE;

        public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

        public static final String PARENT_FOLDER_FIELD = "parent_folder";
        public static final String IS_FOLDER_FIELD = "is_folder";
        public static final String HASH_FIELD = "hash";
        public static final String LOCAL_FILE_FIELD = "local_file";
        public static final String ICON_DATA_FIELD = "local_icon";

        public static final int PARENT_FOLDER_FIELD_INDEX = 9;
        public static final int IS_FOLDER_FIELD_INDEX = 10;
        public static final int HASH_FIELD_INDEX = 11;
        public static final int LOCAL_FILE_FIELD_INDEX = 12;
        public static final int LOCAL_ICON_FIELD_INDEX = 13;

        public static final String[] CLOUD_BOX_PROJECTION = new String[] {
            _ID, FILE_TYPE_FIELD, FILE_PATH_FIELD, FILE_NAME_FIELD, FILE_SIZE_FIELD, FILE_EXTENSION_FIELD, FILE_DATE_FIELD, FILE_STORAGE_FIELD, FILE_SYNC_FIELD,
            PARENT_FOLDER_FIELD, IS_FOLDER_FIELD, HASH_FIELD, LOCAL_FILE_FIELD, ICON_DATA_FIELD,
        };

        public static final class SQL {

            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                    +" (_id INTEGER PRIMARY KEY, "
                    +FILE_PATH_FIELD + " TEXT, "
                    +FILE_TYPE_FIELD + " TEXT, "
                    +FILE_NAME_FIELD + " TEXT, "
                    +FILE_SIZE_FIELD + " LONG, "
                    +FILE_DATE_FIELD + " LONG, "
                    +FILE_EXTENSION_FIELD + " TEXT, "
                    +FILE_STORAGE_FIELD + " TEXT, "
                    +FILE_SYNC_FIELD + " INTEGER, "
                    +PARENT_FOLDER_FIELD + " TEXT, "
                    +IS_FOLDER_FIELD + " INTEGER, "
                    +HASH_FIELD + " TEXT, "
                    +LOCAL_FILE_FIELD + " TEXT, "
                    + ICON_DATA_FIELD + " TEXT, UNIQUE("+FILE_PATH_FIELD+") "
                    + ");";

            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;

        }

    }

    public static final class SelectedColumns extends FileColumns {

        public static final String TABLE = "selected";

        public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

        public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

        public static final String URL_FIELD = "url";
        public static final String SERVER_NAME_FIELD = "server_name";
        public static final String PACKAGE_FIELD = "package";
        public static final String VERSION_FIELD = "version";
        public static final String VERSION_NAME_FIELD = "version_name";
        public static final String SERVER_DATE_FIELD = "sever_date";
        public static final String DESCRIPTION_FIELD = "description";
        public static final String APP_CATEGORY_FIELD = "app_category";
        public static final String ICON_FIELD = "icon";
        public static final String PHOTO_FIELD = "photo";

        public static final int URL_FILED_INDEX = 9;
        public static final int SERVER_NAME_FIELD_INDEX = 10;
        public static final int PACKAGE_FIELD_INDEX = 11;
        public static final int VERSION_FIELD_INDEX = 12;
        public static final int VERSION_NAME_FIELD_INDEX = 13;
        public static final int SERVER_DATE_FIELD_INDEX = 14;
        public static final int DESCRIPTION_FIELD_INDEX = 15;
        public static final int APP_CATEGORY_FIELD_INDEX = 16;
        public static final int ICON_FIELD_INDEX = 17;
        public static final int PHOTO_FIELD_INDEX = 18;

        public static final String DEFAULT_SORT_ORDER = "sever_date desc";

        public static final String[] SELECTED_PROJECTION =  new String[] {
                _ID, FILE_TYPE_FIELD, FILE_PATH_FIELD, FILE_NAME_FIELD, FILE_SIZE_FIELD, FILE_EXTENSION_FIELD, FILE_DATE_FIELD, FILE_STORAGE_FIELD, FILE_SYNC_FIELD,
            URL_FIELD, SERVER_NAME_FIELD, PACKAGE_FIELD, VERSION_FIELD, VERSION_NAME_FIELD, SERVER_DATE_FIELD, DESCRIPTION_FIELD, APP_CATEGORY_FIELD, ICON_FIELD, PHOTO_FIELD,
        };

        public static final class SQL {

            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                    +" (_id INTEGER PRIMARY KEY, "
                    +FILE_PATH_FIELD + " TEXT, "
                    +FILE_TYPE_FIELD + " TEXT, "
                    +FILE_NAME_FIELD + " TEXT, "
                    +FILE_SIZE_FIELD + " LONG, "
                    +FILE_DATE_FIELD + " LONG, "
                    +FILE_EXTENSION_FIELD + " TEXT, "
                    +FILE_STORAGE_FIELD + " TEXT, "
                    +FILE_SYNC_FIELD + " INTEGER, "
                    +URL_FIELD + " TEXT, "
                    +SERVER_NAME_FIELD + " TEXT, "
                    +PACKAGE_FIELD + " TEXT, "
                    +VERSION_FIELD + " INTEGER, "
                    +VERSION_NAME_FIELD + " TEXT, "
                    +SERVER_DATE_FIELD + " LONG, "
                    +DESCRIPTION_FIELD + " TEXT, "
                    +APP_CATEGORY_FIELD + " INTEGER, "
                    +ICON_FIELD + " TEXT, "
                    +PHOTO_FIELD + " TEXT, UNIQUE("+SelectedColumns.URL_FIELD+") "
                    + ");";

            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;

        }
    }
	
	public static final class DocumentColumns extends FileColumns {
		
        public static final String TABLE = "document";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String[] DOCUMENT_PROJECTION =  new String[] {
			_ID, FILE_TYPE_FIELD, FILE_PATH_FIELD, FILE_NAME_FIELD, FILE_SIZE_FIELD, FILE_EXTENSION_FIELD, FILE_DATE_FIELD, FILE_STORAGE_FIELD, FILE_SYNC_FIELD,};
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +FILE_PATH_FIELD + " TEXT, "
                +FILE_TYPE_FIELD + " TEXT, "
                +FILE_NAME_FIELD + " TEXT, "
                +FILE_SIZE_FIELD + " LONG, "
                +FILE_DATE_FIELD + " LONG, "
                +FILE_EXTENSION_FIELD + " TEXT, "
                +FILE_STORAGE_FIELD + " TEXT, "
            	+FILE_SYNC_FIELD + " INTEGER, UNIQUE("+DocumentColumns.FILE_PATH_FIELD+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class ZipColumns extends FileColumns {
		
        public static final String TABLE = "zip";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String DEFAULT_SORT_ORDER = "extension asc";
		
		public static final String[] ZIP_PROJECTION =  new String[] {
			_ID, FILE_TYPE_FIELD, FILE_PATH_FIELD, FILE_NAME_FIELD, FILE_SIZE_FIELD, FILE_EXTENSION_FIELD, FILE_DATE_FIELD, FILE_STORAGE_FIELD, FILE_SYNC_FIELD,};
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +FILE_PATH_FIELD + " TEXT, "
                +FILE_TYPE_FIELD + " TEXT, "
                +FILE_NAME_FIELD + " TEXT, "
                +FILE_SIZE_FIELD + " LONG, "
                +FILE_DATE_FIELD + " LONG, "
                +FILE_EXTENSION_FIELD + " TEXT, "
                +FILE_STORAGE_FIELD + " TEXT, "
                +FILE_SYNC_FIELD + " INTEGER, UNIQUE("+ZipColumns.FILE_PATH_FIELD+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	
    public static final class PreferenceColumns implements BaseColumns {

        public static final String TABLE = "preference";
        
        public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;
        
        public static final Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

        public static final String NAME = "name";
        public static final String VALUE = "value";

        public static final String DEFAULT_SORT_ORDER = "name asc";

        public static final String[] PREFERENCE_PROJECTION = new String[] {
                NAME, VALUE
        };
        
        public static final class SQL {
            
            public static final String CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE
            		+ " ("
            		+ PreferenceColumns.NAME + " TEXT PRIMARY KEY, "
                    + PreferenceColumns.VALUE + " TEXT, UNIQUE("+PreferenceColumns.NAME+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }

    }
    
	public static final class FavoriteColumns extends FileColumns {
		
        public static final String TABLE = "favorite";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String[] FAVORITE_PROJECTION =  new String[] {
			_ID, FILE_TYPE_FIELD, FILE_PATH_FIELD, FILE_NAME_FIELD, FILE_SIZE_FIELD, FILE_EXTENSION_FIELD, FILE_DATE_FIELD, FILE_STORAGE_FIELD, FILE_SYNC_FIELD,};
		
		public static final String IS_DIRECTORY_FIELD = "is_directory";
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +FILE_PATH_FIELD + " TEXT, "
                +FILE_TYPE_FIELD + " TEXT, "
                +FILE_NAME_FIELD + " TEXT, "
                +FILE_SIZE_FIELD + " LONG, "
                +FILE_DATE_FIELD + " LONG, "
                +IS_DIRECTORY_FIELD + " INTEGER, "
                +FILE_EXTENSION_FIELD + " TEXT, "
                +FILE_STORAGE_FIELD + " TEXT, "
            	+FILE_SYNC_FIELD + " INTEGER, UNIQUE("+FavoriteColumns.FILE_PATH_FIELD+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class MatchColumns implements BaseColumns {
		
        public static final String TABLE = "match";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String EXTENSION_FIELD = "extension";
		
		public static final String CATEGORY_FIELD = "category";
		
		public static final String APP_FIELD = "app";
		
		public static final String DATE_FIELD = "date";
		
		public static final String DEFAULT_SORT_ORDER = "extension asc";
		
		public static final String[] MATCH_PROJECTION =  new String[] {
			EXTENSION_FIELD, CATEGORY_FIELD, APP_FIELD, DATE_FIELD};
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" ("
                +EXTENSION_FIELD + " TEXT PRIMARY KEY, "
                +CATEGORY_FIELD + " INTEGER, "
                +APP_FIELD + " TEXT, "
                +DATE_FIELD + " LONG);";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
    

}
