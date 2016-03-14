package com.andrutyk.nasa.Database.Realm;

import android.support.annotation.NonNull;

import com.andrutyk.nasa.Content.Imagery;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by admin on 01.02.2016.
 */
public class ImageryHelper {
    public static void save(@NonNull Realm realm, Imagery imagery) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(imagery);
        realm.commitTransaction();
    }

    @NonNull
    public static List<Imagery> getImageries(@NonNull Realm realm) {
        return realm.allObjects(Imagery.class);
    }

    public static Imagery getImagery(@NonNull Realm realm, @NonNull String date){
        return realm.where(Imagery.class).equalTo("date", date).findFirst();
    }

    public static Imagery getLastImagery(@NonNull Realm realm){
        return realm.where(Imagery.class).findFirst();
    }
}
