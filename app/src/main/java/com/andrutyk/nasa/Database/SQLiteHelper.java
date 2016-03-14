package com.andrutyk.nasa.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.andrutyk.nasa.Database.Tables.ImagesTable;

/**
 * Created by admin on 29.01.2016.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String CONTENT_AUTHORITY = "andrutyk.retrofit.loaders";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String DATABASE_NAME = "nasa.db";

    private static final int DATABASE_VERSION = 3;

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ImagesTable.Requests.CREATION_REQUEST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ImagesTable.Requests.DROP_REQUEST);
        onCreate(db);
    }
}
