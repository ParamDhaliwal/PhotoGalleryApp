package com.example.photogalleryapp;

import android.app.DatePickerDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SearchActivity extends AppCompatActivity {

    private Button btnSearch;
    private EditText fromDate;
    private EditText toDate;
    private Calendar fromCalendar;
    private Calendar toCalendar;
    private DatePickerDialog.OnDateSetListener fromListener;
    private DatePickerDialog.OnDateSetListener toListener;
    private Date fDate;
    private Date tDate;
    private GridLayout searchResultDisplay;

    private ArrayList<String> searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fromDate = (EditText) findViewById(R.id.search_fromDate);
        toDate   = (EditText) findViewById(R.id.search_toDate);

        searchResultDisplay = findViewById(R.id.search_result);

        btnSearch = findViewById(R.id.search_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPhoto(getPictures(fromDate.getText().toString(), toDate.getText().toString()));
            }
        });
    }


    public void cancel(final View v) {

    }

    public void search(final View v) {
        displayPhoto(getPictures(fromDate.getText().toString(), toDate.getText().toString()));
    }

    public ArrayList<String> getPictures(String fromDate, String toDate) {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] allImages = {};
        try {
            fDate = new SimpleDateFormat("yy/MM/dd").parse(fromDate);
            tDate = new SimpleDateFormat("yy/MM/dd").parse(toDate);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }

        searchResult = new ArrayList<>();
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
                    searchResult.add(image.getAbsolutePath());
                }
            } catch (ParseException ex) {
                Log.v("Exception", ex.getLocalizedMessage());
            }
        }
        return searchResult;
    }

    private void displayPhoto(ArrayList<String> searchResults) {
        for (String result: searchResults) {
            ImageView iv = new ImageView(SearchActivity.this);
            Log.d("displayPhoto====>", result);
            iv.setImageBitmap(BitmapFactory.decodeFile(result));
            searchResultDisplay.addView(iv);
        }
    }
}