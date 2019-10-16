package com.example.photogalleryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

import static com.example.photogalleryapp.MainActivity.decodeFile;
import static com.example.photogalleryapp.MainActivity.photoGallery;

public class GalleryActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        gridLayout = findViewById(R.id.gallery_grid);

        for (File image: photoGallery) {
            ImageView iv = new ImageView(GalleryActivity.this);
            iv.setImageBitmap(decodeFile(image));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    200,
                    200
            );
            iv.setLayoutParams(lp);
            iv.setPadding(5,5,5,5);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            gridLayout.addView(iv);
        }
    }
}
