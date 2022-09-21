package com.example.mobile_assignment22;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyActivity extends AppCompatActivity {

    private Context context;
    private ExecutorService mExecutor=  Executors.newWorkStealingPool();
    private Cursor mCursor;
    private InputStream is;
    private ImageView mImageview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Intent i = getIntent();

        int id2 = i.getExtras().getInt("id");

            mCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,null,
                    null, null, MediaStore.Images.Media.DATE_ADDED);
            mCursor.moveToPosition(id2);

            @SuppressLint("Range") String id = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
            try {
                is = getContentResolver().openInputStream(Uri.withAppendedPath(MediaStore.Images.Media.
                        EXTERNAL_CONTENT_URI, id));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bmp = BitmapFactory.decodeStream(is);
            // make sure it isn't rotated
            //vh.image.setImageResource(photos[i]);
            mImageview = findViewById(R.id.imageView);
            mImageview.setRotationY(0);
            mImageview.setImageBitmap(bmp);

    }
}