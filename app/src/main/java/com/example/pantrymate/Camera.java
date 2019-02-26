package com.example.pantrymate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.*;

public class Camera extends AppCompatActivity {

    int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap image = null;
    int port = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        testCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void testCamera()
    {
        //Opens the camera in the app
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    //Called when the capture button is pressed
    public void onCapturePressed(View view)
    {
        testCamera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            //Stores the image taken in a variable
            image = (Bitmap) data.getExtras().get("data");
            try
            {
                //Starts a new thread to send the image to the server
                new AsyncTask<Void,Void,Void>(){

                    @Override
                    protected Void doInBackground(Void ...params) {
                        try
                        {sendNetworkDataTest(image);}
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        return null;
                    }

                }.execute();

                //Stores the image to a file on the phone
                File f = new File(Environment.getExternalStorageDirectory().toString(), "Test.png");
                FileOutputStream out = new FileOutputStream(f);
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    public void sendNetworkDataTest(Bitmap img) throws IOException
    {
        //Connects to the server
        Socket s = new Socket("192.168.0.10",port);


        DataOutputStream outToServer = new DataOutputStream(s.getOutputStream());

        //Stores the image as an array of bytes
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        //Sends the size of the image in bytes to the server
        outToServer.writeInt(stream.size());
        //Sends the image data to the server
        outToServer.write(byteArray);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.Home:
                Intent intentHome = new Intent(this, MainActivity.class);
                startActivity(intentHome);
                return true;
            case R.id.Manual:
                Intent intentManual = new Intent(this, Edit.class);
                startActivity(intentManual);
                return true;
            case R.id.Camera:
                Intent intentCamera = new Intent(this, Camera.class);
                startActivity(intentCamera);
                return true;
            case R.id.Barcode:
                Intent intentBarcode = new Intent(this, barcode.class);
                startActivity(intentBarcode);
                return true;
            case R.id.Help:
                Intent intentHelp = new Intent(this, Help.class);
                startActivity(intentHelp);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

