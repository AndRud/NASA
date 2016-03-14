package com.andrutyk.nasa.API.Response;

import android.content.Context;
import android.widget.Toast;

import com.andrutyk.nasa.Content.Imagery;
import com.andrutyk.nasa.Database.Realm.ImageryHelper;
import com.andrutyk.nasa.Database.Tables.ImagesTable;

import java.util.List;

import io.realm.Realm;

/**
 * Created by admin on 28.01.2016.
 */
public class ImageryResponse extends Response {
    @Override
    public void save(Context context) {
        Imagery imagery = getTypedAnswer();
        if (imagery != null) {
            ImageryHelper.save(Realm.getInstance(context), imagery);
        }
    }

    @Override
    public Imagery getFromDB(Context context, String date) {
        return ImageryHelper.getImagery(Realm.getInstance(context), date);
    }
}
