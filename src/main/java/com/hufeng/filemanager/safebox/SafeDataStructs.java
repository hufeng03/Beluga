package com.hufeng.filemanager.safebox;

import android.net.Uri;
import android.provider.BaseColumns;

public class SafeDataStructs {
    public static final String AUTHORITY = "com.hufeng.safemanager";

	public static final class SafeColumns implements BaseColumns {
		
        public static final String TABLE = "secrect";
		
		public static String CONTENT_BOX = "content://" + AUTHORITY + "/"+TABLE;

		public static Uri CONTENT_URI = Uri.parse(CONTENT_BOX);
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.hufeng.safebox.secrect";
		
		
		public static final String ORIGINAL_PATH = "original_path";
        public static final String ORIGINAL_DATE = "original_date";
        public static final String ORIGINAL_SIZE = "original_size";
        public static final String ORIGINAL_EXTENSION = "original_extension";
		public static final String SAFE_PATH = "safe_path";
        public static final String SAFE_ADD_DATE = "safe_add_date";
        public static final String CATEGORY = "category";
        public static final String THUMBNAIL = "thumbnail";
		public static final String RESERVED_FIELD_1 = "reserved_1";
		public static final String RESERVED_FIELD_2 = "reserved_2";
		public static final String RESERVED_FIELD_3 = "reserved_3";
		public static final String RESERVED_FIELD_4 = "reserved_4";
        public static final String RESERVED_FIELD_5 = "reserved_5";
		
		public static final String[] SAFE_PROJECTION =  new String[] {
			_ID, ORIGINAL_PATH, ORIGINAL_DATE, ORIGINAL_SIZE, ORIGINAL_EXTENSION, SAFE_PATH, SAFE_ADD_DATE, CATEGORY, THUMBNAIL, RESERVED_FIELD_1, RESERVED_FIELD_2, RESERVED_FIELD_3, RESERVED_FIELD_4, RESERVED_FIELD_5};
				
		public static final int FIELD_INDEX_ORIGINAL_PATH = 1;
		public static final int FIELD_INDEX_ORIGINAL_DATE = 2;
		public static final int FIELD_INDEX_ORIGIANL_SIZE = 3;
        public static final int FIELD_INDEX_ORIGIANL_EXTENSION = 4;
        public static final int FIELD_INDEX_SAFE_PATH = 5;
        public static final int FIELD_INDEX_SAFE_ADD_DATE = 6;
        public static final int FIELD_INDEX_CATEGORY = 7;
        public static final int FIELD_INDEX_THUMBNAIL = 8;

		
		public static final class SQL {
            
            public static final String CREATE = "Create TABLE IF NOT EXISTS " + TABLE
                +" (_id INTEGER PRIMARY KEY, "
                +ORIGINAL_PATH + " TEXT, "
                +ORIGINAL_SIZE + " LONG, "
                +ORIGINAL_DATE + " LONG, "
                +ORIGINAL_EXTENSION + " TEXT, "
                +SAFE_PATH + " TEXT, "
                +SAFE_ADD_DATE + " LONG, "
                +CATEGORY + " INTEGER, "
                +THUMBNAIL + " TEXT, "
                +RESERVED_FIELD_1 + " LONG, "
                +RESERVED_FIELD_2 + " LONG, "
                +RESERVED_FIELD_3 + " INTEGER, "
                +RESERVED_FIELD_4 + " TEXT, "
                +RESERVED_FIELD_5 + " TEXT);";
            
            public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;                   
                    
        }
	}
    	
}
