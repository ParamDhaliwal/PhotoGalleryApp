package com.example.photogalleryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private ArrayList<String> photoGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridLayout = findViewById(R.id.gallery_grid);
        photoGallery = populateGallery();

        for (String imagePath: photoGallery) {
            ImageView iv = new ImageView(GalleryActivity.this);
            iv.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(400, 400);
            lp.setMargins(10,10,10,10);
            iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            iv.setLayoutParams(lp);
            gridLayout.addView(iv);
        }
    }

    private ArrayList<String> populateGallery() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        ArrayList<String> gallery = new ArrayList<>();
        if (dir.exists()) {
            for (File image: (dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpg"));
                }
            }))) {
                gallery.add(image.getAbsolutePath());
            }
        }
        return gallery;
    }
}
