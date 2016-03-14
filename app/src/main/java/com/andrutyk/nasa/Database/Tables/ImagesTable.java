package com.andrutyk.nasa.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.andrutyk.nasa.Content.Imagery;
import com.andrutyk.nasa.Database.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 29.01.2016.
 */
public class ImagesTable {

    public static final Uri URI = SQLiteHelper.BASE_CONTENT_URI.buildUpon().appendPath(Requests.TABLE_NAME).build();

    public static void save(Context context, @NonNull Imagery imageries) {
        context.getContentResolver().insert(URI, toContentValues(imageries));
    }

    public static void save(Context context, @NonNull List<Imagery> imageries) {
        ContentValues[] values = new ContentValues[imageries.size()];
        for (int i = 0; i < imageries.size(); i++) {
            values[i] = toContentValues(imageries.get(i));
        }
        context.getContentResolver().bulkInsert(URI, values);
    }

    @NonNull
    public static ContentValues toContentValues(@NonNull Imagery imagery) {
        ContentValues values = new ContentValues();
        values.put(Columns.DATE, imagery.getDate());
        values.put(Columns.EXPLANATION, imagery.getExplanation());
        values.put(Columns.HDURL, imagery.getHdurl());
        values.put(Columns.TITLE, imagery.getTitle());
        return values;
    }

    @NonNull
    public static Imagery fromCursor(@NonNull Cursor cursor) {
        String date = cursor.getString(cursor.getColumnIndex(Columns.DATE));
        String explanation = cursor.getString(cursor.getColumnIndex(Columns.EXPLANATION));
        String hdurl = cursor.getString(cursor.getColumnIndex(Columns.HDURL));
        String title = cursor.getString(cursor.getColumnIndex(Columns.TITLE));
        String media_type = cursor.getString(cursor.getColumnIndex(Columns.TITLE));
        return new Imagery(date, explanation, hdurl, media_type, title);
    }

    @NonNull
    public static List<Imagery> listFromCursor(@NonNull Cursor cursor) {
        List<Imagery> imageries = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return imageries;
        }
        try {
            do {
                imageries.add(fromCursor(cursor));
            } while (cursor.moveToNext());
            return imageries;
        } finally {
            cursor.close();
        }
    }

    public static void clear(Context context) {
        context.getContentResolver().delete(URI, null, null);
    }

    public interface Columns {
        String DATE = "date";
        String EXPLANATION = "explanation";
        String HDURL = "hdurl";
        String TITLE = "title";
    }

    public interface Requests {

        String TABLE_NAME = ImagesTable.class.getSimpleName();

        String CREATION_REQUEST = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                Columns.DATE + " DATETIME NOT NULL PRIMARY KEY, " +
                Columns.EXPLANATION + " TEXT(200), " +
                Columns.HDURL + " VARCHAR(200), " +
                Columns.TITLE + " VARCHAR(200)" + ");";

        String DROP_REQUEST = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
