package com.andrutyk.nasa.API;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by admin on 28.01.2016.
 */
public abstract class RetrofitCallback<T> implements Callback<T>{
    @Override
    public void onResponse(Response<T> response, Retrofit retrofit) {

    }

    @Override
    public void onFailure(Throwable t) {

    }
}
