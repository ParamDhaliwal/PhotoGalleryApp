package com.example.photogalleryapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SearchActivity extends AppCompatActivity {

    private Button btnSearch;
    private EditText fromDate;
    private EditText toDate;
    private Date fDate;
    private Date tDate;
    private LinearLayout searchResultDisplay;

    private ArrayList<File> searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fromDate = findViewById(R.id.search_fromDate);
        toDate   = findViewById(R.id.search_toDate);

        searchResultDisplay = findViewById(R.id.search_result);

        btnSearch = findViewById(R.id.search_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPhoto(getPictures(fromDate.getText().toString(), toDate.getText().toString()));
            }
        });
    }

    public ArrayList<File> getPictures(String fromDate, String toDate) {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] allImages = null;

        searchResult = new ArrayList<>(); // stores all images results by date search and returns
        try {
            fDate = new SimpleDateFormat("yy/MM/dd").parse(fromDate);
            tDate = new SimpleDateFormat("yy/MM/dd").parse(toDate);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }

        if (dir.exists()) {
            allImages = dir.listFiles(new FilenameFilter() {
               public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpg"));
               }
            });
        }
        assert allImages != null;
        Date imageDate;
        for(File image: allImages) {
            try {
                String[] parts = image.getName().split("_");
                String imageFileName = parts[1];
                imageDate = new SimpleDateFormat("yyMMdd").parse(imageFileName);
                Log.d("imageDate = ", imageDate.toString());
                if (imageDate.compareTo(fDate) > 0 && imageDate.compareTo(tDate) < 0) {
                    Log.d("Selected image: ", image.getName());
                    searchResult.add(image);
                }
            } catch (ParseException ex) {
                Log.v("Exception", ex.getLocalizedMessage());
            }
        }
        return searchResult;
    }

    private void displayPhoto(ArrayList<File> searchResults) {
        for (File result: searchResults) {
            LinearLayout ll = new LinearLayout(SearchActivity.this);
            ImageView iv = new ImageView(SearchActivity.this);
            TextView tv = new TextView(SearchActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(400, 400);
            Log.d("displayPhoto====>", result.getName());
            // ImageView for image itself
            iv.setImageBitmap(BitmapFactory.decodeFile(result.getAbsolutePath()));
            iv.setLayoutParams(lp);
            // TextView for image name
            tv.setText(result.getName());
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.addView(iv);
            ll.addView(tv);
            searchResultDisplay.addView(ll);
        }
    }
}