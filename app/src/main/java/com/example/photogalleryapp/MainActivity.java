package com.example.photogalleryapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
<<<<<<< HEAD
import androidx.core.app.ActivityCompat;
=======
>>>>>>> 2e50344d33fd74b2e033db32973d3946d26154b4
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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
    public static final int GPS_REQUEST = 1001;
    public static final int LOCATION_REQUEST = 1000;
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
    private TextView captionDisplay;
    private TextView timeStamp;
    private File tmpNewFile;
    private ExifInterface exifInterface;

    private double wayLatitude = 0.0, wayLongitude = 0.0;

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private boolean isGPS = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        getLocation();

        ivMain = findViewById(R.id.ivMain);
        caption = (EditText) findViewById(R.id.captionBox);
        timeStamp = (TextView) findViewById(R.id.timeStampDisplay);
        captionDisplay = (TextView) findViewById(R.id.captionDisplay);


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
            }
            @Override
            public void onSwipeRight() {
                ++currentPhotoIndex;
                if (currentPhotoIndex < 0)
                    currentPhotoIndex = 0;
                if (currentPhotoIndex >= photoGallery.size())
                    currentPhotoIndex = photoGallery.size() - 1;
                currentPhoto = photoGallery.get(currentPhotoIndex);
                displayPhoto(currentPhoto);
            }
            @Override
            public void onSwipeLeft() {
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
    private void displayPhoto(final File image) {
        final ImageView iv = findViewById(R.id.ivMain);
        ViewTreeObserver vto = iv.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                iv.getViewTreeObserver().removeOnPreDrawListener(this);
                final int ivHeight = iv.getMeasuredHeight();
                final int ivWidth = iv.getMeasuredWidth();
                iv.setImageBitmap(decodeSampledBitmapFromFile(image, ivWidth, ivHeight));
                return true;
            }
        });
        iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        currentPhotoPath = image.getPath();
        currentPhoto = image;
        try {
            ExifInterface exif = new ExifInterface(image.getAbsolutePath());
            String theCaption = exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
            if (theCaption != null && !theCaption.isEmpty()) {
                captionDisplay.setText(theCaption);
            }
            else {
                captionDisplay.setText("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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



        timeStamp.setText(theTimeStamp);
        caption.setText("");
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(File image,
                                                         int fileImgWidth, int fileImgHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, fileImgWidth, fileImgHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(image.getAbsolutePath(), options);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && tmpNewFile != null && tmpNewFile.length() != 0) {
            getLocation();
            if (tmpNewFile.length() == 0) {
                tmpNewFile.delete();
                tmpNewFile = null;
            }
            currentPhoto = tmpNewFile;
            currentPhotoPath = currentPhoto.getPath();
            photoGallery.add(currentPhoto);
            displayPhoto(currentPhoto);
            if (requestCode == GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void savingCaption(View v) {
        File pic = null;
        ExifInterface exif;

        while (!isGPS) {
            Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
        }

        pic = photoGallery.get(currentPhotoIndex);
        try {
            exifInterface = new ExifInterface(pic.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(MainActivity.this, "My Location=> Lat: " + this.wayLatitude + " | Long: " + this.wayLongitude, Toast.LENGTH_SHORT).show();

        if (this.wayLatitude > 0)
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
        else
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
        if (this.wayLongitude > 0)
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
        else
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");

        exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, dec2DMS(this.wayLongitude));
        exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, dec2DMS(this.wayLatitude));
        try {
            exifInterface.saveAttributes();
        } catch ( IOException e) {
            e.printStackTrace();
        }


        File tmpFile = null;
        String newName = "";
        String lastHalf = "";

            if(!caption.getText().toString().matches(""))
            {
                exif.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, caption.getText().toString());
                exif.saveAttributes();
                String theCaption = exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);

                if (theCaption != null && !theCaption.isEmpty()) {
                    captionDisplay.setText(theCaption);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

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
        this.imageFileName = "JPEG_" + timeStamp + "_";
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

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST);
        } else {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                }
            });
        }
    }
}
