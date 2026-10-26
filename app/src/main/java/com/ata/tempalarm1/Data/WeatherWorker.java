package com.ata.tempalarm1.Data;

import static android.content.Context.ALARM_SERVICE;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ata.tempalarm1.AlarmReceiver;
import com.ata.tempalarm1.MainActivity;
import com.ata.tempalarm1.Data.Alarm;
import com.ata.tempalarm1.Data.GetDataService;
import com.ata.tempalarm1.Data.ListDAO;
import com.ata.tempalarm1.Data.MainDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherWorker extends Worker {

    static int milToMS(int milit){
        return (((milit-(milit%100))*36000)+((milit%100)*60000));
    }

    static {
        System.loadLibrary("api-keys");
    }

    public native String getAPIKey();
//    private MainDB Database;
//    private LiveData<List<Alarm>> ListOfAlarms;

    public static MutableLiveData<WeatherInfo> outputObservable = new MutableLiveData();

    public WeatherWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }
    private final String WEATHWORK = "WeatherWorker";

    @NonNull
    @Override
    public Result doWork() {
        Log.d(WEATHWORK, "Start doWork");

        GetDataService service = APIClient.getRetrofit().create(GetDataService.class);
//        Database = Room.databaseBuilder(getApplicationContext(), MainDB.class, "userAlarms")
//                .allowMainThreadQueries()
//                .build();
//        ListDAO listDAO = Database.listDAO();
//        ///data/data/com.ata.tempalarm1/databases
//        //ListOfAlarms= getApplicationContext().getDatabasePath();
//        ListOfAlarms= listDAO.getAllAlarms();
//        List<Alarm> currList = ListOfAlarms.getValue();
//        Log.e("LocationGot", "listDAO?: "+listDAO );
//        Log.e("LocationGot", "ListOfAlarms?: "+ListOfAlarms );
//        Log.e("LocationGot", "currList?: "+currList );

        //getting the Retrofit instance and telling it to create a service that specifies what the call will be
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        final Call<WeatherInfo>[] call = new Call[]{null};
        /*try {
            Thread.sleep(2);//UGPS=t, first break
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Log.e("LocationGot", "UseGPS: "+sharedPref.getBoolean("UseGPS", false) );
        if (!sharedPref.getBoolean("UseGPS", false)) {
            /*try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            Log.d(WEATHWORK, "Setup API call with Zipcode");
            call[0] = service.getWeatherAtZipcode(getAPIKey(), Integer.toString(sharedPref.getInt("Zipcode", 95928)));
            /*try {
                Thread.sleep(2);
                //UseGPS==false, first break, call[0] set
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        } else {
            /*try {
                Thread.sleep(2);
                //UGPS=t, second break
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                /*try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

            }else{
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {//entering even onFailure()??
                        /*try {
                            Thread.sleep(2);
                            //should be UGPS=t, third break
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                        Log.e("LocationGot", "Lat: "+Double.toString(location.getLatitude()) );
                        Log.e("LocationGot", "Long: "+Double.toString(location.getLongitude()) );


                        Log.d(WEATHWORK, "Setup API call with GPS");
                        call[0] = service.getWeatherAtZipcode(getAPIKey(), Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude()));
                        //call[0] = service.getWeatherAtZipcode(getAPIKey(), "37.759, -122.403");

                        //getting stuck here^
                        /*try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                    }
                });
            }



        }
        try{


            Log.e("CurrTime", "C: HHmm: "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm")) );
            Log.e("CurrTime", "W: HHmm: "+ sharedPref.getInt("WakeTime",1000) );
            Log.e("CurrTime", "S: HHmm: "+ sharedPref.getInt("SleepTime",2000) );

            int currTime= Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm")));
            int wakeTime= sharedPref.getInt("WakeTime",1000);
            int sleepTime= sharedPref.getInt("SleepTime",2000);
            int waitTime=0;

            if( currTime < wakeTime || currTime > sleepTime){

                //Log.e("CurrTime", "Wms: "+ (milToMS(wakeTime)) );
                //Log.e("CurrTime", "Cms: "+ (milToMS(currTime)) );

                //Log.e("CurrTime", "Wms+24h: "+ ((milToMS(wakeTime))+86400000) );
                //Log.e("CurrTime", "Cms%24h: "+ ((milToMS(currTime))%86400000) );

                //Log.e("CurrTime", "Dif:    "+ ((((milToMS(wakeTime))+86400000)-((milToMS(currTime))%86400000))%86400000) );
                //900 +2400 = 3300  2300%2400 =2300 3300-2300= 1000
                //900 +2400 = 3300  100%2400 =100 3300-100= 32000%2400 = 800


                waitTime= ((((milToMS(wakeTime)+86400000)-milToMS(currTime))%86400000)%86400000);
                Log.e("CurrTime", "WT: "+ (waitTime) );
                //Log.e("CurrTime", "WTmin: "+ ((waitTime/60000)%60) );
                //Log.e("CurrTime", "WTh: "+ ((waitTime/60000-((waitTime/60000)%60))/.6) );
                //Log.e("CurrTime", "M: HHmm: "+ (wakeTime) );
                Log.e("CurrTime", "A: HHmm: "+ ( (waitTime/60000-((waitTime/60000)%60))/.6+(waitTime/60000)%60 ) );
                Thread.sleep( waitTime );
            }

            Log.e("CurrTime", "P" );

            try {
                Thread.sleep(2048);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(WEATHWORK, "API call");
            Response<WeatherInfo> response = call[0].execute();
            Log.d(WEATHWORK, "API returned");
            Log.d(WEATHWORK, response.toString());
            //UseGPS==false, second break, after:API success
            //UseGPS==true, third break, after:API success
            /*try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            Log.i("APICall", "API call Success");

            Log.i("LocationGot", "ZipIn: "+Integer.toString(sharedPref.getInt("Zipcode", 95928)) );
            Log.i("LocationGot", "Town: "+response.body().getLocation().getName() );
            Log.i("LocationGot", "Sample Time: "+response.body().getCurrent().getLastUpdated() );
            Log.i("LocationGot", "" );

            if (response.isSuccessful() && response.body() != null && response.body().getCurrent().getTempF() != null){
                Data output = new Data.Builder().putDouble("temperature",response.body().getCurrent().getTempF()).build();

                //trying
                double currTemp = response.body().getCurrent().getTempF();

                //save currTemp to sharedPref as "LastTemp"
                sharedPref.edit().putString("LastTemp", Double.toString(currTemp)).apply();

//                String alarmText = "";
//                        //mainViewModel.compare(response.body().getCurrent().getTempF());//,weatherInfo.getCurrent().getLastUpdated());//runs compare of list vs current and returns notification string
//
//                for(Alarm alarm:currList) {
//                    Log.e("LocationGot", "alarm.getTemperature(): "+alarm.getTemperature() );
//                    if(alarm.getHighOrLow() == 0) {//is hotter?
//                        if(alarm.getTemperature() <= currTemp) {
//                            //call alarm with: currTemp" has exceeded "alarm.getTemperature()
//                            //alarm.setHighOrLow(1);
//                            //swap those of HOL 0 below to 1
//                            alarmText = "The current Temperature of "+currTemp+"℉ has exceeded your monitored Temp of "+alarm.getTemperature()+"℉";// "+LastTime;
//                        }
//                    }else{
//                        if(alarm.getTemperature() >= currTemp) {//is colder?
//                            //call alarm with: currTemp" has fallen below "alarm.getTemperature()
//                            //alarm.setHighOrLow(0);
//                            //swap those of HOL 1 above to 0
//                            alarmText = "The current Temperature of "+currTemp+"℉ has fallen below your monitored Temp of "+alarm.getTemperature()+"℉";
//                        }
//                    }
//                }
//
//                if(!alarmText.isEmpty()) {
//                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);//getting default alarm service
//
//                    Intent alarmReceiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
//                    alarmReceiverIntent.putExtra("alarmText", alarmText);
//
//                    PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(),
//                            1
//                            , alarmReceiverIntent
//                            , PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//                    );//breaking here//fixed
//
//                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), alarmIntent);
//                }
//                //

                outputObservable.postValue(response.body());

                return Result.success();//output);
            }else{
                /*try {
                    Thread.sleep(2);
                } catch (InterruptedException x) {
                    x.printStackTrace();
                }*/
                return Result.retry();
            }
        }catch(Throwable e){
            /*try {
                Thread.sleep(2);
            } catch (InterruptedException y) {
                y.printStackTrace();
            }*/
            return Result.failure();
            //return Result.retry();
            //when UseGPS == true ^??
        }
        /*call.enqueue(new Callback<WeatherInfo>() {
            @Override
            public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                Log.e("APICall", "APICall Success");
                WeatherInfo weatherinfo= response.body();
                Log.e("APISent",Integer.toString(sharedPref.getInt("Zipcode", 95928)));
                Log.e("APICall", String.valueOf(weatherinfo.getCurrent().getTempF()));
            }

            @Override
            public void onFailure(Call<WeatherInfo> call, Throwable t) {
                Log.e("APICall", "APICall Fail");
            }
        });*/


    }
}
