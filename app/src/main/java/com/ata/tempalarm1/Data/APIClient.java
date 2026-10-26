package com.ata.tempalarm1.Data;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit;
    private static final String baseURL= "https://api.weatherapi.com/v1/current.json/";

    public static Retrofit getRetrofit() {

        if(retrofit == null){
            retrofit= new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build();
            //Retrofit.Builder() is the constructor for this Retrofit object
            //  behind the scenes, this object handles the requests made to the API with OKhttp2
            //  which is the object that's used for any internet request made while handling the boilerplate code
            //.baseUrl( specifies the baseUrl of the API
            //.addConverterFactory( tells Retrofit what Gson library to use so that the term from the API is in JSON converted to the Java object to compare to other Java objects
            //.build executes once all the parameters are set

        }
        return retrofit;
    }
}

/*

SAMPLE CALL:
https://api.weatherapi.com/v1/current.json?key=<api-key>&q=95928&aqi=no
or
curl --request GET --url "https://api.weatherapi.com/v1/current.json?key=<api-key>&q=95864&aqi=no"

SAMPLE OUTPUT:
{
  "location": {
    "name": "Chico",
    "region": "California",
    "country": "USA",
    "lat": 39.7224006652832,
    "lon": -121.810997009277,
    "tz_id": "America/Los_Angeles",
    "localtime_epoch": 1792967301,
    "localtime": "2026-10-25 15:28"
  },
  "current": {
    "last_updated_epoch": 1792966500,
    "last_updated": "2026-10-25 15:15",
    "temp_c": 22.8,
    "temp_f": 73.0,
    "is_day": 1,
    "condition": {
      "text": "Overcast",
      "icon": "//cdn.weatherapi.com/weather/64x64/day/122.png",
      "code": 1009
    },
    "wind_mph": 5.1,
    "wind_kph": 8.3,
    "wind_degree": 194,
    "wind_dir": "SSW",
    "pressure_mb": 1017.0,
    "pressure_in": 30.02,
    "precip_mm": 0.0,
    "precip_in": 0.0,
    "humidity": 32,
    "cloud": 100,
    "feelslike_c": 20.1,
    "feelslike_f": 68.2,
    "windchill_c": 22.8,
    "windchill_f": 73.0,
    "heatindex_c": 23.8,
    "heatindex_f": 74.8,
    "dewpoint_c": 5.5,
    "dewpoint_f": 41.9,
    "vis_km": 10.0,
    "vis_miles": 6.0,
    "uv": 2.1,
    "gust_mph": 6.7,
    "gust_kph": 10.8,
    "will_it_rain": 0,
    "chance_of_rain": 10,
    "will_it_snow": 0,
    "chance_of_snow": 0,
    "wetbulb_c": 13.4,
    "wetbulb_f": 56.1,
    "short_rad": 408.32,
    "diff_rad": 232.8,
    "dni": 321.83,
    "gti": 215.29
  }
}
*/
