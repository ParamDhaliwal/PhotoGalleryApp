package com.example.photogalleryapp;

import android.app.KeyguardManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    static final int CAMERA_REQUEST_CODE = 1;
    static final String PHOTO_FILEPROVIDER = "com.example.photogalleryapp.fileprovider";

    private String imageFileName;
    private String currentPhotoPath = null;
    private File imageFile;
    private int currentPhotoIndex = 0;
    private ArrayList<PhotoClass> photoGallery;

    private Button btnFilter;
    private Button leftBtn;
    private Button rightBtn;
    private EditText caption;
    private TextView timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        try {
            KeyguardManager mKeyGuardManager = (KeyguardManager) ctx.getSystemService(Context.KEY)
        }
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        caption = (EditText) findViewById(R.id.captionBox);
        timeStamp = (TextView) findViewById(R.id.timeStampDisplay);

        photoGallery = populateGallery();    // Load up the gallery on startup

        if (photoGallery.size() > 0) { // in case there were no pictures taken before
            displayPhoto(photoGallery.get(photoGallery.size() - 1).getPath());    // Display latest gallery picture

            PhotoClass crtPhoto = new PhotoClass(photoGallery.get(photoGallery.size() - 1).getPath());
        }

        btnFilter = (Button)findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

    }

    private View.OnClickListener filterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivityForResult(i, SEARCH_ACTIVITY_REQUEST_CODE);
        }
    };

    private ArrayList<PhotoClass> populateGallery() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photogalleryapp/files/Pictures"); //should be changed to the package name
        ArrayList tmpGallery = new ArrayList<PhotoClass>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : file.listFiles()) {
                tmpGallery.add(new PhotoClass(f.getPath()));
            }
        }
        return tmpGallery;
    }

    /**
     * Displays a image file
     * @param path
     */
    private void displayPhoto(String path) {
        PhotoClass tmp = new PhotoClass(path);
        ImageView iv = (ImageView) findViewById(R.id.ivMain);
        iv.setImageBitmap(BitmapFactory.decodeFile(path));
        currentPhotoPath = path;
        caption.setText(tmp.getCaption());
        timeStamp.setText(tmp.getTimeStamp());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Changes the currently displayed photo to the next or the previous photo.
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                --currentPhotoIndex;
                break;
            case R.id.btnRight:
                ++currentPhotoIndex;
                break;
            default:
                break;
        }
        if (currentPhotoIndex < 0)
            currentPhotoIndex = 0;
        if (currentPhotoIndex >= photoGallery.size())
            currentPhotoIndex = photoGallery.size() - 1;

        currentPhotoPath = photoGallery.get(currentPhotoIndex).getPath();
        Log.d("photoleft, size", Integer.toString(photoGallery.size()));
        Log.d("photoleft, index", Integer.toString(currentPhotoIndex));
        displayPhoto(currentPhotoPath);
    }

    public void scrolling(View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                --currentPhotoIndex;
                break;
            case R.id.btnRight:
                ++currentPhotoIndex;
                break;
            default:
                break;
        }
        if (currentPhotoIndex < 0)
            currentPhotoIndex = 0;
        if (currentPhotoIndex >= photoGallery.size())
            currentPhotoIndex = photoGallery.size() - 1;

        currentPhotoPath = photoGallery.get(currentPhotoIndex).getPath();
        Log.d("photoleft, size", Integer.toString(photoGallery.size()));
        Log.d("photoleft, index", Integer.toString(currentPhotoIndex));
        displayPhoto(currentPhotoPath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            photoGallery.add(new PhotoClass(currentPhotoPath));
            displayPhoto(currentPhotoPath);
        }
    }

    /**
     * Handles a phone Camera app actions
     * @throws IOException If an input or output exception occurred.
     */
    public void takePicture(View v) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        PHOTO_FILEPROVIDER,
                        photoFile);
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
        this.setImageFileName(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        this.setImageFile(File.createTempFile(this.getImageFileName(), ".jpg", dir));
        this.currentPhotoPath = this.getImageFile().getAbsolutePath();
        Toast.makeText(this, dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        Log.d("createImageFile", currentPhotoPath); // Ignore: Log file update
        return this.imageFile;
    }

    public void savingCaption(View v) { // Appends caption to file name after a ~
        PhotoClass tmp = null;
        for (int i = 0; i < photoGallery.size(); ++i) {
            if (photoGallery.get(i).getPath().equals(currentPhotoPath)) {
                tmp = photoGallery.get(i);
                break;
            }
        }

        File pic = new File(tmp.getPath());
        File tmpFile = null;
        String pathWithoutJpg = "";

        if (pic.getPath().indexOf('~') >= 0) {
            pathWithoutJpg = tmp.getPath().substring(0, tmp.getPath().lastIndexOf("~") + 1);
            tmpFile = new File(pathWithoutJpg + caption.getText().toString() + ".jpg");
            Toast.makeText(this, tmpFile.getPath(), Toast.LENGTH_LONG).show();
        } else {
            pathWithoutJpg = tmp.getPath().substring(0, tmp.getPath().lastIndexOf("."));
            tmpFile = new File(pathWithoutJpg + "~" + caption.getText().toString() + ".jpg");
        }

        if (!caption.getText().toString().isEmpty()) {
            pic.renameTo(tmpFile);
            tmp.setFileName(pic.getName());
            tmp.setPath(pic.getPath());
            tmp.setCaption(caption.getText().toString());
        }
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
}
