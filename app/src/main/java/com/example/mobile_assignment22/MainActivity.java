package com.example.mobile_assignment22;

import static android.content.ContentValues.TAG;
import static android.media.ThumbnailUtils.OPTIONS_RECYCLE_INPUT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Button reload;
    private Context context;
    private ExecutorService mExecutor=  Executors.newWorkStealingPool();

    private GridView mTiles;
    private MyAdapter mTileAdapter;
    private static final int NTILES=32;
    private static final int NCOLS=3;
    // for pinch to zoom
    private ScaleGestureDetector mScaleGestureDetector;
    private Cursor mCursor;
    private InputStream is;


    Uri addNewPhoto(String title, long date, String path) {
        ContentValues values = new ContentValues(5);
        values.put(MediaStore.Images.ImageColumns.TITLE, title);
        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, title +
                ".jpg");
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, date);
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.ImageColumns.DATA, path);
        Uri uri = null;
        try {
            uri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Throwable th) {
            Log.e(TAG, "Failed to write MediaStore" + th);
        }
        return uri;
    }

    private Bitmap getImageThumbnail(Bitmap bitmap) {
        return ThumbnailUtils.extractThumbnail(bitmap, 320, 320, OPTIONS_RECYCLE_INPUT);
    }

    public class MyAdapter extends BaseAdapter {


        public MyAdapter() {

        }

        class ViewHolder {
            int position;
            ImageView image;
        }


        public MyAdapter(Context c){
            context = c;
        }

        @Override
        public int getCount() {
            mCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,null,
                    null, null, MediaStore.Images.Media.DATE_ADDED);
            return mCursor.getCount();
        }
        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @SuppressLint("StaticFieldLeak")
        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup){
            ViewHolder vh;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.activity_my, viewGroup, false);
                vh=new ViewHolder();
                vh.image=convertView.findViewById(R.id.imageView);
                convertView.setTag(vh);
            } else
                vh=(ViewHolder)convertView.getTag();
                // set size to be square
                convertView.setMinimumHeight(mTiles.getWidth() / mTiles.getNumColumns());
                vh.image.setImageBitmap(null);

                mExecutor.submit(() -> {
                    mCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,null,
                            null, null, MediaStore.Images.Media.DATE_ADDED);
                    mCursor.moveToPosition(i);

                    @SuppressLint("Range") String id = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                    try {
                        is = getContentResolver().openInputStream(Uri.withAppendedPath(MediaStore.Images.Media.
                                EXTERNAL_CONTENT_URI, id));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    // make sure it isn't rotated
                    vh.image.setRotationY(0);
                    vh.image.post(() -> vh.image.setImageBitmap(getImageThumbnail(bmp)));
                    //vh.image.setImageResource(photos[i]);
                });

            return convertView;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //check for permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
        // set the number of columns in the grid
        mTiles=findViewById(R.id.gridview);
        mTiles.setNumColumns(NCOLS);
        // and the adapter for tile data
        mTileAdapter=new MyAdapter();
        mTiles.setAdapter(mTileAdapter);
        // when a tile is clicked
        mTiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MyActivity.class);
                intent.putExtra("id", i);
                startActivity(intent);
            }
        });
        // for pinch to zoom
        mScaleGestureDetector=new ScaleGestureDetector(this,new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            // must be a float so it knows if we are half way between integer values
            private float mCols = NCOLS;
            // not used
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
            // nut used
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }
            // change the columns if necessary
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                //
                mCols = mCols/ detector.getScaleFactor();
                if(mCols<1)
                    mCols=1;
                if(mCols>8)
                    mCols=8;
                mTiles.setNumColumns((int)mCols);
                // recalculate the tile heights
                for(int i=0;i<mTiles.getChildCount();i++) {
                    if (mTiles.getChildAt(i) != null) {
                        mTiles.getChildAt(i).setMinimumHeight(( (mTiles.getWidth() / (int)(mCols))));
                    }
                }
                // make sure it's redrawn
                mTiles.invalidate();
                return true;
            }
        });
        // call the ScaleGestureDetector when the view is touched
        mTiles.setOnTouchListener((View view, MotionEvent motionEvent) -> {
            mScaleGestureDetector.onTouchEvent(motionEvent);
            return false;
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }
}