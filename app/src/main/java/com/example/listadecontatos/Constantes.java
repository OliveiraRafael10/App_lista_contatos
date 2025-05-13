package com.example.listadecontatos;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Constantes {
    public static String API_URL = "https://api-contacts-qfxx.onrender.com/";
    public static Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(Constantes.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
