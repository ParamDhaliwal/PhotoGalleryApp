package com.example.photogalleryapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    static final int CAMERA_REQUEST_CODE = 1;
    static final String PHOTO_FILEPROVIDER = "com.example.photogalleryapp.fileprovider";

    private String imageFileName;
    private String currentPhotoPath = null;
    private File imageFile;
    private int currentPhotoIndex = 0;
    private File currentPhoto = null;
    public static ArrayList<File> photoGallery;

    private Button btnFilter;
    private ImageView ivMain;
    private EditText caption;
    private TextView timeStamp;
    private File tmpNewFile;
    private ExifInterface exifInterface;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivMain = findViewById(R.id.ivMain);
        caption = (EditText) findViewById(R.id.captionBox);
        timeStamp = (TextView) findViewById(R.id.timeStampDisplay);

        try {
            photoGallery = populateGallery();

            if (!photoGallery.isEmpty()) {
                displayPhoto(this.photoGallery.get(this.photoGallery.size()-1));
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            
        }

        btnFilter = (Button)findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        ivMain.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "Gallery Open", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, GalleryActivity.class));
               // startActivityForResult(new Intent(MainActivity.this, GalleryActivity.class), 1);
            }
            @Override
            public void onSwipeRight() {
                //Toast.makeText(MainActivity.this, "SWIPE RIGHT", Toast.LENGTH_SHORT).show();
                ++currentPhotoIndex;
                if (currentPhotoIndex < 0)
                    currentPhotoIndex = 0;
                if (currentPhotoIndex >= photoGallery.size())
                    currentPhotoIndex = photoGallery.size() - 1;
                currentPhoto = photoGallery.get(currentPhotoIndex);
                displayPhoto(currentPhoto);
            }
            @Override
            public  void onSwipeLeft() {
                //Toast.makeText(MainActivity.this, "SWIPE LEFT", Toast.LENGTH_SHORT).show();
                --currentPhotoIndex;
                if (currentPhotoIndex < 0)
                    currentPhotoIndex = 0;
                if (currentPhotoIndex >= photoGallery.size())
                    currentPhotoIndex = photoGallery.size() - 1;
                currentPhoto = photoGallery.get(currentPhotoIndex);
                displayPhoto(currentPhoto);
            }
        });
    }


    private ArrayList<File> populateGallery() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        ArrayList<File> gallery = new ArrayList<>();
        File[] allImages = null;
        if (dir.exists()) {
            allImages = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpg"));
                }
            });
        }

        assert allImages != null;
        for(File image: allImages) {
            gallery.add(image);
        }
        return gallery;
    }

    /**
     * Displays a image file
     * @param image
     */
    private void displayPhoto(File image) {
        ImageView iv = findViewById(R.id.ivMain);
        iv.setImageBitmap(decodeFile(image));
        iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        currentPhotoPath = image.getPath();
        currentPhoto = image;

        String theCaption = "";
        String theTimeStamp = "";
        int tmp = image.getName().indexOf('_', 0);

        SimpleDateFormat inf = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat outf = new SimpleDateFormat("MMM dd, yyyy");
        Date date = null;

        theTimeStamp = image.getName().substring(tmp + 1, tmp + 7);

        try {
            date = inf.parse(theTimeStamp);
            theTimeStamp = outf.format(date);
        } catch (ParseException e) {}

        int usFirst = image.getName().indexOf('_', 5);
        int usLast = image.getName().indexOf('_', usFirst + 1);
        try {
            if (image.getName().indexOf('~') < 0) {
                theCaption = image.getName().substring(usFirst + 1, usLast);
            }
        } catch (StringIndexOutOfBoundsException e) {}

        caption.setText(theCaption);
        timeStamp.setText(theTimeStamp);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (tmpNewFile.length() == 0) {
            tmpNewFile.delete();
            tmpNewFile = null;
        }

        if (resultCode == RESULT_OK && tmpNewFile != null && tmpNewFile.length() != 0) {
            currentPhoto = tmpNewFile;
            currentPhotoPath = currentPhoto.getPath();
            photoGallery.add(currentPhoto);
            displayPhoto(currentPhoto);
        }
    }

    /**
     * Handles a phone Camera app actions
     * @throws IOException If an input or output exception occurred.
     */
    public void takePicture(View v) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            tmpNewFile = createImageFile();
            if (tmpNewFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        PHOTO_FILEPROVIDER,
                        tmpNewFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    /**
     * Creates a new image file.
     * @throws IOException  If an input or output exception occurred.
     * @return imageFile
     */
    private File createImageFile() throws IOException {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        this.setImageFileName(new SimpleDateFormat("yyMMdd").format(new Date()));
        this.setImageFile(File.createTempFile(this.getImageFileName(), ".jpg", dir));
        this.currentPhotoPath = this.getImageFile().getAbsolutePath();
        Log.d("createImageFile", currentPhotoPath); // Ignore: Log file update
        return this.imageFile;
    }

    public void savingCaption(View v) {
        File pic = null;

        pic = photoGallery.get(currentPhotoIndex);
        try {
            exifInterface = new ExifInterface(pic.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);
            Toast.makeText(MainActivity.this, "Permissions main", Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        String provider = locationManager.getBestProvider(new Criteria(), true);


        this.location = locationManager.getLastKnownLocation(provider);

        if (this.location != null) {
            Toast.makeText(MainActivity.this, "My Location " + this.location, Toast.LENGTH_SHORT).show();

            if (this.location.getLatitude() > 0)
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
            else
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
            if (this.location.getLongitude() > 0)
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
            else
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");

            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, dec2DMS(this.location.getLongitude()));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, dec2DMS(this.location.getLatitude()));
            try {
                exifInterface.saveAttributes();
            } catch ( IOException e) {
                e.printStackTrace();
            }
        }

        File tmpFile = null;
        String newName = "";
        String lastHalf = "";

        int usFirst = pic.getName().indexOf('_', 5);
        int usLast = pic.getName().indexOf('_', usFirst + 1);

        newName = pic.getName().substring(0, pic.getName().indexOf('_', 1))
                + pic.getName().substring(pic.getName().indexOf('_',
                1), pic.getName().indexOf('_', 5) + 1);
        lastHalf = pic.getName().substring(pic.getName().lastIndexOf('_'));

        if (!caption.getText().toString().isEmpty()) {
            newName = newName + caption.getText().toString() + lastHalf;
        } else if (caption.getText().toString().isEmpty()){
            newName = newName + "~" + lastHalf;
        }

        String zePath = pic.getPath().substring(0, pic.getPath().lastIndexOf('/') + 1);
        tmpFile = new File(zePath + newName);
        pic.renameTo(tmpFile);
    }

    private String dec2DMS(double coord) {
        coord = coord > 0 ? coord : -coord;  // -105.9876543 -> 105.9876543
        String sOut = Integer.toString((int)coord) + "/1,";   // 105/1,
        coord = (coord % 1) * 60;         // .987654321 * 60 = 59.259258
        sOut = sOut + Integer.toString((int)coord) + "/1,";   // 105/1,59/1,
        coord = (coord % 1) * 60000;             // .259258 * 60000 = 15555
        sOut = sOut + Integer.toString((int)coord) + "/1000";   // 105/1,59/1,15555/1000
        return sOut;
    }

    /**
     * Gets the name of the image file
     * @return imageFileName
     */
    private String getImageFileName() {
        return this.imageFileName;
    }

    /**
     * Sets a name for the image file
     * @param timeStamp
     */
    private void setImageFileName(String timeStamp) {
        this.imageFileName = "JPEG_" + timeStamp + "_~_";
    }

    /**
     * Gets the image file
     * @return imageFile
     */
    private File getImageFile(){
        return this.imageFile;
    }

    /**
     * Sets the image file
     * @param imageFile
     */
    private void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public static Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    private class MyLocationListener implements LocationListener {
        private double longitude;
        private double latitude;
        @Override
        public void onLocationChanged(Location location) {
            this.longitude = location.getLongitude();
            this.latitude = location.getLatitude();
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public double getLongitude() {
            return this.longitude;
        }

        public double getLatitude() {
            return this.latitude;
        }
    }
}
