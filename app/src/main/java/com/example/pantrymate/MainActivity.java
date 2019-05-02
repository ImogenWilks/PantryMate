package com.example.pantrymate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private Button edit;
    private Button camera;
    private Button barcode;
    private Button help;
    private Button shopping;
    private Button receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openEdit();
            }
        });

        help= findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openHelp();
            }
        });

        camera = findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openCamera();
            }
        });

        barcode = findViewById(R.id.barcode);
        barcode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openBarcode();
            }
        });

        shopping = findViewById(R.id.shopping);
        shopping.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openShopping();
            }
        });

        receipt = findViewById(R.id.receiptBtn);
        receipt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openReceipt();
            }
        });

        Intent intent = new Intent(this,AlarmReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(this,createID(),intent,0);

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.HOUR_OF_DAY,12);



        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), AlarmManager.INTERVAL_DAY,broadcast);

    }

    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.UK).format(now));
        return id;
    }

    public void openEdit(){
        Intent intent = new Intent(this,Edit.class);
        startActivity(intent);
    }

    public void openCamera(){
        Intent intent = new Intent(this,Camera.class);
        startActivity(intent);
    }

    public void openBarcode(){
        Intent intent = new Intent(this,barcode.class);
        startActivity(intent);
    }
    public void openHelp(){
        Intent intent = new Intent(this,Help.class);
        startActivity(intent);
    }

    public void openReceipt(){
        Intent intent = new Intent(this,receipt.class);
        startActivity(intent);
    }

    public void openShopping(){
        Intent intent = new Intent(this,ShoppingList.class);
        startActivity(intent);
    }
}
