package com.andrutyk.nasa.Loaders.Async.Cursor;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.andrutyk.nasa.API.Response.ImageryResponse;
import com.andrutyk.nasa.API.Response.RequestResult;
import com.andrutyk.nasa.API.Response.Response;

import java.io.IOException;

import io.realm.Realm;

/**
 * Created by admin on 28.01.2016.
 */
public abstract class BaseLoader extends AsyncTaskLoader<Response> {

    private Context context;

    public BaseLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public Response loadInBackground() {
        try {
            Response response = apiCall();
            if (response.getRequestResult() == RequestResult.SUCCESS) {
                response.save(context);
            } else {
                response = onError();
            }
            return response;
        } catch (IOException e) {
            return onError();
        }
    }

    protected abstract Response onError();

    protected abstract Response apiCall() throws IOException;
}
