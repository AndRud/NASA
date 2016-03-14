package com.andrutyk.nasa.API.Response;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrutyk.nasa.Content.Imagery;

import java.util.Objects;

/**
 * Created by admin on 28.01.2016.
 */
public class Response {

    @Nullable private Object mAnswer;

    private RequestResult mRequestResult;

    public Response() {
        mRequestResult = RequestResult.ERROR;
    }

    @NonNull
    public RequestResult getRequestResult(){
        return mRequestResult;
    }

    public Response setRequestResult(RequestResult requestResult){
        mRequestResult = requestResult;
        return this;
    }

    @Nullable
    public <T> T getTypedAnswer() {
        if (mAnswer == null) {
            return null;
        }
        //noinspection unchecked
        return (T) mAnswer;
    }

    public Response setAnswer(@Nullable Object answer) {
        mAnswer = answer;
        return this;
    }

    public void save(Context context) {
    }

    public Imagery getFromDB(Context context, String date){
        return null;
    };
}
