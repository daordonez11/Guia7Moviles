package com.moviles.domiciliosmoviles.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maca on 9/02/17.
 */

public class RestClient {

    private static final String BASE_URL = "http://demo7931028.mockable.io/";

    private ApiService apiService;
    private static RestClient instance;

    private RestClient()  {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static RestClient getInstance() {
        if (instance == null) {
            instance = new RestClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }



}
