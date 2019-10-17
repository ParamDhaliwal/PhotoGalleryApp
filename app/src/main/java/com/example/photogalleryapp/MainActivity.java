package com.example.photogalleryapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private TextView caption;
    private TextView timeStamp;
    private File tmpNewFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivMain = findViewById(R.id.ivMain);
        caption = (TextView) findViewById(R.id.captionDisplay);
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

        for (int i = 0; i < photoGallery.size(); ++i) {
            if (photoGallery.get(i).getPath().equals(currentPhotoPath)) {
                pic = photoGallery.get(i);
                break;
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
}
