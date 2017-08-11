package com.appsys.android.popularmovie.classes;

/**
 * Created by shakir on 8/11/2017.
 */

public class MovieDbJSONException extends MovieDbException {

    private String mJsonString;

    public MovieDbJSONException(String message, String json) {
        super(message);
        mJsonString = json;
    }

    public String getJsonString() {
        return mJsonString;
    }
}