package com.example.pantrymate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStreamWriter;
import java.net.*;
import java.util.List;

public class Camera extends AppCompatActivity {
    public String visionData;
    int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap image = null;
    int port = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        writeToFile("Is the file being written?", getApplicationContext());
        Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
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
        //saveToFile("Testing file is getting written");
        writeToFile("Is the file being written?", getApplicationContext());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    //Called when the capture button is pressed
    public void onCapturePressed(View view)
    {
        testCamera();
    }

    void testVisionObjectDetection(Bitmap img)
    {
        //writeToFile("Is the function being called?", getApplicationContext());

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(img);

        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setWidth(480)   // 480x360 is typically sufficient for
                .setHeight(360)  // image recognition
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(0)
                .build();

        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler();

// Or, to set the minimum confidence required:
// FirebaseVisionCloudImageLabelerOptions options =
//     new FirebaseVisionCloudImageLabelerOptions.Builder()
//         .setConfidenceThreshold(0.7f)
//         .build();
// FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
//     .getCloudImageLabeler(options);

        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        // Task completed successfully
                        // ...
                        String data = "";
                        for (FirebaseVisionImageLabel label: labels) {
                            String text = label.getText();
                            String entityId = label.getEntityId();
                            float confidence = label.getConfidence();
                            data += "Text: " + text + ", entity: " + entityId + ", confidence: " + Float.toString(confidence) + "\n";
                            //try
                            //{
                                //Connects to the server
                                /*Socket s = new Socket("192.168.0.10",port);


                                DataOutputStream outToServer = new DataOutputStream(s.getOutputStream());

                                outToServer.writeUTF(data);
                                //writeToFile(data, getApplicationContext());
                                //Toast.makeText(camera, "Test", Toast.LENGTH_SHORT).show();
                            //}
                            //catch (IOException ex)
                            //{

                            //}*/
                        }
                        Camera.saveToFile(data);
                        //Toast.makeText(this., "Test", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Task failed with an exception
                        // ...
                        writeToFile("something went wrong", getApplicationContext());
                    }
                });
    }

    private void writeToFile(String data, Context context) {
        /*try {
            File f = new File(Environment.getExternalStorageDirectory().toString(), "MLKITTest.txt");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(f);
            /*OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("MLKITTest.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }*/


    }

    public static boolean saveToFile( String data){
        try {
            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "Visontest.txt" ).mkdir();
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Visontest.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());

            return true;
        }  catch(FileNotFoundException ex) {
           // Log.d(TAG, ex.getMessage());
        }  catch(IOException ex) {
           // Log.d(TAG, ex.getMessage());
        }
        return  false;


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
                        testVisionObjectDetection(image);
                        /*try
                        {

                            //sendNetworkDataTest(image);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }*/
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

