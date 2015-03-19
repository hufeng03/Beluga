package com.belugamobile.filemanager.provider;



import android.net.Uri;
import android.provider.BaseColumns;

public class DataStructures {
	
	public static final String AUTHORITY = "com.belugamobile.filemanager";

	public static class CategoryColumns implements BaseColumns {
		
		public static final String TABLE = "category";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/" +TABLE;
		
		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

		public static final String CATEGORY = "category";
        public static final String SIZE = "size";
        public static final String NUMBER = "number";
        public static final String STORAGE = "storage";
        public static final String DEFAULT_SORT_ORDER = "size asc";
		
		public static final String[] PROJECTION = new String[] {
			_ID, CATEGORY, SIZE, NUMBER, STORAGE};
		
		public static final int CATEGORY_INDEX = 1;
		public static final int SIZE_INDEX = 2;
		public static final int NUMBER_INDEX = 3;
		public static final int STORAGE_INDEX = 4;

		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +CATEGORY + " INTEGER, "
                +SIZE + " LONG, "
                +STORAGE + " STRING, "
                +NUMBER + " LONG);";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static class FileColumns implements BaseColumns {
		
        public static final String TABLE = "file";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

        public static final String TYPE = "type";
		public static final String PATH = "path";
		public static final String NAME = "name";
		public static final String SIZE = "size";
		public static final String DATE = "date";
		public static final String SYNC = "sync";
		public static final String EXTENSION = "extension";
		public static final String STORAGE = "storage";
		public static final String DEFAULT_SORT_ORDER = DATE + " desc";

        public static final String FAVORITE_ID = "favorite_id";

		public static final String[] PROJECTION = new String[] {
			_ID, TYPE, PATH, NAME, SIZE, EXTENSION, DATE, STORAGE, SYNC, FAVORITE_ID};

		public static final int ID_INDEX = 0;
		public static final int TYPE_INDEX = 1;
		public static final int PATH_INDEX = 2;
		public static final int NAME_INDEX = 3;
		public static final int SIZE_INDEX = 4;
		public static final int EXTENSION_INDEX = 5;
		public static final int DATE_INDEX = 6;
		public static final int STORAGE_INDEX = 7;
		public static final int SYNC_INDEX = 8;
        public static final int FAVORITE_ID_INDEX = 9;
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +PATH + " TEXT, "
                +NAME + " TEXT, "
                +TYPE + " TEXT, "
                +SIZE + " LONG, "
                +DATE + " LONG, "
                +EXTENSION + " TEXT, "
                +STORAGE + " TEXT, "
            	+SYNC + " INTEGER, UNIQUE("+FileColumns.PATH+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class ImageColumns extends FileColumns {
		
		public static String TABLE = "image";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

		public static final String IMAGE_WIDTH = "image_width";
		public static final String IMAGE_HEIGHT = "image_height";

		public static final int IMAGE_WIDTH_INDEX = 9;
		public static final int IMAGE_HEIGHT_INDEX = 10;
		
		public static final String[] IMAGE_PROJECTION =  new String[] {
			_ID, TYPE, PATH, NAME, SIZE, EXTENSION, DATE, STORAGE, SYNC, FAVORITE_ID, IMAGE_WIDTH, IMAGE_HEIGHT};
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +PATH + " TEXT, "
                +TYPE + " TEXT, "
                +NAME + " TEXT, "
                +SIZE + " LONG, "
                +DATE + " LONG, "
                +STORAGE + " TEXT, "
                +SYNC + " INTEGER, "
                +IMAGE_WIDTH + " INTEGER, "
                +IMAGE_HEIGHT + " INTEGER, "
                +EXTENSION + " TEXT, UNIQUE("+ImageColumns.PATH+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class AudioColumns extends FileColumns {
		
        public static final String TABLE = "audio";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

		public static final String PLAY_DURATION = "play_duration";
		public static final String ALBUM = "album";
		public static final String SINGER = "singer";
		public static final String TITLE = "title";
		public static final String DEFAULT_SORT_ORDER = NAME + " desc";
		
		public static final String[] AUDIO_PROJECTION =  new String[] {
			_ID, TYPE, PATH, NAME, SIZE, EXTENSION, DATE, STORAGE, SYNC, FAVORITE_ID, PLAY_DURATION, SINGER, ALBUM, TITLE};
	
		
		public static final int DURATION_INDEX = 9;
		public static final int SINGER_INDEX = 10;
		public static final int ALBUM_INDEX = 11;
		public static final int TITLE_INDEX = 12;
		public static final int NAME_INDEX = 3;
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +PATH + " TEXT, "
                +TYPE + " TEXT, "
                +NAME + " TEXT, "
                +SIZE + " LONG, "
                +DATE + " LONG, "
                +STORAGE + " TEXT, "
                +SYNC + " INTEGER, "
                +PLAY_DURATION + " LONG, "
                +SINGER + " TEXT, "
                +ALBUM + " TEXT, "
                +TITLE + " TEXT, "
                +EXTENSION + " TEXT, UNIQUE("+AudioColumns.PATH+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class VideoColumns extends FileColumns {
		
		public static final String TABLE = "video";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

		public static final String PLAY_DURATION = "play_duration";
		public static final String[] VIDEO_PROJECTION =  new String[] {
			_ID, TYPE, PATH, NAME, SIZE, EXTENSION, DATE, STORAGE, SYNC, FAVORITE_ID, PLAY_DURATION};
	
		public static final int DURATION_INDEX = 9;
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +PATH + " TEXT, "
                +TYPE + " TEXT, "
                +NAME + " TEXT, "
                +SIZE + " LONG, "
                +DATE + " LONG, "
                +STORAGE + " TEXT, "
                +SYNC + " INTEGER, "
                +PLAY_DURATION + " LONG, "
                +EXTENSION + " TEXT, UNIQUE("+VideoColumns.PATH+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class ApkColumns extends FileColumns {
		
        public static final String TABLE = "apk";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String[] APK_PROJECTION =  new String[] {
			_ID, TYPE, PATH, NAME, SIZE, EXTENSION, DATE, STORAGE, SYNC, FAVORITE_ID};
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +PATH + " TEXT, "
                +TYPE + " TEXT, "
                +NAME + " TEXT, "
                +SIZE + " LONG, "
                +DATE + " LONG, "
                +EXTENSION + " TEXT, "
                +STORAGE + " TEXT, "
                +SYNC + " INTEGER, UNIQUE("+ApkColumns.PATH+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}

	
	public static final class DocumentColumns extends FileColumns {
		
        public static final String TABLE = "document";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String[] DOCUMENT_PROJECTION =  new String[] {
			_ID, TYPE, PATH, NAME, SIZE, EXTENSION, DATE, STORAGE, SYNC, FAVORITE_ID};
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +PATH + " TEXT, "
                +TYPE + " TEXT, "
                +NAME + " TEXT, "
                +SIZE + " LONG, "
                +DATE + " LONG, "
                +EXTENSION + " TEXT, "
                +STORAGE + " TEXT, "
            	+SYNC + " INTEGER, UNIQUE("+DocumentColumns.PATH+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class ZipColumns extends FileColumns {
		
        public static final String TABLE = "zip";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String DEFAULT_SORT_ORDER = EXTENSION + " asc";
		
		public static final String[] ZIP_PROJECTION =  new String[] {
			_ID, TYPE, PATH, NAME, SIZE, EXTENSION, DATE, STORAGE, SYNC, FAVORITE_ID};
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +PATH + " TEXT, "
                +TYPE + " TEXT, "
                +NAME + " TEXT, "
                +SIZE + " LONG, "
                +DATE + " LONG, "
                +EXTENSION + " TEXT, "
                +STORAGE + " TEXT, "
                +SYNC + " INTEGER, UNIQUE("+ZipColumns.PATH+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}

    public static final class CloudBoxColumns extends FileColumns {

        public static final String TABLE = "cloudbox";

        public static String CONTENT_BOX = "content://" + AUTHORITY + "/" + TABLE;

        public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

        public static final String PARENT_FOLDER = "parent_folder";
        public static final String IS_FOLDER = "is_folder";
        public static final String HASH = "hash";
        public static final String LOCAL_FILE = "local_file";
        public static final String ICON_DATA = "local_icon";

        public static final int PARENT_FOLDER_INDEX = 9;
        public static final int IS_FOLDER_INDEX = 10;
        public static final int HASH_INDEX = 11;
        public static final int LOCAL_INDEX = 12;
        public static final int LOCAL_ICON_INDEX = 13;

        public static final String[] CLOUD_BOX_PROJECTION = new String[] {
                _ID, TYPE, PATH, NAME, SIZE, EXTENSION, DATE, STORAGE, SYNC,
                PARENT_FOLDER, IS_FOLDER, HASH, LOCAL_FILE, ICON_DATA,
        };

        public static final class SQL {

            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                    +" (_id INTEGER PRIMARY KEY, "
                    +PATH + " TEXT, "
                    +TYPE + " TEXT, "
                    +NAME + " TEXT, "
                    +SIZE + " LONG, "
                    +DATE + " LONG, "
                    +EXTENSION + " TEXT, "
                    +STORAGE + " TEXT, "
                    +SYNC + " INTEGER, "
                    +PARENT_FOLDER + " TEXT, "
                    +IS_FOLDER + " INTEGER, "
                    +HASH + " TEXT, "
                    +LOCAL_FILE + " TEXT, "
                    + ICON_DATA + " TEXT, UNIQUE("+PATH+") "
                    + ");";

            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;

        }

    }

    public static final class SelectedColumns extends FileColumns {

        public static final String TABLE = "selected";

        public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

        public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);

        public static final String URL = "url";
        public static final String SERVER_NAME = "server_name";
        public static final String PACKAGE = "package";
        public static final String VERSION = "version";
        public static final String VERSION_NAME = "version_name";
        public static final String SERVER_DATE = "sever_date";
        public static final String DESCRIPTION = "description";
        public static final String APP_CATEGORY = "app_category";
        public static final String ICON = "icon";
        public static final String PHOTO = "photo";

        public static final int URL_FILED_INDEX = 9;
        public static final int SERVER_NAME_INDEX = 10;
        public static final int PACKAGE_INDEX = 11;
        public static final int VERSION_INDEX = 12;
        public static final int VERSION_NAME_INDEX = 13;
        public static final int SERVER_DATE_INDEX = 14;
        public static final int DESCRIPTION_INDEX = 15;
        public static final int APP_CATEGORY_INDEX = 16;
        public static final int ICON_INDEX = 17;
        public static final int PHOTO_INDEX = 18;

        public static final String DEFAULT_SORT_ORDER = "sever_date desc";

        public static final String[] SELECTED_PROJECTION =  new String[] {
                _ID, TYPE, PATH, NAME, SIZE, EXTENSION, DATE, STORAGE, SYNC,
                URL, SERVER_NAME, PACKAGE, VERSION, VERSION_NAME, SERVER_DATE, DESCRIPTION, APP_CATEGORY, ICON, PHOTO,
        };

        public static final class SQL {

            public static final String CREATE = "Create TABLE IF NOT EXISTS  " + TABLE
                    +" (_id INTEGER PRIMARY KEY, "
                    +PATH + " TEXT, "
                    +TYPE + " TEXT, "
                    +NAME + " TEXT, "
                    +SIZE + " LONG, "
                    +DATE + " LONG, "
                    +EXTENSION + " TEXT, "
                    +STORAGE + " TEXT, "
                    +SYNC + " INTEGER, "
                    +URL + " TEXT, "
                    +SERVER_NAME + " TEXT, "
                    +PACKAGE + " TEXT, "
                    +VERSION + " INTEGER, "
                    +VERSION_NAME + " TEXT, "
                    +SERVER_DATE + " LONG, "
                    +DESCRIPTION + " TEXT, "
                    +APP_CATEGORY + " INTEGER, "
                    +ICON + " TEXT, "
                    +PHOTO + " TEXT, UNIQUE("+SelectedColumns.URL+") "
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

        public static final String IS_DIRECTORY = "is_directory";

		public static final String[] FAVORITE_PROJECTION =  new String[] {
			_ID, TYPE, PATH, NAME, SIZE, EXTENSION, DATE, STORAGE, SYNC, IS_DIRECTORY};
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +PATH + " TEXT, "
                +TYPE + " TEXT, "
                +NAME + " TEXT, "
                +SIZE + " LONG, "
                +DATE + " LONG, "
                +IS_DIRECTORY + " INTEGER, "
                +EXTENSION + " TEXT, "
                +STORAGE + " TEXT, "
            	+SYNC + " INTEGER, UNIQUE("+FavoriteColumns.PATH+") "
                    + ");";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
	
	public static final class MatchColumns implements BaseColumns {
		
        public static final String TABLE = "match";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String EXTENSION = "extension";
		
		public static final String CATEGORY = "category";
		
		public static final String APP = "app";
		
		public static final String DATE = "date";
		
		public static final String DEFAULT_SORT_ORDER = "extension asc";
		
		public static final String[] MATCH_PROJECTION =  new String[] {
			EXTENSION, CATEGORY, APP, DATE};
		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" ("
                +EXTENSION + " TEXT PRIMARY KEY, "
                +CATEGORY + " INTEGER, "
                +APP + " TEXT, "
                +DATE + " LONG);";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
    

}
