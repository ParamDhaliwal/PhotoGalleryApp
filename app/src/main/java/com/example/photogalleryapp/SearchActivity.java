package com.example.photogalleryapp;

import android.graphics.BitmapFactory;
import android.media.ExifInterface;
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
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.example.photogalleryapp.MainActivity.decodeFile;

public class SearchActivity extends AppCompatActivity {

    private Button btnSearch;
    private Date fDate;
    private Date tDate;
    private LinearLayout searchResultDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchResultDisplay = findViewById(R.id.search_result);

        btnSearch = findViewById(R.id.search_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPhoto(filterPictures());
            }
        });
    }

    public ArrayList<File> filterPictures()
    {
        ArrayList<File> searchResult = null;
        EditText fromDate = findViewById(R.id.search_fromDate);
        EditText toDate   = findViewById(R.id.search_toDate);
        EditText latitude = findViewById(R.id.search_latitude);
        EditText longitude = findViewById(R.id.search_longitude);
        String lat = latitude.getText().toString();
        String lng = longitude.getText().toString();
        String fdate = fromDate.getText().toString();
        String tdate = toDate.getText().toString();

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] allImages = null;

        if (dir.exists()) {
            allImages = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpg"));
                }
            });
        }

        assert allImages != null;

        if(!fdate.matches("") && !tdate.matches(""))
        {
            return filterPicturesByDate(fdate, tdate, allImages);
        }
        else if(!lat.matches("") && !lng.matches(""))
        {
            return filterPicturesByLocation(lat, lng, allImages);
        }
        else
        {
            return new ArrayList<>(Arrays.asList(allImages));
        }

    }

    public ArrayList<File> filterPicturesByDate(String fromDate, String toDate, File[] images) {
        ArrayList<File> searchResult = new ArrayList<>();
        Date imageDate;
        try {
            fDate = new SimpleDateFormat("yy/MM/dd").parse(fromDate);
            tDate = new SimpleDateFormat("yy/MM/dd").parse(toDate);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }

        for(File image: images) {
            try {
                String[] parts = image.getName().split("_");
                String imageFileName = parts[1];
                String caption = parts[2];
                imageDate = new SimpleDateFormat("yyMMdd").parse(imageFileName);
                Log.d("imageDate = ", imageDate.toString());
                if (imageDate.compareTo(fDate) >= 0 && imageDate.compareTo(tDate) <= 0) {
                    Log.d("Selected image: ", image.getName());
                    searchResult.add(image);
                }
            } catch (ParseException ex) {
                Log.v("Exception", ex.getLocalizedMessage());
            }
        }
        return searchResult;
    }

    public ArrayList<File> filterPicturesByLocation(String latitude, String longitude, File[] images)
    {
        ArrayList<File> searchResult = new ArrayList<>();
        ExifInterface exif;
        String image_lat, image_lng;

        for(File image: images) {
            try {
                exif = new ExifInterface(image.getAbsolutePath());
                image_lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                image_lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                if (latitude.equals(image_lat) && longitude.equals(image_lng)) {
                    searchResult.add(image);
                }

            } catch (IOException ex) {
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
            iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            iv.setImageBitmap(decodeFile(result));
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