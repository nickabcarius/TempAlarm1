package com.ata.tempalarm1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ata.tempalarm1.Data.Alarm;
import com.ata.tempalarm1.databinding.ActivityAlarmInputBinding;
import com.ata.tempalarm1.databinding.ActivityMainBinding;

import java.io.Serializable;


public class AlarmInputActivity extends AppCompatActivity {
    private ActivityAlarmInputBinding binding;
    private AlarmInputViewModel alarmInputViewModel;
    private Alarm pAlarm;

    @Override //Overriding the AppCompactActivity which is the android class that has a function onCreate that represents the view lifecycle
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        alarmInputViewModel = new ViewModelProvider(this).get(AlarmInputViewModel.class);//passing the class

        binding = ActivityAlarmInputBinding.inflate(getLayoutInflater());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(binding.getRoot());

        binding.saveToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //binding.H2CS.isChecked()

                int H2CVal;

                if(!binding.H2CS.isChecked()){//if not checked AKA 0 AKA Hotter
                    H2CVal = Integer.parseInt(binding.H2CS.getTextOff().toString());//0
                }else{//if checked AKA 1 AKA Colder
                    H2CVal = Integer.parseInt(binding.H2CS.getTextOn().toString());//1
                }

                pAlarm = alarmInputViewModel.addAlarm(getApplicationContext(), Integer.parseInt(binding.editTextTempSigned.getText().toString()),H2CVal);

                finish();//go back to main
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmInputActivity.super.finish();
            }
        });

    }

    @Override
    public void finish(){
        Intent intent=new Intent();
        intent.putExtra("alarm", (Serializable) pAlarm);
        setResult(RESULT_OK, intent);

        super.finish();
    }
}
