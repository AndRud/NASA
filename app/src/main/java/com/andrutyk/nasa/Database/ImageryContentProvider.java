package com.andrutyk.nasa.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.andrutyk.nasa.Database.Tables.ImagesTable;

/**
 * Created by admin on 09.02.2016.
 */
public class ImageryContentProvider extends ContentProvider{

    private static final int IMAGERIES_TABLE = 1;

    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(SQLiteHelper.CONTENT_AUTHORITY, ImagesTable.Requests.TABLE_NAME, IMAGERIES_TABLE);
    }

    private SQLiteHelper mSqliteHelper;

    @Override
    public boolean onCreate() {
        mSqliteHelper = new SQLiteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mSqliteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new UnsupportedOperationException("No such table to query");
        } else {
            return database.query(table,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case IMAGERIES_TABLE:
                return ImagesTable.Requests.TABLE_NAME;
            default:
                return "";
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = mSqliteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new UnsupportedOperationException("No such table to query");
        }
        else {
            long id = database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            return ContentUris.withAppendedId(uri, id);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mSqliteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new UnsupportedOperationException("No such table to query");
        }
        else {
            return database.delete(table, selection, selectionArgs);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mSqliteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new UnsupportedOperationException("No such table to query");
        }
        else {
            return database.update(table, values, selection, selectionArgs);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase database = mSqliteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new UnsupportedOperationException("No such table to query");
        }
        else {
            int numInserted = 0;
            database.beginTransaction();
            try {
                for (ContentValues contentValues : values) {
                    long id = database.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    if (id > 0) {
                        numInserted++;
                    }
                }
                database.setTransactionSuccessful();
            }
            finally {
                database.endTransaction();
            }
            return numInserted;
        }
    }
}
