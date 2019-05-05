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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import database.DBPantry;
import database.DatabaseHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
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
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private RecyclerView nRecyclerView;
    private Adapter nAdapter;
    private RecyclerView.LayoutManager nlayoutManager;
    private DatabaseHelper db1,db;
    ArrayList<Items> itemList = new ArrayList<>();
    Button addAll,helpBut,addBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db1 = new DatabaseHelper(this, "receipt.db");
        db = new DatabaseHelper(this, "pantry.db");

        itemList = getPantry();

        nRecyclerView = findViewById(R.id.ListRecyclerView);
        nRecyclerView.setHasFixedSize(true);
        nlayoutManager = new LinearLayoutManager(this);
        nAdapter = new Adapter(itemList);
        nRecyclerView.setLayoutManager(nlayoutManager);
        nRecyclerView.setAdapter(nAdapter);

        nAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent i = new Intent(receipt.this, editCheck.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", itemList.get(position).getText1());
                bundle.putString("expiry", itemList.get(position).getText2());
                bundle.putString("quantity", itemList.get(position).getText3());
                bundle.putString("date", itemList.get(position).getDateAdded());
                bundle.putInt("Add", 2);
                bundle.putInt("pantry",4);
                bundle.putString("activity","receipt");
                i.putExtras(bundle);
                startActivity(i);
            }

        });

        addAll = (Button) findViewById(R.id.addPantry);
        addAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Items tempItem : itemList){
                    increaseQuantity(tempItem.getText1(),tempItem.getText2(),Integer.parseInt(tempItem.getText3()));
                }

                itemList.clear();
                nAdapter.notifyDataSetChanged();
                db1.deleteAll();
                Toast.makeText(receipt.this, "Added to pantry", Toast.LENGTH_SHORT).show();
            }
        });

        helpBut= (Button) findViewById(R.id.instructions);
        helpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

            }
        });

        addBut = (Button) findViewById(R.id.addItem);
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Format formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                String dateString=formatter.format(date);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.WEEK_OF_MONTH, 1);
                Date expiryDate=c.getTime();
                String expiryString=formatter.format(expiryDate);


                Intent i = new Intent(receipt.this, editCheck.class);
                Bundle bundle = new Bundle();
                bundle.putString("name","");
                bundle.putString("expiry",expiryString);
                bundle.putString("quantity","");
                bundle.putString("date","");
                bundle.putInt("Add",1);
                bundle.putInt("pantry",4);
                bundle.putString("activity","receipt");
                i.putExtras(bundle);
                startActivity(i);


            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder target, int i) {
                int position = target.getAdapterPosition();
                removeItem(itemList.get(position).getText1(),itemList.get(position).getDateAdded());
                itemList.remove(position);
                nAdapter.notifyDataSetChanged();

            }
        });
        helper.attachToRecyclerView(nRecyclerView);
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


            }

            addItem(stringBuilder.toString());
            recreate();

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
            case R.id.ShoppingList:
                Intent intentShopping = new Intent(this, ShoppingList.class);
                startActivity(intentShopping);
                return true;
            case R.id.Receipt:
                Intent intentReceipt = new Intent(this, receipt.class);
                startActivity(intentReceipt);
                return true;
            case R.id.Help:
                Intent intentHelp = new Intent(this, Help.class);
                startActivity(intentHelp);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void addItem(String items)
    {
        String[] tempItemList = items.split("\n");
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.WEEK_OF_MONTH, 1);

        Date expiryDate=c.getTime();
        String expiryString=formatter.format(expiryDate);

        for ( String item : tempItemList)
        {
            db1.insertFood(item,expiryString,1);

        }
        nAdapter.notifyDataSetChanged();


    }

    private ArrayList<Items> getPantry() {
        ArrayList<Items> itemList = new ArrayList<>();
        // returns a list of the pantry
        // use pantryList[x].getVariableName() to get values from the list
        List<DBPantry> pantryList = db1.fetchPantryAll();

        for (DBPantry tempPantry : pantryList) {
            // adds the new food item to the item list
            itemList.add(new Items(tempPantry.getName(),tempPantry.getDateExpiry(),Integer.toString(tempPantry.getAmount()), tempPantry.getDateAdded()));
        }

        return itemList;
    }

    private void removeItem(String foodName, String dateAdded) {
        //removes the item from the table, pass food name and dateadded
        db1.deleteFood(foodName, dateAdded);

    }

    public boolean increaseQuantity(String foodName, String expiry, int amount) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateString = formatter.format(date);
        List<DBPantry> pantryList;
        boolean found = false;

        pantryList = db.fetchPantryAll();

        for (DBPantry tempPantry : pantryList) {
            if (tempPantry.getName().equals(foodName) && tempPantry.getDateExpiry().substring(0, 10).equals(expiry)) {
                DBPantry tempP = new DBPantry();
                tempP.setName(foodName);
                tempP.setDateExpiry(expiry);
                tempP.setAmount(tempPantry.getAmount() + amount);
                db.updateFood(tempP, foodName, tempPantry.getDateAdded());
                found = true;
            }
        }
        if (!found) {
            db.insertFood(foodName, expiry, amount);
        }
        return found;
    }

    public void openDialog() {
        receiptDialog addDialogue = new receiptDialog();
        addDialogue.show(getSupportFragmentManager(),"help");

    }
}






