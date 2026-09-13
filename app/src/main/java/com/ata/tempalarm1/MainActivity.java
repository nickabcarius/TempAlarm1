package com.ata.tempalarm1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Database;
import androidx.room.Room;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ata.tempalarm1.Data.APIClient;
import com.ata.tempalarm1.Data.Alarm;
import com.ata.tempalarm1.Data.GetDataService;
import com.ata.tempalarm1.Data.ListDAO;
import com.ata.tempalarm1.Data.MainDB;
import com.ata.tempalarm1.Data.WeatherInfo;
import com.ata.tempalarm1.Data.WeatherWorker;
import com.ata.tempalarm1.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private MainAdapter mainAdapter;



    @Override //Overriding the AppCompactActivity which is the android class that has a function onCreate that represents the view lifecycle
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //creating a new view model, based on the MainViewModel class, and storing it in the variable mainViewModel
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);//passing the class
        mainViewModel.initializeDataBase(getApplicationContext());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        DividerItemDecoration drawBox = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        drawBox.setDrawable(getApplicationContext().getResources().getDrawable(R.drawable.box));
        binding.recyclerView.addItemDecoration(drawBox);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));//this 4.011230
        //List<Alarm> emptyList = new ArrayList<Alarm>();
        //binding.recyclerView.setAdapter(new MainAdapter( emptyList));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(binding.getRoot());

//
//        import androidx.core.app.ActivityCompat;
//        import android.content.pm.PackageManager;
//        ActivityCompat.requestPermissions(this,
//                new String[]{
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_BACKGROUND_LOCATION},
//                PackageManager.PERMISSION_GRANTED);

        /*//________________________________________________

        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);//getting default alarm service
        Intent alarmReceiverIntent = new Intent(MainActivity.this,AlarmReceiver.class);
        alarmReceiverIntent.putExtra("alarmText","Here I Am");

        PendingIntent alarmIntent= PendingIntent.getBroadcast(this,(int)System.currentTimeMillis(),alarmReceiverIntent,PendingIntent.FLAG_IMMUTABLE);//breaking here

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),alarmIntent);



        NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setContentTitle("Notification");
        mBuilder.setContentText("The current Temperature of 82℉ has exceeded your monitored Temp of 80℉");
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        //mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("The current Temperature of 82℉ has exceeded your monitored Temp of 80℉"));
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel("default","default channel", NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            //mBuilder.setNotificationChannel
            mBuilder.setChannelId("default");
        }

        Notification notification = mBuilder.build();
        notification.flags|=Notification.FLAG_INSISTENT;
        mNotificationManager.notify(1,notification);


        //AlarmManager alarmManager2= (AlarmManager) getSystemService(ALARM_SERVICE);
        //if(alarmManager.canScheduleExactAlarms()){}
        //PendingIntent alarmIntent2= PendingIntent.getBroadcast(getApplicationContext(),0,new Intent(getApplicationContext(),AlarmReceiver.class),0);
        //alarmManager2.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),alarmIntent2);


        */ //________________________________________________

        //mainViewModel.updateListOfAlarms();
        mainViewModel.getListOfAlarms().observe(this,alarms -> {//this or the onResume work
            //update UI
            //onChange(ListOfAlarms)
            mainAdapter = new MainAdapter(alarms,getApplicationContext());
            binding.recyclerView.setAdapter(mainAdapter);
        });

        //setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        binding.zipText.setText(Integer.toString(sharedPref.getInt("Zipcode",95928)));
        //code to update zipText on press of replaceZip
        binding.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);

            }
        });
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION
                //,Manifest.permission.ACCESS_COARSE_LOCATION
                //,Manifest.permission.ACCESS_BACKGROUND_LOCATION
        });


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), AlarmInputActivity.class);
                //startActivityForResult(intent,RESULT_OK);
                startActivity(intent);
                //startService(intent);
            }
        });

        Log.e("pre1", mainViewModel.getListOfAlarms().toString());
        Constraints constraint = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();//set a constraint to be sure the device is connected to a network
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(WeatherWorker.class,
                15/* technically tied to specific phone's power management service 10/15 min, variable later*/, TimeUnit.MINUTES).addTag("MainActivity")
                .setConstraints(constraint).setBackoffCriteria(BackoffPolicy.LINEAR,//if something goes wrong in the api call, it automatically takes the difference in time and handles it by itself
                        PeriodicWorkRequest.MIN_BACKOFF_MILLIS,TimeUnit.MILLISECONDS).build();
        WorkManager mWorkManager = WorkManager.getInstance(getApplicationContext());//the instance of the WorkManager directly in the MainActivity instead of the ViewModel

        //WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("getTemperature",
        mWorkManager.enqueueUniquePeriodicWork("getTemperature",
                ExistingPeriodicWorkPolicy.KEEP,periodicWorkRequest);//@24:
        mWorkManager.getWorkInfosByTagLiveData("MainActivity").observe(this,listOfWorkInfo->{
            if(listOfWorkInfo == null || listOfWorkInfo.isEmpty()){
                return;
            }
            WorkInfo workInfo = listOfWorkInfo.get(0);//get first item of array list
            Log.e("work manager", workInfo.getState().toString());
            switch(workInfo.getState()){
                case ENQUEUED:
                    break;
                case RUNNING:
                    WeatherWorker.outputObservable.observe(MainActivity.this, weatherInfo -> {
                        mWorkManager.getWorkInfosByTagLiveData("MainActivity").removeObservers(MainActivity.this);
                        /*NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(this);*/

                        String alarmText = mainViewModel.compare(weatherInfo.getCurrent().getTempF());//,weatherInfo.getCurrent().getLastUpdated());//runs compare of list vs current and returns notification string

                        //tryout
                        if(!alarmText.isEmpty()){
                            AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);//getting default alarm service
                            //if(alarmManager.canScheduleExactAlarms()){}
                            Intent alarmReceiverIntent = new Intent(MainActivity.this,AlarmReceiver.class);
                            alarmReceiverIntent.putExtra("alarmText",alarmText);
                            //___________________________________________

                            //___________________________________________
                            Log.e("pre1", workInfo.getState().toString());
                            PendingIntent alarmIntent= PendingIntent.getBroadcast(this,
                                    1
                                    //(int)System.currentTimeMillis()
                                    ,alarmReceiverIntent,
                                    //PendingIntent.FLAG_IMMUTABLE|
                                     PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE//missing mutability?
                                    );//breaking here//fixed


                            Log.e("post1", workInfo.getState().toString());

                            Log.e("pre2", workInfo.getState().toString());
                            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),alarmIntent);
                            Log.e("post2", workInfo.getState().toString());
                            //

                        }//to here

                    });
                    break;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Alarm rAlarm= (Alarm) data.getExtras().get("alarm");
            mainAdapter.addItem(rAlarm);
            //binding.recyclerView.getAdapter().addItem();
        }
    }

    @Override
    protected void onResume(){//an option
        super.onResume();


        mainViewModel.getListOfAlarms().observe(this, alarms ->{
            binding.recyclerView.setAdapter(new MainAdapter(alarms,getApplicationContext()));
        });

        //if(binding.recyclerView.getAdapter() != null){
        //    binding.recyclerView.getAdapter().notifyDataSetChanged();
        //}

    }
}