package com.example.pantrymate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    private Button edit;
    private Button camera;
    private Button barcode;
    private Button help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openEdit();
            }
        });

        help= (Button) findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openHelp();
            }
        });

        camera = (Button) findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openCamera();
            }
        });

        barcode = (Button) findViewById(R.id.barcode);
        barcode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openBarcode();
            }
        });


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
}
