package com.andrutyk.nasa.API;

import com.andrutyk.nasa.Content.Imagery;

import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.Call;

/**
 * Created by admin on 28.01.2016.
 */
public interface ImageryService {

    @GET("/planetary/apod")
    Call<Imagery> images(@Query("date") String date, @Query("api_key") String api_key);
}
