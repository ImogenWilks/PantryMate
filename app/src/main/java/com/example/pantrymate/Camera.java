package com.example.pantrymate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Camera extends AppCompatActivity {
    public String visionData;
    int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap image = null;
    int port = 3000;
    EditText itemListTextView;
    String currentPhotoPath = null;
    File imageFile = null;

    //Used to filter out non food items from vision results
    ArrayList<String> foodList = new ArrayList<String>();
    //Stores results of object recognition
    ArrayList<String> visionResultsList = new ArrayList<String>();
    //Used to determin when all results have been recieved
    volatile int numOfResponses = 0;
    volatile  boolean recievedText = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodList.add("Apple");
        foodList.add("Banana");
        foodList.add("Grape");
        foodList.add("Cucumber");
        foodList.add("Pineapple");

        itemListTextView = (EditText) findViewById(R.id.itemListTextView);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void 	onPostResume()
    {
        super.onPostResume();

        Toast.makeText(this, "Test", Toast.LENGTH_LONG).show();
    }

    private File createImageFile() throws IOException {

            // Create an image file name
            String imageFileName =  "PantryMate";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void dispatchCameraIntent()
    {
        //Opens the camera in the app
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try
                {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.pantrymate.fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                    //Stores the image file outside the scope of the function so it can be deleted once the image has been processed
                    imageFile = photoFile;
                }
                catch (Exception ex)
                {
                    saveToFile(ex.toString());
                }

            }
        }
    }

    //Called when the capture button is pressed
    public void onCapturePressed(View view)
    {
        dispatchCameraIntent();
    }

    public void segmentImage(Bitmap img)
    {
        //TODO:
        //Implement code which segemnts the image


        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(img);

        numOfResponses = 0;
        recievedText = false;
        visionResultsList = new ArrayList<String>();

        detectObjects(image);
        detectText(image);

        if (img.getWidth() * img.getHeight() > 3000000)
        {
            img = Bitmap.createScaledBitmap(img, img.getWidth() / 2, img.getHeight() / 2, true);
        }

        //Bitmap test = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        int incX = img.getWidth() / 5;
        int incY = img.getHeight() / 5;

        int fileCount = 1;

        for (int x1 = 0; x1 < img.getWidth() - incX; x1 += incX)
        {
            for (int y1 = 0; y1 < img.getHeight() - incY; y1 += incY)
            {
                Bitmap currentImage = Bitmap.createBitmap(incX, incY, Bitmap.Config.ARGB_8888);
                for (int x = 0; x < currentImage.getWidth(); x++)
                {
                    for (int y = 0; y < currentImage.getHeight(); y++)
                    {
                        try
                        {
                            currentImage.setPixel(x,y, img.getPixel(x + x1,y + y1));
                        }
                        catch (Exception ex)
                        {
                            saveToFile(ex.toString());
                        }
                    }
                }
                //saveImage(Integer.toString(fileCount++) + ".png", currentImage);
                detectObjects(FirebaseVisionImage.fromBitmap(currentImage));

            }
        }

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void ...params) {

                int timer = 0;
                //Waits until its recieved responses for all image segments
                while (numOfResponses < 26 && timer < 15)
                {
                    try {
                        EditText e = (EditText) findViewById(R.id.itemListTextView);
                        CharSequence newchars = numOfResponses + " / 26";
                        e.setText(newchars);
                        Thread.sleep(1000);
                        timer++;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    catch (Exception ex)
                    {
                        saveToFile(ex.toString());
                    }
                }

                String data = "Response:";

                for (String s: visionResultsList)
                {
                    for (String currentFood: foodList)
                    {
                        if (s.contains(currentFood))
                        {
                            data += currentFood + "\n";
                        }
                    }


                }

                EditText e = (EditText) findViewById(R.id.itemListTextView);
                CharSequence newchars = e.getText() + data;
                e.setText(newchars);

                return null;
            }

        }.execute();

        imageFile.delete();
    }

    void saveImage(String fileName, Bitmap img)
    {
        //Stores the image to a file on the phone

        try {
            File f = new File(Environment.getExternalStorageDirectory().toString(), fileName);
            FileOutputStream out = new FileOutputStream(f);

            img.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            saveToFile(e.toString());
        } catch (IOException ex)
        {
            saveToFile(ex.getMessage());
        }
        catch (NullPointerException ex)
        {
            saveToFile(ex.toString());
        }


    }

    void detectObjects(FirebaseVisionImage image)
    {
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler();

        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        // Task completed successfully
                        // ...
                        //Loops through each object that was recognised

                        for (FirebaseVisionImageLabel label: labels) {
                            String text = label.getText();
                            visionResultsList.add(text);
                        }
                        numOfResponses++;
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception ex) {
                        // Task failed with an exception
                        // ...

                        numOfResponses++;
                    }
                });

    }

    void detectText(FirebaseVisionImage image)
    {
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getCloudTextRecognizer();

        //Processes the image to find text
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                // Task completed successfully
                                // ...
                                String data = "Text recognition" + firebaseVisionText.getText();

                                EditText e = (EditText) findViewById(R.id.itemListTextView);
                                CharSequence newchars = e.getText() + data;
                                e.setText(newchars);




                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception ex) {
                                        // Task failed with an exception
                                        // ...

                                        String data = "Could not find text:\n\n" + ex.toString();
                                        EditText e = (EditText) findViewById(R.id.itemListTextView);
                                        CharSequence newchars = e.getText() + data;
                                        e.setText(newchars);

                                    }
                                });

    }


    public static boolean saveToFile(String data){

        try {
            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "Visontest.txt" ).mkdir();
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Visontest.txt");
            //if (!file.exists()) {
                file.createNewFile();
            //}
            FileOutputStream fileOutputStream = new FileOutputStream(file,false);
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
            Bitmap image = BitmapFactory.decodeFile(currentPhotoPath);
            segmentImage(image);
            //saveToFile("point b");
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

