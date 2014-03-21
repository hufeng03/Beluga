package com.hufeng.filemanager.safebox;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.hufeng.filemanager.safebox.SafeDataStructs.SafeColumns;

import java.io.File;
import java.util.HashMap;

public class SafeBoxDatabaseProvider extends ContentProvider{
	
    private static final String TAG = SafeBoxDatabaseProvider.class.getSimpleName();

    private static final String DATABASE_NAME = Environment.getExternalStorageDirectory()
            .getPath() + File.separator + SafeBoxConfig.STORAGE_DIR + File.separator + SafeBoxConfig.DATABASE_NAME;
    private static final int DATABASE_VERSION = 1;

    private static final HashMap<String, String> mSafeProjectionMap;
    private static final int SECRECTS = 19;
	private static final int SECRECT_ID = 20;
	
	private static final UriMatcher URI_MATCHER;
	
	static{
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        URI_MATCHER.addURI(SafeDataStructs.AUTHORITY, SafeColumns.TABLE, SECRECTS);
        URI_MATCHER.addURI(SafeDataStructs.AUTHORITY, SafeColumns.TABLE+"/#", SECRECT_ID);
        
        mSafeProjectionMap = new HashMap<String, String>();
        mSafeProjectionMap.put(SafeColumns._ID, SafeColumns._ID);
        mSafeProjectionMap.put(SafeColumns.ORIGINAL_PATH, SafeColumns.ORIGINAL_PATH);
        mSafeProjectionMap.put(SafeColumns.ORIGINAL_DATE, SafeColumns.ORIGINAL_DATE);
        mSafeProjectionMap.put(SafeColumns.ORIGINAL_SIZE, SafeColumns.ORIGINAL_SIZE);
        mSafeProjectionMap.put(SafeColumns.ORIGINAL_EXTENSION, SafeColumns.ORIGINAL_EXTENSION);
        mSafeProjectionMap.put(SafeColumns.SAFE_PATH, SafeColumns.SAFE_PATH);
        mSafeProjectionMap.put(SafeColumns.SAFE_ADD_DATE, SafeColumns.SAFE_ADD_DATE);
        mSafeProjectionMap.put(SafeColumns.CATEGORY, SafeColumns.CATEGORY);
        mSafeProjectionMap.put(SafeColumns.THUMBNAIL, SafeColumns.THUMBNAIL);
        mSafeProjectionMap.put(SafeColumns.RESERVED_FIELD_1, SafeColumns.RESERVED_FIELD_1);
        mSafeProjectionMap.put(SafeColumns.RESERVED_FIELD_2, SafeColumns.RESERVED_FIELD_2);
        mSafeProjectionMap.put(SafeColumns.RESERVED_FIELD_3, SafeColumns.RESERVED_FIELD_3);
        mSafeProjectionMap.put(SafeColumns.RESERVED_FIELD_4, SafeColumns.RESERVED_FIELD_4);
        mSafeProjectionMap.put(SafeColumns.RESERVED_FIELD_5, SafeColumns.RESERVED_FIELD_5);
	}
	

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SafeColumns.SQL.CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

    }

    private DatabaseHelper mOpenHelper;
    
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String limit = null;
        String orderBy = null;

        switch (URI_MATCHER.match(uri)) {
        case SECRECTS:
            qb.setTables(SafeColumns.TABLE);
            qb.setProjectionMap(mSafeProjectionMap);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, orderBy, limit);

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
        case SECRECTS:
            return SafeColumns.CONTENT_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        Log.i(TAG, "insert" + uri + " values = " + initialValues);

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (URI_MATCHER.match(uri)) {
        case SECRECTS: {
            long rowId = db.insert(SafeColumns.TABLE, SafeColumns.SAFE_PATH,
                    values);
            if (rowId > 0) {
                Uri result = ContentUris.withAppendedId(SafeColumns.CONTENT_URI,
                        rowId);
                getContext().getContentResolver().notifyChange(result, null);
                return result;
            }
        }
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String table = null;
        String nullColumnHack = null;
        int match = URI_MATCHER.match(uri);
        if (match == SECRECTS) {
            table = SafeColumns.TABLE;
            nullColumnHack = SafeColumns.SAFE_PATH;
        }
        if (table == null)
            return 0;

        db.beginTransaction();
        try {
            for (ContentValues v : values) {
                long row = db.insert(table, nullColumnHack, v);
                if (row < 0)
                    return 0;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return values.length;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        switch (URI_MATCHER.match(uri)) {
        case SECRECTS:
            count = db.delete(SafeColumns.TABLE, where, whereArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
            String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int count = 0;
        switch (URI_MATCHER.match(uri)) {
        case SECRECTS:
            count = db.update(SafeColumns.TABLE, values, where, whereArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    

}
