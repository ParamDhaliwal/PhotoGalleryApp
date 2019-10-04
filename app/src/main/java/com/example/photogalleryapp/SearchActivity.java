package com.example.photogalleryapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SearchActivity extends AppCompatActivity {


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
    }


    public void cancel(final View v) {

    }

    public void search(final View v) {
        displayPhoto(getPictures(fromDate.getText().toString(), toDate.getText().toString()));
    }

    public ArrayList getPictures(String fromDate, String toDate) {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] allImages = {};
        try {
            fDate = new SimpleDateFormat("dd/mm/yyyy").parse(fromDate);
            tDate = new SimpleDateFormat("dd/mm/yyyy").parse(toDate);
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
                imageDate = new SimpleDateFormat("yyyyMMdd").parse(imageFileName);
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
        ImageView iv = new ImageView(this);
        searchResultDisplay = new GridLayout(this);
        for (String result: searchResults) {
            iv.setImageBitmap(BitmapFactory.decodeFile(result));
            searchResultDisplay.addView(iv);
        }
        setContentView(searchResultDisplay);
    }
}