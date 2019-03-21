package com.company.cameraapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private ImageView imageHolder;
    private Button btnLoadImage, btnTakeImage;
    private EditText textFindImage;
    private final int requestCode = 20;
    private SQLiteOpenHelper cameraDatabaseHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cameraDatabaseHelper = new CameraDatabaseHelper(this);

        imageHolder = findViewById(R.id.captured_photo);
        btnTakeImage = findViewById(R.id.photo_button);
        btnLoadImage = findViewById(R.id.find_button);
        textFindImage = findViewById(R.id.id_text);

        btnTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, requestCode);
            }
        });

        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImageFromDB();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode == requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            imageHolder.setImageBitmap(bitmap);
            byte[] bytesOfImages = getBytes(bitmap);
            db = cameraDatabaseHelper.getWritableDatabase();
            long idValue = ((CameraDatabaseHelper) cameraDatabaseHelper).insertPictureToDB(db, bytesOfImages);
            db.close();
            Toast toast = Toast.makeText(this, "Picture is saved under ID: " + String.valueOf(idValue), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    // convert bitmap to bytes
    private byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert byte array to bitmap
    private Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private void loadImageFromDB() {
        long idValue = 0;
        try {
            idValue = Long.parseLong(textFindImage.getText().toString());
        } catch(NumberFormatException e) {
            Toast toast = Toast.makeText(this, "Not a valid ID.", Toast.LENGTH_LONG);
            toast.show();
        }
        if(idValue != 0) {
            db = cameraDatabaseHelper.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT IMAGE FROM PICTURES WHERE ID = " + idValue, null);
            if(c.moveToFirst()) {
                byte[] blob = c.getBlob(c.getColumnIndex("IMAGE"));
                c.close();
                Bitmap bitmap = getImage(blob);
                imageHolder.setImageBitmap(bitmap);
            }
            c.close();
            db.close();
        }
    }
}
