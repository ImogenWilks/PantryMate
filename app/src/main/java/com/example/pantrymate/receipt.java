package com.example.pantrymate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Date;

import static android.provider.MediaStore.Images.Media.getBitmap;
import static java.security.AccessController.getContext;


public class receipt extends AppCompatActivity {

    EditText mtextView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath = null;
    File imageFile = null;
    Bitmap bitmap = null;
    Uri resultUri;
    Bitmap image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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

    public void dispatchCameraIntent() {
        //Opens the camera in the app
        Intent takePicuteintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePicuteintent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.pantrymate.fileprovider",
                            photoFile);
                    takePicuteintent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePicuteintent, REQUEST_IMAGE_CAPTURE);

                    imageFile = photoFile;

                } catch (Exception ex) {
                    saveToFile(ex.toString());
                }
            }
        }
    }

    public void Startcapture(View view) {
        dispatchCameraIntent();
    }

    private static final String TAG = "receipt";

    private static final int requestPermissionID = 101;

    public static boolean saveToFile( String data){

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
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                }
                catch (Exception ex) {
                }

                textrecogniser(image);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            cropper(imageFile);

        }
    }

   public void cropper(File ImageFile) {

       Uri imageUri =  FileProvider.getUriForFile(this, "com.example.pantrymate.fileprovider", imageFile);

     // start cropping activity for pre-acquired image saved on the device
       CropImage.activity(imageUri)
               .start(this);

    }

    void textrecogniser(Bitmap image) {

        //Create the TextRecognizer
        TextRecognizer textRecogniser = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecogniser.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            Frame imageFrame = new Frame.Builder().setBitmap(image).build();

            StringBuilder stringBuilder = new StringBuilder();

            SparseArray<TextBlock> textBlocks = textRecogniser.detect(imageFrame);

            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));

                stringBuilder.append(textBlock.getValue()) ;
                stringBuilder.append("\n");

                mtextView = (EditText) findViewById(R.id.mtextView);
                mtextView.setText(stringBuilder.toString());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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






