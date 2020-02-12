package com.nhq.goodie.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    private static JsonApi instance;
    public static JsonApi getInstance(){
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://goodie-server.herokuapp.com") //fill with server IP
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            instance = retrofit.create(JsonApi.class);
        }
        return instance;
    }
}
