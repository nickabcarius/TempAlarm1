package com.ata.tempalarm1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ata.tempalarm1.databinding.ActivityMainBinding;
import com.ata.tempalarm1.databinding.ActivitySettingsBinding;

import java.io.Serializable;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
ActivitySettingsBinding binding;
    String[] times = {"1am","2am","3am","4am","5am","6am","7am","8am","9am","10am","11am","12pm","1pm","2pm","3pm","4pm","5pm","6pm","7pm","8pm","9pm","10pm","11pm","12am"};
    @Override //Overriding the AppCompactActivity which is the android class that has a function onCreate that represents the view lifecycle
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(binding.getRoot());
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        binding.zipEditText.setText(Integer.toString(sharedPref.getInt("Zipcode",95928)));
        binding.Z2GS.setChecked(sharedPref.getBoolean("UseGPS",false));
        binding.replaceZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref.edit().putInt("Zipcode", Integer.parseInt(binding.zipEditText.getText().toString())).apply();
                sharedPref.edit().putBoolean("UseGPS",binding.Z2GS.isChecked()).apply();
                finish();
            }
        });
        binding.Z2GS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPref.edit().putBoolean("UseGPS",b).apply();
                /*if(sharedPref.getBoolean("UseGPS",false)){
                    locationPermissionRequest.launch(new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                            //,Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    });
                }*/
            }
        });
        Spinner spinW = (Spinner) findViewById(R.id.spinnerW);
        Spinner spinS = (Spinner) findViewById(R.id.spinnerS);
        ArrayAdapter<String> adapterW = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, times);
        ArrayAdapter<String> adapterS = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, times);
        adapterW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinW.setAdapter(adapterW);
        spinS.setAdapter(adapterS);
        spinW.setOnItemSelectedListener(this);
        spinS.setOnItemSelectedListener(this);

        binding.spinnerW.setSelection(((sharedPref.getInt("WakeTime",1000))/100)-1);
        binding.spinnerS.setSelection(((sharedPref.getInt("SleepTime",2000))/100)-1);

        /*binding.spinnerW.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected Time: "+times[position] ,Toast.LENGTH_SHORT).show();
                sharedPref.edit().putInt("WakeTime", (Integer.parseInt(times[position]))*100).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected Time: "+times[position] ,Toast.LENGTH_SHORT).show();
                sharedPref.edit().putInt("SleepTime", (Integer.parseInt(times[position]))*100).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.super.finish();
            }
        });

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
        //Toast.makeText(getApplicationContext(), "Selected Time: "+times[position] ,Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        if (parent.getId() == R.id.spinnerW){
            sharedPref.edit().putInt("WakeTime", (position+1)*100).apply();
        }
        if (parent.getId() == R.id.spinnerS){
            sharedPref.edit().putInt("SleepTime", (position+1)*100).apply();
        }
        //sharedPref.edit().putInt("WakeTime", (Integer.parseInt(times[position]))*100).apply();
        //sharedPref.edit().putInt("SleepTime", (Integer.parseInt(times[position]))*100).apply();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }


}
