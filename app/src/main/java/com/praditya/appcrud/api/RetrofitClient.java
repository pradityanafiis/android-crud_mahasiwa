package com.praditya.appcrud.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient mInstance;
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.1.5:8000/api/v1/";
    public static final String BASE_URL_IMAGE = "http://192.168.1.5:8000/foto_mahasiswa/";

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public APIRequest getAPI() {
        return retrofit.create(APIRequest.class);
    }

    public static String getImageUrl(String filename) {
        return BASE_URL_IMAGE + filename;
    }
}
