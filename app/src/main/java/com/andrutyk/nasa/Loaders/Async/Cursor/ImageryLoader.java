package com.andrutyk.nasa.Loaders.Async.Cursor;

import android.content.Context;
import android.os.Looper;

import com.andrutyk.nasa.API.ApiFactory;
import com.andrutyk.nasa.API.ImageryService;
import com.andrutyk.nasa.API.Response.ImageryResponse;
import com.andrutyk.nasa.API.Response.RequestResult;
import com.andrutyk.nasa.API.Response.Response;
import com.andrutyk.nasa.Content.Imagery;
import com.andrutyk.nasa.Database.Realm.ImageryHelper;

import java.io.IOException;

import android.os.Handler;

import io.realm.Realm;
import retrofit.Call;

/**
 * Created by admin on 28.01.2016.
 */
public class ImageryLoader extends BaseLoader {

    private final String date;
    private final String api_key;
    private Realm realm;
    private volatile ImageryResponse imageryResponse;

    public ImageryLoader(Context context, Realm realm, String date, String api_key){
        super(context);
        this.date = date;
        this.api_key = api_key;
        this.realm = realm;
        imageryResponse = new ImageryResponse();
    }

    @Override
    protected Response onError() {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Imagery imagery = ImageryHelper.getImagery(Realm.getInstance(getContext()), date);
                if (imagery == null) {
                    imagery = new Imagery();
                    imagery.setDate(date);
                }
                imageryResponse.setAnswer(imagery)
                    .setRequestResult(RequestResult.SUCCESS);
            }
        };
        handler.post(runnable);

        return imageryResponse;
    }

    @Override
    protected Response apiCall() throws IOException{
        ImageryService imageryService = ApiFactory.getImageryService();
        Call<Imagery> call = imageryService.images(date, api_key);
        Imagery imagery = call.execute().body();
        return new ImageryResponse()
                .setRequestResult(RequestResult.SUCCESS)
                .setAnswer(imagery);
    }
}
