package com.ata.tempalarm1.Data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface GetDataService {
    @GET("/current.json")
    Call <WeatherInfo> getWeatherAtZipcode(@Header("key") String apiKey, @Query("q") String queryString);
    //everytime getWeatherAtZipcode( is called, the apiKey is passed and made part of a header object
    //the query adds a ? to the queryString that holds the passed in zipcode

}
